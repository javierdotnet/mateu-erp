package io.mateu.erp.server.booking;

import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.booking.transfer.TransferDirection;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.config.DummyDate;
import io.mateu.erp.model.importing.TransferImportTask;
import io.mateu.erp.shared.booking.BookingService;
import io.mateu.ui.core.server.ServerSideHelper;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.FileLocator;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;

import javax.persistence.EntityManager;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * Created by miguel on 23/4/17.
 */
public class BookingServiceImpl implements BookingService {
    @Override
    public Data getTransferSummary(Data parameters) throws Throwable {
        Data d = new Data();

        //INCOMING, SHUTTLE, EXECUTIVE, PRIVATE
        //INBOUND, OUTBOUND, POINTTOPOINT
        String sql = "select d.value, to_char(d.value, 'yyyy-MM-dd DY'), a.name " +


                ", sum(case when transfertype = 1 and direction in (0,2) then pax else 0 end)" +
                ", sum(case when transfertype = 1 and direction = 1 then pax else 0 end)" +

                ", sum(case when transfertype = 3 and direction in (0,2) then pax else 0 end)" +
                ", sum(case when transfertype = 3 and direction = 1 then pax else 0 end)" +

                ", sum(case when transfertype = 2 and direction in (0,2) then pax else 0 end)" +
                ", sum(case when transfertype = 2 and direction = 1 then pax else 0 end)" +

                ", sum(case when transfertype = 0 and direction in (0,2) then pax else 0 end)" +
                ", sum(case when transfertype = 0 and direction = 1 then pax else 0 end)" +



                ", min(case when transfertype = 1 and direction in (0,2) then effectiveprocessingstatus else 1000 end)" +
                ", min(case when transfertype = 1 and direction = 1 then effectiveprocessingstatus else 1000 end)" +

                ", min(case when transfertype = 3 and direction in (0,2) then effectiveprocessingstatus else 1000 end)" +
                ", min(case when transfertype = 3 and direction = 1 then effectiveprocessingstatus else 1000 end)" +

                ", min(case when transfertype = 2 and direction in (0,2) then effectiveprocessingstatus else 1000 end)" +
                ", min(case when transfertype = 2 and direction = 1 then effectiveprocessingstatus else 1000 end)" +

                ", min(case when transfertype = 0 and direction in (0,2) then effectiveprocessingstatus else 1000 end)" +
                ", min(case when transfertype = 0 and direction = 1 then effectiveprocessingstatus else 1000 end)" +


                ", to_char(pickupTimeInformed, 'Mon-dd HH24:MI')" +



                "from dummydate d left outer join service on d.value = start left outer join transferpoint a on a.id = airport_id " +

                "where 1 = 1 ";

        if (!parameters.isEmpty("start")) sql += " and d.value >= '" + parameters.getLocalDate("start").format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "' ";
        if (!parameters.isEmpty("finish")) sql += " and d.value <= '" + parameters.getLocalDate("finish").format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "' ";

        sql += " group by 1, 2, a.name order by 1, a.name";

        long t0 = new Date().getTime();

        int rowsPerPage = 3000;
        int fromRow = 0;

        d.getList("_data");

        for (Object[] l : ServerSideHelper.getServerSideApp().selectPage(sql, fromRow, rowsPerPage)) {
            Data r;
            d.getList("_data").add(r = new Data());
            if (l != null) {
                for (int i = 0; i <= 2; i++) {
                    r.set((i == 0)?"_id":"col" + i, l[i]);
                }

                for (int i = 0; i < 8; i++) {
                    Data dx = new Data();
                    dx.set("_text", "" + l[3 + i]);
                    dx.set("_status", l[3 + i + 8]);
                    String css = "";
                    Object o = l[3 + i + 8];
                    int v = 0;
                    if (o == null) v = 0;
                    else if (o instanceof Integer) v = (Integer)o;
                    else if (o instanceof Long) v = ((Long)o).intValue();
                    if ("0".equals("" + l[3 + i])) {
                        css = null;
                    } else {
                        if (v == 450) css = "rojo";
                        else if (v < 500) css = "naranja";
                        else if (v >= 500) css = "verde";
                    }
                    dx.set("_css", css);
                    r.set("col" + (3 + i), dx);
                }
                int i = 11;
                r.set("col" + i, l[i++]);

            }

        }

        int numRows = ServerSideHelper.getServerSideApp().getNumberOfRows(sql);
        long t = new Date().getTime() - t0;
        d.set("_subtitle", "" + numRows + " records found in " + t + "ms.");
        d.set("_data_currentpageindex", fromRow / rowsPerPage);
        d.set("_data_totalrows", numRows);
        d.set("_data_pagecount", numRows / rowsPerPage + ((numRows % rowsPerPage == 0)?0:1));

        return d;
    }

    @Override
    public void informPickupTime(UserData user, List<Data> selection) throws Throwable {
        for (Data s : selection) {
            LocalDate d = s.get("_id");

            Helper.transact(new JPATransaction() {
                @Override
                public void run(EntityManager em) throws Throwable {

                    List<TransferService> l = em.createQuery("select x from " + TransferService.class.getName() + " x where start = :s and direction = :d order by flightTime asc").setParameter("s", d).setParameter("d", TransferDirection.OUTBOUND).getResultList();

                    for (TransferService s : l) {
                        try {
                            s.informPickupTime(user, em);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }


                    DummyDate dd = em.find(DummyDate.class, d);
                    dd.setPickupTimeInformed(LocalDateTime.now());

                }
            });


        }
    }

    @Override
    public String importPickupTimeExcel(Data data) throws Throwable {
        System.out.println("" + data);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        if (data.isEmpty("file")) throw new Throwable("You must first upload an excel file");
        else {
            Helper.transact(new JPATransaction() {
                @Override
                public void run(EntityManager em) throws Throwable {
                    Object[][] l = Helper.parseExcel(new File(((FileLocator) data.get("file")).getTmpPath()))[0];
                    int colref = -1;
                    int colfecha = -1;
                    int colhora = -1;

                    for (int fila = 0; fila < l.length; fila++) {
                        if (colref < 0 || colfecha < 0 || colhora < 0) {
                            for (int col = 0; col < l[fila].length; col++) {
                                if ("ref".equalsIgnoreCase("" + l[fila][col])) colref = col;
                                if ("pickup date".equalsIgnoreCase("" + l[fila][col])) colfecha = col;
                                if ("pickup time".equalsIgnoreCase("" + l[fila][col])) colhora = col;
                            }
                        } else {
                            try {
                                String ref = (l[fila][colref] != null)?"" + l[fila][colref]:null;
                                Date fecha = (Date) l[fila][colfecha];
                                Date hora = (Date) l[fila][colhora];

                                if (ref == null) pw.println("line " + fila + ": missing ref");
                                else if (fecha == null) pw.println("line " + fila + ": missing pickup date");
                                else if (hora == null) pw.println("line " + fila + ": missing pickup time");
                                else {
                                    long id = (long) Double.parseDouble(ref);
                                    TransferService s = em.find(TransferService.class, id);
                                    LocalDateTime pud = Instant.ofEpochMilli(fecha.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
                                    LocalDateTime put = Instant.ofEpochMilli(hora.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();

                                    s.setPickupTime(LocalDateTime.of(pud.getYear(), pud.getMonth(), pud.getDayOfMonth(), put.getHour(), put.getMinute()));

                                    pw.println("line " + fila + ": pickup time for service id " + id + " setted to " + s.getPickupTime());
                                }

                            } catch (Exception e) {
                                pw.println("line " + fila + ": " + e.getClass().getName() + ":" + e.getMessage());
                            }
                        }
                    }
                    if (colref < 0) throw new Throwable("Missing ref col");
                    if (colfecha < 0) throw new Throwable("Missing pickup date col");
                    if (colhora < 0) throw new Throwable("Missing pickup time col");
                }
            });
        }
        return sw.toString();
    }

    @Override
    public void retryImportationTasks(List<Data> selection) throws Throwable {
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {
                for (Data d : selection) {
                    TransferImportTask t = em.find(TransferImportTask.class, d.get("_id"));
                    t.setStatus(TransferImportTask.STATUS.PENDING);
                    t.execute(em);
                }
            }
        });
    }

    @Override
    public void cancelImportationTasks(List<Data> selection) throws Throwable {
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {
                for (Data d : selection) {
                    TransferImportTask t = em.find(TransferImportTask.class, d.get("_id"));
                    t.setStatus(TransferImportTask.STATUS.PENDING);
                    t.execute(em);
                }
            }
        });
    }
}
