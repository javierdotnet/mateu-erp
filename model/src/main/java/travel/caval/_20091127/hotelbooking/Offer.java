
package travel.caval._20091127.hotelbooking;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Clase Java para offer complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="offer"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="hotelId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="hotelName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="modificationDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="formalizationDates" type="{http://caval.travel/20091127/hotelBooking}datesRange" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="checkinDates" type="{http://caval.travel/20091127/hotelBooking}datesRange" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="stayDates" type="{http://caval.travel/20091127/hotelBooking}datesRange" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="checkinWeekDays" type="{http://www.w3.org/2001/XMLSchema}boolean" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="checkoutWeekDays" type="{http://www.w3.org/2001/XMLSchema}boolean" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="minimumStay" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="maximumStay" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="boardTypes" type="{http://caval.travel/20091127/hotelBooking}boardType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="roomTypes" type="{http://caval.travel/20091127/hotelBooking}roomType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="categories" type="{http://caval.travel/20091127/hotelBooking}offerCategory" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="offerDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="image" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "offer", propOrder = {
    "id",
    "hotelId",
    "hotelName",
    "modificationDate",
    "formalizationDates",
    "checkinDates",
    "stayDates",
    "checkinWeekDays",
    "checkoutWeekDays",
    "minimumStay",
    "maximumStay",
    "boardTypes",
    "roomTypes",
    "categories",
    "offerDescription",
    "image"
})
public class Offer {

    protected String id;
    protected String hotelId;
    protected String hotelName;
    protected String modificationDate;
    @XmlElement(nillable = true)
    protected List<DatesRange> formalizationDates;
    @XmlElement(nillable = true)
    protected List<DatesRange> checkinDates;
    @XmlElement(nillable = true)
    protected List<DatesRange> stayDates;
    @XmlElement(nillable = true)
    protected List<Boolean> checkinWeekDays;
    @XmlElement(nillable = true)
    protected List<Boolean> checkoutWeekDays;
    protected Integer minimumStay;
    protected Integer maximumStay;
    @XmlElement(nillable = true)
    protected List<BoardType> boardTypes;
    @XmlElement(nillable = true)
    protected List<RoomType> roomTypes;
    @XmlElement(nillable = true)
    protected List<OfferCategory> categories;
    protected String offerDescription;
    protected String image;

    /**
     * Obtiene el valor de la propiedad id.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Define el valor de la propiedad id.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Obtiene el valor de la propiedad hotelId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHotelId() {
        return hotelId;
    }

    /**
     * Define el valor de la propiedad hotelId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHotelId(String value) {
        this.hotelId = value;
    }

    /**
     * Obtiene el valor de la propiedad hotelName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHotelName() {
        return hotelName;
    }

    /**
     * Define el valor de la propiedad hotelName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHotelName(String value) {
        this.hotelName = value;
    }

    /**
     * Obtiene el valor de la propiedad modificationDate.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModificationDate() {
        return modificationDate;
    }

    /**
     * Define el valor de la propiedad modificationDate.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModificationDate(String value) {
        this.modificationDate = value;
    }

    /**
     * Gets the value of the formalizationDates property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the formalizationDates property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFormalizationDates().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DatesRange }
     * 
     * 
     */
    public List<DatesRange> getFormalizationDates() {
        if (formalizationDates == null) {
            formalizationDates = new ArrayList<DatesRange>();
        }
        return this.formalizationDates;
    }

    /**
     * Gets the value of the checkinDates property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the checkinDates property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCheckinDates().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DatesRange }
     * 
     * 
     */
    public List<DatesRange> getCheckinDates() {
        if (checkinDates == null) {
            checkinDates = new ArrayList<DatesRange>();
        }
        return this.checkinDates;
    }

    /**
     * Gets the value of the stayDates property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the stayDates property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStayDates().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DatesRange }
     * 
     * 
     */
    public List<DatesRange> getStayDates() {
        if (stayDates == null) {
            stayDates = new ArrayList<DatesRange>();
        }
        return this.stayDates;
    }

    /**
     * Gets the value of the checkinWeekDays property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the checkinWeekDays property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCheckinWeekDays().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Boolean }
     * 
     * 
     */
    public List<Boolean> getCheckinWeekDays() {
        if (checkinWeekDays == null) {
            checkinWeekDays = new ArrayList<Boolean>();
        }
        return this.checkinWeekDays;
    }

    /**
     * Gets the value of the checkoutWeekDays property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the checkoutWeekDays property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCheckoutWeekDays().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Boolean }
     * 
     * 
     */
    public List<Boolean> getCheckoutWeekDays() {
        if (checkoutWeekDays == null) {
            checkoutWeekDays = new ArrayList<Boolean>();
        }
        return this.checkoutWeekDays;
    }

    /**
     * Obtiene el valor de la propiedad minimumStay.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMinimumStay() {
        return minimumStay;
    }

    /**
     * Define el valor de la propiedad minimumStay.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMinimumStay(Integer value) {
        this.minimumStay = value;
    }

    /**
     * Obtiene el valor de la propiedad maximumStay.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaximumStay() {
        return maximumStay;
    }

    /**
     * Define el valor de la propiedad maximumStay.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaximumStay(Integer value) {
        this.maximumStay = value;
    }

    /**
     * Gets the value of the boardTypes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the boardTypes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBoardTypes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BoardType }
     * 
     * 
     */
    public List<BoardType> getBoardTypes() {
        if (boardTypes == null) {
            boardTypes = new ArrayList<BoardType>();
        }
        return this.boardTypes;
    }

    /**
     * Gets the value of the roomTypes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the roomTypes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRoomTypes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RoomType }
     * 
     * 
     */
    public List<RoomType> getRoomTypes() {
        if (roomTypes == null) {
            roomTypes = new ArrayList<RoomType>();
        }
        return this.roomTypes;
    }

    /**
     * Gets the value of the categories property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the categories property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCategories().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OfferCategory }
     * 
     * 
     */
    public List<OfferCategory> getCategories() {
        if (categories == null) {
            categories = new ArrayList<OfferCategory>();
        }
        return this.categories;
    }

    /**
     * Obtiene el valor de la propiedad offerDescription.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOfferDescription() {
        return offerDescription;
    }

    /**
     * Define el valor de la propiedad offerDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOfferDescription(String value) {
        this.offerDescription = value;
    }

    /**
     * Obtiene el valor de la propiedad image.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImage() {
        return image;
    }

    /**
     * Define el valor de la propiedad image.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImage(String value) {
        this.image = value;
    }

}
