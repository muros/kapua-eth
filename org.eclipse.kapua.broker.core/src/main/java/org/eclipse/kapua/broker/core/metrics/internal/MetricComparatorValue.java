package org.eclipse.kapua.broker.core.metrics.internal;

import java.util.Comparator;


public class MetricComparatorValue<T> implements Comparator<MetricColumn<T>> {

    private boolean ascending;

    public MetricComparatorValue(boolean asc) {
        ascending = asc;
    }

    @Override
    public int compare(MetricColumn<T> mc1, MetricColumn<T> mc2) {

        Class<T> c = mc1.get_class();
        if(c == Float.class) {
            Float f1 = (Float)mc1.get_metric();
            Float f2 = (Float)mc2.get_metric();
            if(ascending) return f1.compareTo(f2);
            else return f2.compareTo(f1);
        } else if(c == Double.class) {
            Double f1 = (Double)mc1.get_metric();
            Double f2 = (Double)mc2.get_metric();
            if(ascending) return f1.compareTo(f2);
            else return f2.compareTo(f1);
        } else if(c == Integer.class) {
            Integer f1 = (Integer)mc1.get_metric();
            Integer f2 = (Integer)mc2.get_metric();
            if(ascending) return f1.compareTo(f2);
            else return f2.compareTo(f1);
        } else if(c == Long.class) {
            Long f1 = (Long)mc1.get_metric();
            Long f2 = (Long)mc2.get_metric();
            if(ascending) return f1.compareTo(f2);
            else return f2.compareTo(f1);
        } else if(c == Boolean.class) {
            Boolean f1 = (Boolean)mc1.get_metric();
            Boolean f2 = (Boolean)mc2.get_metric();
            if(ascending) return f1.compareTo(f2);
            else return f2.compareTo(f1);
        } else if(c == String.class) {
            String f1 = (String)mc1.get_metric();
            String f2 = (String)mc2.get_metric();
            if(ascending) return f1.compareTo(f2);
            else return f2.compareTo(f1);
        } else if(c == byte[].class) {
//   byte[] f1 = (byte[])mc1.get_metric();
//   byte[] f2 = (byte[])mc2.get_metric();
            return 0;
        }
        return 0;
    }

}
