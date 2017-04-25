package io.mateu.ui.mdd.shared;

import io.mateu.ui.core.communication.Service;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.mdd.server.WizardPageVO;

/**
 * Created by miguel on 11/1/17.
 */
@Service(url = "erp")
public interface ERPService {

    public Object[][] select(String jpql) throws Throwable;

    public Object selectSingleValue(String jpql) throws Throwable;

    public Data selectPaginated(Data parameters) throws Throwable;

    public int executeUpdate(String jpql) throws Throwable;

    Data set(String serverSideControllerKey, String entityClassName, Data data) throws Throwable;

    Data get(String serverSideControllerKey, String entityClassName, Object id) throws Throwable;

    Data getMetaData(String entityClassName) throws Throwable;

    Object runInServer(String className, String methodName, Data parameters) throws Throwable;

    WizardPageVO execute(String wizardClassName, Object action, Data data) throws Throwable;

}
