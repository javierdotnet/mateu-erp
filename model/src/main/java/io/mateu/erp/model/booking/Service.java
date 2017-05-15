package io.mateu.erp.model.booking;

import com.google.common.base.Strings;
import io.mateu.erp.model.authentication.Audit;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.booking.generic.GenericService;
import io.mateu.erp.model.booking.generic.PriceDetail;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.financials.PurchaseOrderSendingMethod;
import io.mateu.erp.model.mdd.*;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.workflow.AbstractTask;
import io.mateu.erp.model.workflow.SendPurchaseOrdersTask;
import io.mateu.erp.model.workflow.TaskStatus;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.annotations.*;
import io.mateu.ui.mdd.server.annotations.Parameter;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import io.mateu.ui.mdd.shared.ActionType;
import io.mateu.ui.mdd.shared.MDDLink;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by miguel on 31/1/17.
 */
@Entity
@Getter
@Setter
public abstract class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ListColumn(width = 70)
    private long id;

    @Embedded
    @Ignored
    private Audit audit;

    @ManyToOne
    @Required
    @SearchFilter(value="Booking Id", field = "id")
    @SearchFilter(field = "agencyReference")
    @SearchFilter(field = "agency")
    @ListColumn(value="Boking", field = "id")
    @ListColumn(field = "agencyReference", width = 150)
    @ListColumn(field = "agency", width = 150)
    @SearchFilter(field = "leadName")
    @ListColumn(field = "leadName")
    private Booking booking;


    @Ignored
    @NotInEditor
    @ListColumn
    @CellStyleGenerator(IconCellStyleGenerator.class)
    private String icon;

    @StartsLine
    @ListColumn(width = 60)
    @CellStyleGenerator(ConfirmedCellStyleGenerator.class)
    private ServiceConfirmationStatus answer = ServiceConfirmationStatus.CONFIRMED;

    @ListColumn
    @CellStyleGenerator(ProcessingStatusCellStyleGenerator.class)
    @NotInEditor
    private ProcessingStatus processingStatus;

    public void setProcessingStatus(ProcessingStatus processingStatus) {
        this.processingStatus = processingStatus;
        if (getProcessingStatus() != null) switch (getProcessingStatus()) {
            case INITIAL: setEffectiveProcessingStatus(100); break;
            case DATA_OK: setEffectiveProcessingStatus(200); break;
            case PURCHASEORDERS_READY: setEffectiveProcessingStatus(300); break;
            case PURCHASEORDERS_SENT: setEffectiveProcessingStatus(400); break;
            case PURCHASEORDERS_REJECTED: setEffectiveProcessingStatus(450); break;
            case PURCHASEORDERS_CONFIRMED: setEffectiveProcessingStatus(500); break;
            default: setEffectiveProcessingStatus(0);
        }
    }

    private String answerText;

    @ListColumn
    @CellStyleGenerator(CancelledCellStyleGenerator.class)
    private boolean cancelled;

    @ListColumn
    @CellStyleGenerator(NoShowCellStyleGenerator.class)
    private boolean noShow;

    @ListColumn
    @CellStyleGenerator(LockedCellStyleGenerator.class)
    private boolean locked;

    @ListColumn
    @CellStyleGenerator(HeldCellStyleGenerator.class)
    private boolean held;


    @TextArea
    @StartsLine
    private String comment;

    @TextArea
    private String privateComment;

    @Output
    private String changeLog;

    @StartsLine
    private boolean alreadyInvoiced;

    @Required
    @ManyToOne
    private Office office;

    @Required
    @ManyToOne
    private PointOfSale pos;


    @StartsLine
    private boolean valueOverrided;

    private double overridedValue;

    @Output
    @ListColumn
    @CellStyleGenerator(ValuedCellStyleGenerator.class)
    private boolean valued;

    @Output
    @ListColumn
    private double total;

    @Output
    private String priceReport;

    @Ignored
    private int effectiveProcessingStatus;

    @StartsLine
    @ManyToOne
    private Actor preferredProvider;

    private boolean alreadyPurchased;


    @Output
    @ListColumn
    @SearchFilter
    private String providers;

    @Output
    private LocalDateTime sentToProvider;



    @NotInEditor
    @SearchFilter
    @ListColumn(order = true)
    private LocalDate start;

    @Ignored
    @ListColumn
    private LocalDate finish;


    @Ignored
    @OneToMany
    private List<PriceDetail> priceBreakdown = new ArrayList<>();

    @ManyToMany
    @Ignored
    private List<PurchaseOrder> purchaseOrders = new ArrayList<>();


    @Ignored
    private String signature;

    @Transient
    @Ignored
    private String signatureBefore;

    public Map<String, Object> toMap() {
        Map<String, Object> m = new HashMap<>();
        m.put("cancelled", isCancelled());
        m.put("comment", getComment());
        if (getPreferredProvider() != null) m.put("preferredprovider", getPreferredProvider());
        m.put("start", getStart());
        m.put("finish", getFinish());
        return m;
    }


    @Action(name = "Repair signature")
    public static void repairSignature(@Selection List<Data> _selection) throws Throwable {
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {
                for (Data d : _selection) {
                    Service s = em.find(Service.class, d.get("_id"));
                    if (s instanceof TransferService) {
                        TransferService t = (TransferService) s;
                        LocalDate z = t.getFlightTime().toLocalDate();
                        if (t.getFlightTime().getHour() < 6) z = z.minusDays(1);
                        t.setStart(z);
                        t.setFinish(z);
                    }
                    //s.setSignature(s.createSignature());
                }
            }
        });
    }

    @Action(name = "Repair bookings")
    public static void repair(@Selection List<Data> _selection) throws Throwable {
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {
                for (Data d : _selection) {
                    Service s = em.find(Service.class, d.get("_id"));
                    if (s instanceof TransferService) {
                        ((TransferService)s).afterSet(em, false);
                    } else if (s instanceof GenericService) {
                        ((GenericService)s).afterSet(em, false);
                    }
                    s.price(em);
                }
            }
        });
    }

    @Action(name = "Send to provider")
    public static void sendToProvider(EntityManager em, UserData _user, @Selection List<Data> selection, @Parameter(name = "Provider") Actor provider, @Parameter(name = "Email") String email, @Parameter(name = "Postscript") @TextArea String postscript) {
        for (Data d : selection) {
            Service s = em.find(Service.class, d.get("_id"));
            s.setAlreadyPurchased(false);
            if (provider != null) s.setPreferredProvider(provider);
            try {
                s.checkPurchase(em);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        Map<Actor, SendPurchaseOrdersTask> taskPerProvider = new HashMap<>();
        User u = em.find(User.class, _user.getLogin());
        for (Data d : selection) {
            Service s = em.find(Service.class, d.get("_id"));
            if (provider != null) s.setPreferredProvider(provider);
            for (PurchaseOrder po : s.getPurchaseOrders()) {
                SendPurchaseOrdersTask t = taskPerProvider.get(po.getProvider());
                if (t == null) {
                    taskPerProvider.put(po.getProvider(), t = new SendPurchaseOrdersTask());
                    em.persist(t);
                    t.setOffice(s.getOffice());
                    t.setProvider(po.getProvider());
                    t.setStatus(TaskStatus.PENDING);
                    t.setAudit(new Audit(u));
                    if (!Strings.isNullOrEmpty(email)) {
                        t.setMethod(PurchaseOrderSendingMethod.EMAIL);
                        t.setTo(email);
                    } else {
                        t.setMethod((po.getProvider().getOrdersSendingMethod() != null)?po.getProvider().getOrdersSendingMethod():PurchaseOrderSendingMethod.EMAIL);
                        t.setTo(po.getProvider().getSendOrdersTo());
                    }
                    t.setCc(s.getOffice().getEmailCC());
                    t.setPostscript(postscript);
                }
                t.getPurchaseOrders().add(po);
                po.getSendingTasks().add(t);
                try {
                    po.afterSet(em, false);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }
    }


    public abstract String createSignature();


    @Action(name = "Purchase")
    public void checkPurchase(EntityManager em) throws Throwable {
        if (!isAlreadyPurchasedBefore() && isAlreadyPurchased()) {
            setProcessingStatus(ProcessingStatus.PURCHASEORDERS_CONFIRMED);
            setSignature(createSignature());
        } else if (getPurchaseOrders().size() == 0 || getSignature() == null || !getSignature().equals(createSignature())) {
            setProcessingStatus(ProcessingStatus.DATA_OK);
            setSignature(createSignature());
            try {
                generatePurchaseOrders(em);
                setProcessingStatus(ProcessingStatus.PURCHASEORDERS_READY);
                for (PurchaseOrder po : getPurchaseOrders()) {
                    if (po.getSignature() == null || !po.getSignature().equals(po.createSignature())) po.setSent(false);
                }
                for (PurchaseOrder po : getPurchaseOrders()) {
                    if (!po.isSent() && po.getProvider() != null && po.getProvider().isAutomaticOrderSending()) {
                        try {
                            po.send(em);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                boolean allSent = getPurchaseOrders().size() > 0;
                boolean allConfirmed = getPurchaseOrders().size() > 0;
                for (PurchaseOrder po : getPurchaseOrders()) {
                    if (!po.isSent()) {
                        allSent = false;
                        allConfirmed = false;
                    } else if (!PurchaseOrderStatus.CONFIRMED.equals(po.getStatus())) {
                        allConfirmed = false;
                    }
                }
                if (allConfirmed) setProcessingStatus(ProcessingStatus.PURCHASEORDERS_CONFIRMED);
                else if (allSent) setProcessingStatus(ProcessingStatus.PURCHASEORDERS_SENT);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            String ps = "";
            for (PurchaseOrder po : getPurchaseOrders()) {
                if (!"".equals(ps)) ps += ",";
                ps += po.getProvider().getName();
            }
            setProviders(ps);

        } else {
            throw new Throwable("Nothing changed. No need to purchase again");
        }
    }


    @Action(name = "Price")
    public void price(EntityManager em) {
        setValued(false);
        setTotal(0);
        if (isValueOverrided()) {
            setTotal(getOverridedValue());
            setValued(true);
            setPriceReport("Used overrided value");
        }
        else {
            try {
                setTotal(rate(em));
                setValued(true);
            } catch (Throwable throwable) {
                setPriceReport("" + throwable.getClass().getName() + ":" + throwable.getMessage());
                throwable.printStackTrace();
            }
        }
    }

    @Badges
    public List<Data> getBadges() {
        List<Data> l = new ArrayList<>();
        l.add(new Data("_css", "brown", "_value", "" + getTotal()));
        String s = "";
        ProcessingStatus v = getProcessingStatus();
        switch (v) {
            case INITIAL:
            case DATA_OK: s = "azul"; break;
            case PURCHASEORDERS_SENT:
            case PURCHASEORDERS_READY: s = "naranja"; break;
            case PURCHASEORDERS_CONFIRMED: s = "verde"; break;
            case PURCHASEORDERS_REJECTED: s = "rojo"; break;
        }
        l.add(new Data("_css", s, "_value", "" + getProcessingStatus()));
        return l;
    }

    public List<MDDLink> getLinks() {
        List<MDDLink> l = new ArrayList<>();
        l.add(new MDDLink("Booking", Booking.class, ActionType.OPENEDITOR, new Data("_id", getBooking().getId())));
        l.add(new MDDLink("Tasks", AbstractTask.class, ActionType.OPENLIST, new Data("services.id", getId())));
        l.add(new MDDLink("Purchase orders", PurchaseOrder.class, ActionType.OPENLIST, new Data("services.id", getId())));
        return l;
    }


    public abstract double rate(EntityManager em) throws Throwable;

    public void generatePurchaseOrders(EntityManager em) throws Throwable {
        if (getPreferredProvider() == null) throw new Throwable("Preferred provider needed for service " + getId());
        if (isHeld()) throw new Throwable("Service " + getId() + " is held");
        if (isCancelled() && getSentToProvider() == null) throw new Throwable("Cancelled and was never sent");
        if (!ProcessingStatus.PURCHASEORDERS_CONFIRMED.equals(getProcessingStatus())) {
            PurchaseOrder po = null;
            if (getPurchaseOrders().size() > 0) {
                po = getPurchaseOrders().get(getPurchaseOrders().size() - 1);
                if (!getPreferredProvider().equals(po.getProvider())) {
                    po.cancel(em);
                    po = null;
                }
            }
            if (po == null) {
                po = new PurchaseOrder();
                em.persist(po);
                po.setAudit(new Audit());
                po.getServices().add(this);
                getPurchaseOrders().add(po);
                po.setStatus(PurchaseOrderStatus.PENDING);
            }
            po.setOffice(getOffice());
            po.setProvider(getPreferredProvider());
            po.setCurrency(getPreferredProvider().getCurrency());
        }
    }


    @Override
    public String toString() {
        String s = "";
        if (getAudit() != null) s += getAudit();
        return s;
    }


    public static void main(String... args) {

        /*
        Service s = new Service() {
            @Override
            public String createSignature() {
                return null;
            }

            @Override
            public double rate(EntityManager em) throws Throwable {
                return 0;
            }
        };
        s.setProcessingStatus(ProcessingStatus.DATA_OK);

        System.out.println(s.getProcessingStatus().ordinal());
*/



    }


    public Map<String,Object> getData() {
        Map<String, Object> d = new HashMap<>();

        d.put("id", getId());
        d.put("locator", getBooking().getId());
        d.put("agency", getBooking().getAgency().getName());
        d.put("agencyReference", getBooking().getAgencyReference());
        d.put("status", (isCancelled())?"CANCELLED":"ACTIVE");
        d.put("created", getAudit().getCreated().format(DateTimeFormatter.BASIC_ISO_DATE.ISO_DATE_TIME));
        d.put("office", getOffice().getName());

        return d;
    }

    public void cancel(EntityManager em) {
        setCancelled(true);
    }

    @PostLoad
    public void postLoad() {
        setAlreadyPurchasedBefore(isAlreadyPurchased());
        setSignatureBefore(getSignature());
    }

    @Transient
    @Ignored
    private List<String> vistos = new ArrayList<>();

    @Transient
    @Ignored
    private boolean alreadyPurchasedBefore;

    @PreUpdate
    public void preStore() {
        String l = getChangeLog();
        if (l == null) l = "";
        boolean update = false;
        if (Strings.isNullOrEmpty(getSignatureBefore())) {
            if (vistos.size() == 0) {
                if (!"".equals(l)) l += "\n";
                l += "" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                l += " ";
                l += "ADDED";
                vistos.add("***");
            }
            update = true;
        } else if (!getSignatureBefore().equals(getSignature())) {
            try {
                Map<String, Object> m0 = Helper.fromJson(getSignatureBefore());
                Map<String, Object> m = Helper.fromJson(getSignature());
                List<String> ks = new ArrayList<>();
                ks.addAll(m0.keySet());
                for (String k : m.keySet()) if (!ks.contains(k)) ks.add(k);
                for (String k : ks) {
                    Object v0 = m0.get(k);
                    Object v = m.get(k);
                    if (v0 == null) v0 = "---";
                    if (v == null) v = "---";
                    if (v0 instanceof Map && ((Map)v0).containsKey("chronology")) {
                        Map x0 = (Map) v0;
                        v0 = "" + x0.get("year") + "-" + x0.get("month") + "-" + x0.get("dayOfMonth") + " " + x0.get("hour") + ":" + x0.get("minute");
                    }
                    if (v instanceof Map && ((Map)v).containsKey("chronology")) {
                        Map x0 = (Map) v;
                        v = "" + x0.get("year") + "-" + x0.get("month") + "-" + x0.get("dayOfMonth") + " " + x0.get("hour") + ":" + x0.get("minute");
                    }
                    if (!v0.equals(v)) {
                        if (!vistos.contains(k)) {
                            if (vistos.size() == 0) {
                                if (!"".equals(l)) l += "\n";
                                l += "" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                                l += " ";
                                l += "MODIFIED";
                            }
                            vistos.add(k);
                            l += "\n" + Helper.capitalize(k) + ": " + v0 + " -> " + v;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            update = true;
        }
        setChangeLog(l);
    }

}
