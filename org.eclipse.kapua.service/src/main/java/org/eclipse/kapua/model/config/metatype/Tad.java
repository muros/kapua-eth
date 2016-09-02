package org.eclipse.kapua.model.config.metatype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

/**
 * <p>
 * Java class for Tad complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Tad">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Option" type="{http://www.osgi.org/xmlns/metatype/v1.2.0}Toption" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="description" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" use="required" type="{http://www.osgi.org/xmlns/metatype/v1.2.0}Tscalar" />
 *       &lt;attribute name="cardinality" type="{http://www.w3.org/2001/XMLSchema}int" default="0" />
 *       &lt;attribute name="min" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="max" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="default" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="required" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;anyAttribute/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlRootElement(name = "AD", namespace = "http://www.osgi.org/xmlns/metatype/v1.2.0")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Tad", propOrder = {
                                     "option",
                                     "any"
})
public class Tad
{
    @XmlElement(name = "Option", namespace = "http://www.osgi.org/xmlns/metatype/v1.2.0")
    protected List<Toption>    option;

    @XmlAnyElement(lax = true)
    protected List<Object>     any;

    @XmlAttribute(name = "name")
    protected String           name;

    @XmlAttribute(name = "description")
    protected String           description;

    @XmlAttribute(name = "id", required = true)
    protected String           id;

    @XmlAttribute(name = "type", required = true)
    protected Tscalar          type;

    @XmlAttribute(name = "cardinality")
    protected Integer          cardinality;

    @XmlAttribute(name = "min")
    protected String           min;

    @XmlAttribute(name = "max")
    protected String           max;

    @XmlAttribute(name = "default")
    protected String           _default;

    @XmlAttribute(name = "required")
    protected Boolean          required;

    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the option property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the option property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getOption().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Toption }
     *
     *
     */
    public List<Toption> getOption()
    {
        if (option == null) {
            option = new ArrayList<Toption>();
        }
        return option;
    }

    /**
     * Gets the value of the any property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getAny().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Element }
     * {@link Object }
     *
     *
     */
    public List<Object> getAny()
    {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return any;
    }

    /**
     * Gets the value of the name property.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setName(String value)
    {
        this.name = value;
    }

    /**
     * Gets the value of the description property.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the value of the description property.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setDescription(String value)
    {
        this.description = value;
    }

    /**
     * Gets the value of the id property.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setId(String value)
    {
        this.id = value;
    }

    /**
     * Gets the value of the type property.
     *
     * @return
     *         possible object is
     *         {@link Tscalar }
     *
     */
    public Tscalar getType()
    {
        return type;
    }

    /**
     * Sets the value of the type property.
     *
     * @param value
     *            allowed object is
     *            {@link Tscalar }
     *
     */
    public void setType(Tscalar value)
    {
        this.type = value;
    }

    /**
     * Gets the value of the cardinality property.
     *
     * @return
     *         possible object is
     *         {@link Integer }
     *
     */
    public int getCardinality()
    {
        if (cardinality == null) {
            return 0;
        }
        else {
            return cardinality;
        }
    }

    /**
     * Sets the value of the cardinality property.
     *
     * @param value
     *            allowed object is
     *            {@link Integer }
     *
     */
    public void setCardinality(Integer value)
    {
        this.cardinality = value;
    }

    /**
     * Gets the value of the min property.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getMin()
    {
        return min;
    }

    /**
     * Sets the value of the min property.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setMin(String value)
    {
        this.min = value;
    }

    /**
     * Gets the value of the max property.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getMax()
    {
        return max;
    }

    /**
     * Sets the value of the max property.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setMax(String value)
    {
        this.max = value;
    }

    /**
     * Gets the value of the default property.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getDefault()
    {
        return _default;
    }

    /**
     * Sets the value of the default property.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setDefault(String value)
    {
        this._default = value;
    }

    /**
     * Gets the value of the required property.
     *
     * @return
     *         possible object is
     *         {@link Boolean }
     *
     */
    public boolean isRequired()
    {
        if (required == null) {
            return true;
        }
        else {
            return required;
        }
    }

    /**
     * Sets the value of the required property.
     *
     * @param value
     *            allowed object is
     *            {@link Boolean }
     *
     */
    public void setRequired(Boolean value)
    {
        this.required = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     *
     * <p>
     * the map is keyed by the name of the attribute and
     * the value is the string value of the attribute.
     *
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     *
     *
     * @return
     *         always non-null
     */
    public Map<QName, String> getOtherAttributes()
    {
        return otherAttributes;
    }

}