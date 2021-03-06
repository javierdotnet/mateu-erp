package io.mateu.erp.model.importing;

import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPAHelper;
import io.mateu.ui.mdd.server.util.JPATransaction;

import javax.persistence.EntityManager;

/**
 * Created by Antonia on 26/03/2017.
 */
public class TransferImportQueue {

    public static void main(String... args) throws Throwable {
        run();
    }

    public static void run() throws Throwable {
//hace un select de las tareas en estado pending
        //por cada una llama a execute()
        Object[] ids = JPAHelper.selectObjects("select x.id from TransferImportTask x " +
                " where  x.status = io.mateu.erp.model.importing.TransferImportTask.STATUS.PENDING");

        for (Object id : ids) {
            try {
                Helper.transact(new JPATransaction() {
                    @Override
                    public void run(EntityManager em) throws Exception {
                        TransferImportTask t = em.find(TransferImportTask.class, id);
                        t.execute(em);
                    }
                });
            } catch (Throwable e) {
                e.printStackTrace();

            }
        }
    }
}
