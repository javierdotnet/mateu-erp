package io.mateu.erp.model.product.hotel;

import io.mateu.ui.core.client.views.AbstractForm;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.interfaces.UseCalendarToEdit;
import io.mateu.ui.mdd.server.util.DatesRange;
import io.mateu.ui.mdd.server.util.XMLSerializable;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Element;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class LinearFare implements XMLSerializable, UseCalendarToEdit {

    private List<DatesRange> dates = new ArrayList<>();

    private String name;

    private List<LinearFareLine> lines = new ArrayList<>();

    public LinearFare(List<DatesRange> dates, String name, List<LinearFareLine> lines) {
        this.dates = dates;
        this.name = name;
        this.lines = lines;
    }

    public LinearFare() {
    }

    public LinearFare(Element e) {
        if (e.getAttribute("name") != null) setName(e.getAttributeValue("name"));

        if (e.getChild("dates") != null) for (Element z: e.getChild("dates").getChildren()) getDates().add(new DatesRange(z));

        if (e.getChild("lines") != null) for (Element z : e.getChild("lines").getChildren("line")) getLines().add(new LinearFareLine(z));

    }

    @Override
    public Element toXml() {
        Element e = new Element("fare");
        if (getName() != null) e.setAttribute("name", getName());
        if (getDates() != null) {
            Element x;
            e.addContent(x = new Element("dates"));
            for (DatesRange r : getDates()) x.addContent(r.toXml());
        }

        Element els;
        e.addContent(els = new Element("lines"));
        for (LinearFareLine l : getLines()) {
            els.addContent(l.toXml());
        }
        return e;
    }

    @Override
    public AbstractForm getForm(EntityManager entityManager, UserData userData) {
        return null;
    }

    @Override
    public List<LocalDate> getCalendarDates() {
        return null;
    }

    @Override
    public String getCalendarText() {
        return null;
    }

    @Override
    public String getCalendarCss() {
        return null;
    }

    @Override
    public String getNamePropertyName() {
        return "name";
    }

    @Override
    public String getDatesRangesPropertyName() {
        return "dates";
    }

}
