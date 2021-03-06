package io.mateu.erp.model.config;

import io.mateu.common.model.common.File;
import io.mateu.common.model.config.DummyDate;
import io.mateu.ui.mdd.server.annotations.Action;
import io.mateu.ui.mdd.server.annotations.NotInEditor;
import io.mateu.ui.mdd.server.annotations.Tab;
import io.mateu.ui.mdd.server.annotations.TextArea;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by miguel on 19/3/17.
 */
@Entity(name = "ERPAppConfig")
@Getter
@Setter
public class AppConfig extends io.mateu.common.model.config.AppConfig {

    @TextArea
    private String xslfoForHotelContract;

    @TextArea
    private String xslfoForTransferContract;

    @TextArea
    private String xslfoForVoucher;

    @TextArea
    private String xslfoForIssuedInvoice;

    @TextArea
    private String xslfoForWorld;

    @TextArea
    private String xslfoForTransfersList;

    @TextArea
    private String xslfoForPurchaseOrder;

    @TextArea
    private String purchaseOrderTemplate;

    @TextArea
    private String pickupSmsTemplate;

    @TextArea
    private String pickupEmailTemplate;

    @TextArea
    private String pickupSmsTemplateEs;

    @Tab("CMS")
    private String nginxConfigDirectory = "/etc/nginx/conf.d";

    private String nginxReloadCommand = "service nginx reload";

    //@Tab("Currency exchange")
    //@Convert(converter = CurrencyExchangeConverter.class)
    //private CurrencyExchange currencyExchange = new CurrencyExchange();

    public static AppConfig get(EntityManager em) {
        return em.find(AppConfig.class, 1l);
    }

}
