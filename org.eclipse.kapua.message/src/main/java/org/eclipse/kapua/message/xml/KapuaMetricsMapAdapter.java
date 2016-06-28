package org.eclipse.kapua.message.xml;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class KapuaMetricsMapAdapter extends XmlAdapter<KapuaMetricsMapType,Map<String,Object>> {

    @Override
    public KapuaMetricsMapType marshal(Map<String,Object> metrics) throws Exception {

        KapuaMetricsMapType metricsMap = new KapuaMetricsMapType();
        for(Entry<String,Object> entry : metrics.entrySet()) {
            if (entry != null) {
                if (entry.getValue() != null) {
                    KapuaMetric metricType = new KapuaMetric(entry.getKey(), entry.getValue().getClass(), entry.getValue());
                    metricsMap.metrics.add(metricType);
                }
            }
        }
        return metricsMap;
    }


    @Override
    public Map<String,Object> unmarshal(KapuaMetricsMapType metrics) throws Exception {

        Map<String,Object> metricsMap = new HashMap<String,Object>();
        for(KapuaMetric metric : metrics.metrics) {

            String name  = metric.name;
            Object value = metric.getValue();
            metricsMap.put(name, value);
        }
        return metricsMap;
    }
}
