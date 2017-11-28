package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.util.XMLSerializable;
import io.mateu.ui.mdd.server.annotations.Owned;
import io.mateu.ui.mdd.server.annotations.OwnedList;
import org.jdom2.Element;

import java.util.HashMap;
import java.util.Map;

public class BoardFare implements XMLSerializable {

    @Owned
    private FareValue roomPrice;

    @Owned
    private FareValue paxPrice;

    @OwnedList
    private Map<Integer, FareValue> paxDiscounts = new HashMap<>();

    @OwnedList
    private Map<Integer, FareValue> juniorDiscounts = new HashMap<>();

    @OwnedList
    private Map<Integer, FareValue> childDiscounts = new HashMap<>();

    @OwnedList
    private Map<Integer, FareValue> infantDiscounts = new HashMap<>();


    public FareValue getRoomPrice() {
        return roomPrice;
    }

    public void setRoomPrice(FareValue roomPrice) {
        this.roomPrice = roomPrice;
    }

    public FareValue getPaxPrice() {
        return paxPrice;
    }

    public void setPaxPrice(FareValue paxPrice) {
        this.paxPrice = paxPrice;
    }

    public Map<Integer, FareValue> getPaxDiscounts() {
        return paxDiscounts;
    }

    public void setPaxDiscounts(Map<Integer, FareValue> paxDiscounts) {
        this.paxDiscounts = paxDiscounts;
    }

    public Map<Integer, FareValue> getChildDiscounts() {
        return childDiscounts;
    }

    public void setChildDiscounts(Map<Integer, FareValue> childDiscounts) {
        this.childDiscounts = childDiscounts;
    }

    public BoardFare(Element e) {

        if (e.getChild("roomPrice") != null) setRoomPrice(new FareValue(e.getChild("roomPrice")));
        if (e.getChild("paxPrice") != null) setPaxPrice(new FareValue(e.getChild("paxPrice")));
        for (Element z : e.getChildren("paxDiscount")) getPaxDiscounts().put(Integer.parseInt(z.getAttributeValue("pax")), new FareValue(z));
        for (Element z : e.getChildren("juniorDiscount")) getJuniorDiscounts().put(Integer.parseInt(z.getAttributeValue("junior")), new FareValue(z));
        for (Element z : e.getChildren("childDiscount")) getChildDiscounts().put(Integer.parseInt(z.getAttributeValue("child")), new FareValue(z));
        for (Element z : e.getChildren("infantDiscount")) getInfantDiscounts().put(Integer.parseInt(z.getAttributeValue("infant")), new FareValue(z));
    }

    public BoardFare() {
    }

    public BoardFare(FareValue roomPrice, FareValue paxPrice, Map<Integer, FareValue> paxDiscounts, Map<Integer, FareValue> juniorDiscounts, Map<Integer, FareValue> childDiscounts, Map<Integer, FareValue> infantDiscounts) {
        this.roomPrice = roomPrice;
        this.paxPrice = paxPrice;
        this.paxDiscounts = paxDiscounts;
        this.juniorDiscounts = juniorDiscounts;
        this.childDiscounts = childDiscounts;
        this.infantDiscounts = infantDiscounts;
    }

    @Override
    public Element toXml() {
        Element e = new Element("boardFare");
        if (getRoomPrice() != null) e.addContent(getRoomPrice().toXml().setName("roomPrice"));
        if (getPaxPrice() != null) e.addContent(getPaxPrice().toXml().setName("paxPrice"));
        for (int k : getPaxDiscounts().keySet()) {
            e.addContent(getPaxDiscounts().get(k).toXml().setName("paxDiscount").setAttribute("pax", "" + k));
        }
        for (int k : getJuniorDiscounts().keySet()) {
            e.addContent(getJuniorDiscounts().get(k).toXml().setName("juniorDiscount").setAttribute("junior", "" + k));
        }
        for (int k : getChildDiscounts().keySet()) {
            e.addContent(getChildDiscounts().get(k).toXml().setName("childDiscount").setAttribute("child", "" + k));
        }
        for (int k : getInfantDiscounts().keySet()) {
            e.addContent(getInfantDiscounts().get(k).toXml().setName("infantDiscount").setAttribute("infant", "" + k));
        }
        return e;
    }

    public Map<Integer, FareValue> getJuniorDiscounts() {
        return juniorDiscounts;
    }

    public void setJuniorDiscounts(Map<Integer, FareValue> juniorDiscounts) {
        this.juniorDiscounts = juniorDiscounts;
    }

    public Map<Integer, FareValue> getInfantDiscounts() {
        return infantDiscounts;
    }

    public void setInfantDiscounts(Map<Integer, FareValue> infantDiscounts) {
        this.infantDiscounts = infantDiscounts;
    }
}
