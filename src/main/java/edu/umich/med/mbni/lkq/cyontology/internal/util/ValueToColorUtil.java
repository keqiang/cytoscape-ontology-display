package edu.umich.med.mbni.lkq.cyontology.internal.util;

import java.awt.Color;

public class ValueToColorUtil {

	private double min;
	private double max;
	private double factor;
	
	public ValueToColorUtil(double min, double max) {
		this.min = min;
		this.max = max;
		this.factor = 255 / (max - min);
	}
	
	public double convertValue(double origin) {
		return origin * factor;
	}
	
	public double getMin() {
		return min;
	}
	
	public double getMax() {
		return max;
	}
	
	public Color convertToColor(double value) {
		Double convertedValue = convertValue(value - min);
		Color color = new Color(convertedValue.intValue(), 0, 0);
		return color;
	}
}
