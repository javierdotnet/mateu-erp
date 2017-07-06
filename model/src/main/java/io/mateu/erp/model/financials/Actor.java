package io.mateu.erp.model.financials;

import io.mateu.ui.mdd.server.annotations.ListColumn;
import io.mateu.ui.mdd.server.annotations.Required;
import io.mateu.ui.mdd.server.annotations.SearchFilter;
import io.mateu.ui.mdd.server.annotations.StartsLine;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Element;

import javax.persistence.*;

/**
 * holder for customers (e.g. a touroperator, a travel agency, ...)
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter
@Setter
public class Actor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Required
    @SearchFilter
    @ListColumn
    private String name;

    @ListColumn
    private String businessName;

    private String address;

    private String vatIdentificationNumber;

    @ListColumn
    private String email;

    @ListColumn
    private String comments;

    @ManyToOne
    @Required
    private Currency currency;


    @StartsLine
    private PurchaseOrderSendingMethod ordersSendingMethod;
    private String sendOrdersTo;
    private boolean automaticOrderSending;
    private boolean automaticOrderConfirmation;

    @StartsLine
    private boolean exportableToinvoicingApp;
    private String idInInvoicingApp;
    private boolean shuttleTransfersInOwnInvoice;


    @Override
    public String toString() {
        return getName();
    }

    public Element toXml() {
        Element xml = new Element("actor");
        xml.setAttribute("id", "" + getId());
        xml.setAttribute("name", getName());
        if (getBusinessName() != null) xml.setAttribute("bussinessName", getBusinessName());
        if (getAddress() != null) xml.setAttribute("address", getBusinessName());
        if (getVatIdentificationNumber() != null) xml.setAttribute("vaiIdentificationNumber", getVatIdentificationNumber());
        if (getEmail() != null) xml.setAttribute("email", getEmail());
        if (getComments() != null) xml.setAttribute("comments", getComments());

        return xml;
    }

}
