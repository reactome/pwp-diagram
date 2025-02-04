package org.reactome.web.diagram.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class ExpressionUtil {

    public static double mean(List<Double> list) {
        double sum = 0;
        for (Double value : list) {
            sum += value;
        }
        return sum / list.size();
    }

    // the array double[] m MUST BE SORTED
    public static double median(List<Double> list) {
        if(list.size()==1) return list.get(0);
        int middle = list.size()/2;
        if (list.size() %2 == 1) {
            return list.get(middle);
        } else {
            return (list.get(middle-1) + list.get(middle)) / 2.0;
        }
    }

//    public static double pseudoMedian(List<Double> list) {
//        int middle = list.size()/2;
//        return list.get(middle);
//    }

    public static List<Integer> mode(final List<Integer> numbers) {
        final List<Integer> modes = new ArrayList<Integer>();
        final Map<Integer, Integer> countMap = new HashMap<Integer, Integer>();

        int max = -1;

        for (final int n : numbers) {
            int count = 0;

            if (countMap.containsKey(n)) {
                count = countMap.get(n) + 1;
            } else {
                count = 1;
            }

            countMap.put(n, count);

            if (count > max) {
                max = count;
            }
        }

        for (final Map.Entry<Integer, Integer> tuple : countMap.entrySet()) {
            if (tuple.getValue() == max) {
                modes.add(tuple.getKey());
            }
        }

        return modes;
    }
}
