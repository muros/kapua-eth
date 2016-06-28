package org.eclipse.kapua.broker.core.metrics.internal;

import java.util.Comparator;


public class MetricComparatorTime<T> implements Comparator<MetricColumn<T>> {

    private boolean ascending;

    public MetricComparatorTime(boolean asc) {
        ascending = asc;
    }

    @Override
    public int compare(MetricColumn<T> mc1, MetricColumn<T> mc2) {
        Long t1 = mc1.get_timestamp();
        Long t2 = mc2.get_timestamp();
        if(ascending)
            return t1.compareTo(t2);
        else
            return t2.compareTo(t1);
    }

}
