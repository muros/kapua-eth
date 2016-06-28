package org.eclipse.kapua.message.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * A container for EdcMetric instances organized into a list.
 */
public class KapuaMetricsMapType {
    @XmlElement(name="metric")
    public List<KapuaMetric> metrics = new ArrayList<KapuaMetric>();
}
