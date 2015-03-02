package edu.umich.med.mbni.lkq.cyontology.internal.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Keqiang Li
 */
public class AggregationMethodUtil {
	
	public static final String AGGREGATION_METHOD_MEAN = "mean";
	public static final String AGGREGATION_METHOD_MAX = "max";
	public static final String AGGREGATION_METHOD_MIN = "min";
	public static final String AGGREGATION_METHOD_MEDIAN = "median";
	public static final String AGGREGATION_METHOD_SUM = "sum";
	
	public static Double getMean(Collection<Double> values) {
		if (values.isEmpty()) {
			return null;
		}

		return getSum(values) / values.size();
	}

	public static Double getSum(Collection<Double> values) {
		if (values.isEmpty()) {
			return null;
		}

		Double sum = 0.0;
		for (Double value : values) {
			sum += value;
		}

		return sum;
	}

	public static Double getMax(Collection<Double> values) {
		if (values.isEmpty()) {
			return null;
		}

		Double max = Double.MIN_VALUE;
		for (Double value : values) {
			if (value > max) {
				max = value;
			}
		}

		return max;
	}

	public static Double getMin(Collection<Double> values) {
		if (values.isEmpty()) {
			return null;
		}

		Double min = Double.MAX_VALUE;
		for (Double value : values) {
			if (value < min) {
				min = value;
			}
		}

		return min;
	}

	public static Double getMedian(List<Double> values) {
		Collections.sort(values);
		if (values.isEmpty()) {
			return null;
		}

		int middle = values.size() / 2;
		if (values.size() % 2 == 1) {
			return values.get(middle);
		} else {
			return (values.get(middle - 1) + values.get(middle)) / 2.0;
		}
	}

	public static Double getAggregatedValue(List<Double> values,
			String aggregationType) {
		switch (aggregationType) {
		case AGGREGATION_METHOD_MEAN:
			return getMean(values);
		case AGGREGATION_METHOD_SUM:
			return getSum(values);
		case AGGREGATION_METHOD_MIN:
			return getMin(values);
		case AGGREGATION_METHOD_MAX:
			return getMax(values);
		case AGGREGATION_METHOD_MEDIAN:
			return getMedian(values);
		default:
			return getMean(values);
		}
	}

}
