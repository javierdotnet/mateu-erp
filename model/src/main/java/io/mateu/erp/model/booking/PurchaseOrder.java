package io.mateu.erp.model.booking;

import com.google.common.base.Strings;
import io.mateu.common.model.authentication.Audit;
import io.mateu.common.model.authentication.User;
import io.mateu.common.model.util.Constants;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.financials.PurchaseOrderSendingMethod;
import io.mateu.erp.model.invoicing.Charge;
import io.mateu.erp.model.mdd.CancelledCellStyleGenerator;
import io.mateu.erp.model.mdd.PurchaseOrderStatusCellStyleGenerator;
import io.mateu.erp.model.mdd.SentCellStyleGenerator;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.workflow.AbstractTask;
import io.mateu.erp.model.workflow.SendPurchaseOrdersByEmailTask;
import io.mateu.erp.model.workflow.SendPurchaseOrdersTask;
import io.mateu.erp.model.workflow.TaskStatus;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.annotations.*;
import io.mateu.ui.mdd.server.annotations.Parameter;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import io.mateu.ui.mdd.server.workflow.WorkflowEngine;
import io.mateu.ui.mdd.shared.ActionType;
import io.mateu.ui.mdd.shared.MDDLink;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by miguel on 25/2/17.
 */
@Entity
@Getter
@Setter
public class PurchaseOrder {

    @Transient
    @Ignored
    private boolean preventAfterSet;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SearchFilter
    private long id;

    @Tab("Info")
    @Embedded
    @Ignored
    private Audit audit;

    @SearchFilter
    @ListColumn
    private LocalDate start;

    @ManyToOne
    @NotNull
    @ListColumn
    private Office office;

    @ManyToOne
    @NotNull
    @ListColumn
    @SearchFilter
    @QLFilter("x.provider = true")
    private Partner provider;

    @ListColumn
    @CellStyleGenerator(CancelledCellStyleGenerator.class)
    private boolean cancelled;

    private String comment;

    @Tab("Delivering")
    @Output
    @ListColumn
    @CellStyleGenerator(SentCellStyleGenerator.class)
    private boolean sent;

    public void setSent(boolean v) {
        this.sent = v;
    }

    @Output
    @ListColumn
    private LocalDateTime sentTime;
    @Output
    @ListColumn
    private LocalDateTime responseTime;

    @NotNull
    @ListColumn
    @SearchFilter
    @CellStyleGenerator(PurchaseOrderStatusCellStyleGenerator.class)
    private PurchaseOrderStatus status;
    @ListColumn
    private String providerComment;


    @Output
    private String signature;

    @Tab("Price")
    private boolean valueOverrided;

    private double overridedValue;

    @Ignored
    private String overridedValueCalculator;


    @Output
    private boolean valued;

    @Output
    @ListColumn
    double total;

    public void setTotal(double v) {
        this.total = v;
    }

    @ManyToOne
    @NotNull
    Currency currency;

    @NotNull
    @ManyToOne
    private Currency officeCurrency;

    private double totalInOfficeCurrency;

    private double officeCurrencyExchangeRate;

    @NotNull
    @ManyToOne
    private Currency accountingCurrency;

    private double totalInAccountingCurrency;

    private double accountingCurrencyExchangeRate;

    @Output
    private String priceReport;


    @Tab("Charges")
    @OneToMany(mappedBy = "purchaseOrder")
    @Output
    private List<Charge> charges = new ArrayList<>();


    @SearchFilter(value="Service Id", field = "id")
    @NotInEditor
    @ManyToMany(mappedBy = "purchaseOrders")
    private List<Service> services = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name="purchaseorder_task",
            joinColumns=@JoinColumn(name="purchaseorders_ID"),
            inverseJoinColumns=@JoinColumn(name="sendingtasks_ID"))
    @SearchFilter(value="Task Id", field = "id")
    @NotInEditor
    private List<SendPurchaseOrdersTask> sendingTasks = new ArrayList<>();


    @Links
    public List<MDDLink> getLinks() {
        List<MDDLink> l = new ArrayList<>();
        l.add(new MDDLink("Tasks", AbstractTask.class, ActionType.OPENLIST, new Data("purchaseOrders.id", getId())));
        l.add(new MDDLink("Services", Service.class, ActionType.OPENLIST, new Data("purchaseOrders.id", getId())));
        return l;
    }


    @Action(name = "Send")
    public void sendFromEditor(UserData user, EntityManager em) throws Throwable {
        send(em, em.find(io.mateu.erp.model.authentication.User.class, user.getLogin()));
    }

    @Action(name = "Send")
    public static void sendFromList(EntityManager em, @Selection List<Data> selection, @Parameter(name = "Email") String email) throws Exception {
        SendPurchaseOrdersByEmailTask t = new SendPurchaseOrdersByEmailTask();
        t.setStatus(TaskStatus.PENDING);
        t.setMethod(PurchaseOrderSendingMethod.EMAIL);
        t.setAudit(new Audit(em.find(io.mateu.erp.model.authentication.User.class, Constants.SYSTEM_USER_LOGIN)));
        String a = email;
        for (Data d : selection) {
            PurchaseOrder po = em.find(PurchaseOrder.class, d.get("_id"));
            t.getPurchaseOrders().add(po);
            po.getSendingTasks().add(t);
            if (Strings.isNullOrEmpty(a)) a = po.getProvider().getSendOrdersTo();
        }
        t.setTo(a);
        if (!Strings.isNullOrEmpty(a)) em.persist(t);
        t.execute(em, em.find(io.mateu.erp.model.authentication.User.class, Constants.SYSTEM_USER_LOGIN));
    }



    public String createSignature() {
        String s = "error when serializing";
        try {
            Map<String, Object> m = new HashMap<>();
            m.put("provider", getProvider().getName());
            List<String> serviceSignatures = new ArrayList<>();
            for (Service sv : getServices()) {
                serviceSignatures.add(sv.createSignature());
            }
            m.put("serviceSignatures", serviceSignatures);
            s = Helper.toJson(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public void send(EntityManager em, User u) throws Throwable {

        setSignature(createSignature());

        if (!isCancelled() || getSendingTasks().size() > 0) {

            SendPurchaseOrdersTask t = null;

            t = getProvider().createTask(em, this);

            t.setOffice(getOffice());
            t.setProvider(getProvider());
            t.setStatus(TaskStatus.PENDING);
            t.setAudit(new Audit(u));

            t.setPostscript("");


            t.getPurchaseOrders().add(this);
            getSendingTasks().add(t);

            em.persist(t);

        }

    }

    public Map<String,Object> getData() {
        Map<String, Object> d = new HashMap<>();

        d.put("id", getId());
        d.put("provider", getProvider().getName());
        d.put("status", (isCancelled())?"CANCELLED":"ACTIVE");
        d.put("created", getAudit().getCreated().format(DateTimeFormatter.BASIC_ISO_DATE.ISO_DATE_TIME));
        if (getOffice() != null) d.put("office", getOffice().getName());
        d.put("sent", isSent());
        d.put("sentTime", getSentTime());
        d.put("valued", isValued());
        d.put("total", getTotal());

        List<Map<String, Object>> ls = new ArrayList<>();

        List<Service> ss = new ArrayList<>(getServices());

        Collections.sort(ss, new Comparator<Service>() {
            @Override
            public int compare(Service o1, Service o2) {
                LocalDateTime d1 = o1.getStart().atStartOfDay();
                LocalDateTime d2 = o2.getStart().atStartOfDay();
                if (o1 instanceof TransferService) d1 = ((TransferService)o1).getFlightTime();
                if (o2 instanceof TransferService) d2 = ((TransferService)o2).getFlightTime();
                return d1.compareTo(d2);
            }
        });

        for (Service s : ss) {
            Map<String, Object> ds = s.getData();
            if (isCancelled()) ds.put("status", "CANCELLED");
            ls.add(ds);
        }

        d.put("services", ls);

        return d;
    }

    public void cancel(EntityManager em) {
        if (!isCancelled()) {
            if (!isSent()) {
                setCancelled(true);
                setStatus(PurchaseOrderStatus.CONFIRMED);
            } else {
                setCancelled(true);
                setStatus(PurchaseOrderStatus.PENDING);
            }
        }
    }

    public void price(EntityManager em) {
        boolean v = false;
        double t = 0;
        if (isValueOverrided()) {
            t = getOverridedValue();
            v = true;
            setPriceReport("Used overrided value");
        }
        else {
            try {
                StringWriter sw = new StringWriter();
                t = rate(em, new PrintWriter(sw));
                setPriceReport(sw.toString());
                v = true;
            } catch (Throwable throwable) {
                String error = "" + throwable.getClass().getName() + ":" + throwable.getMessage();
                if (!error.startsWith("java.lang.Throwable") && !error.startsWith("java.lang.Exception")) throwable.printStackTrace();
                else error = error.substring(error.indexOf(":"));
                System.out.println(error);
                setPriceReport(error);
            }
        }
        setValued(v);
        setTotal(t);
    }

    private double rate(EntityManager em, PrintWriter report) throws Throwable {
        double total = 0;
        if (!isCancelled()) for (Service s : getServices()) if (!s.isCancelled()) {
            double serviceCost = s.getOverridedCostValue();
            if (!s.isCostOverrided()) serviceCost = s.rate(em, false, getProvider(), report);
            total += serviceCost;
        }
        return Helper.roundEuros(total);
    }


    @Action(name = "Price")
    public static void price(EntityManager em, @Selection List<Data> selection) {
        for (Data d : selection) {
            PurchaseOrder po = em.find(PurchaseOrder.class, d.get("_id"));
            try {
                po.price(em);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }



    @PostPersist@PostUpdate
    public void afterSet() throws Exception, Throwable {

        long finalId = getId();

        if (!isPreventAfterSet()) {
            WorkflowEngine.add(new Runnable() {
                @Override
                public void run() {

                    try {
                        Helper.transact(new JPATransaction() {
                            @Override
                            public void run(EntityManager em) throws Throwable {
                                em.find(PurchaseOrder.class, finalId).afterSet(em);
                            }
                        });
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }

                }
            });
        }


    }

    public void afterSet(EntityManager em) throws Exception, Throwable {

        System.out.println("po " + getId() + ".afterset");

        for (Service s : getServices()) {
            if (s.getEffectiveProcessingStatus() < 300) {
                s.setProcessingStatus(ProcessingStatus.PURCHASEORDERS_READY);
            }

            if (isSent() && s.getEffectiveProcessingStatus() < 400) {
                s.setProcessingStatus(ProcessingStatus.PURCHASEORDERS_SENT);
                s.setSentToProvider(getSentTime());
            }

            if (PurchaseOrderStatus.REJECTED.equals(getStatus()) && s.getEffectiveProcessingStatus() <= 400) {
                s.setProcessingStatus(ProcessingStatus.PURCHASEORDERS_REJECTED);
            }

            if (PurchaseOrderStatus.CONFIRMED.equals(getStatus()) && s.getEffectiveProcessingStatus() <= 400) {
                s.setProcessingStatus(ProcessingStatus.PURCHASEORDERS_CONFIRMED);
            }
        }
        try {
            price(em);
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }
}
