package edu.umich.med.mbni.lkq.cyontology.internal.utils;

import java.awt.Color;

public class NumberConvertUtil {

	private double min;
	private double max;
	private double factor;
	
	public NumberConvertUtil(double min, double max) {
		this.min = min;
		this.max = max;
		this.factor = 100 / max;
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
		return NumberToColorUtil.numberToColor(convertValue(value));
	}
}
