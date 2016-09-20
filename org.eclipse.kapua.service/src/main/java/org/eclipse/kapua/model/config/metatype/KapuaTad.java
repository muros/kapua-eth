/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.kapua.model.config.metatype;

import java.util.List;
import java.util.Map;

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
public interface KapuaTad
{

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
     * {@link KapuaToption }
     *
     *
     */
    public List<KapuaToption> getOption();

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
    public List<Object> getAny();

    /**
     * Gets the value of the name property.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getName();

    /**
     * Sets the value of the name property.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setName(String value);

    /**
     * Gets the value of the description property.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getDescription();

    /**
     * Sets the value of the description property.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setDescription(String value);

    /**
     * Gets the value of the id property.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getId();

    /**
     * Sets the value of the id property.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setId(String value);

    /**
     * Gets the value of the type property.
     *
     * @return
     *         possible object is
     *         {@link KapuaTscalar }
     *
     */
    public KapuaTscalar getType();

    /**
     * Sets the value of the type property.
     *
     * @param value
     *            allowed object is
     *            {@link KapuaTscalar }
     *
     */
    public void setType(KapuaTscalar value);

    /**
     * Gets the value of the cardinality property.
     *
     * @return
     *         possible object is
     *         {@link Integer }
     *
     */
    public int getCardinality();

    /**
     * Sets the value of the cardinality property.
     *
     * @param value
     *            allowed object is
     *            {@link Integer }
     *
     */
    public void setCardinality(Integer value);

    /**
     * Gets the value of the min property.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getMin();

    /**
     * Sets the value of the min property.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setMin(String value);

    /**
     * Gets the value of the max property.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getMax();

    /**
     * Sets the value of the max property.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setMax(String value);

    /**
     * Gets the value of the default property.
     *
     * @return
     *         possible object is
     *         {@link String }
     *
     */
    public String getDefault();

    /**
     * Sets the value of the default property.
     *
     * @param value
     *            allowed object is
     *            {@link String }
     *
     */
    public void setDefault(String value);

    /**
     * Gets the value of the required property.
     *
     * @return
     *         possible object is
     *         {@link Boolean }
     *
     */
    public boolean isRequired();

    /**
     * Sets the value of the required property.
     *
     * @param value
     *            allowed object is
     *            {@link Boolean }
     *
     */
    public void setRequired(Boolean value);

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
    public Map<QName, String> getOtherAttributes();

}