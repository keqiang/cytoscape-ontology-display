package edu.umich.med.mbni.lkq.cyontology.internal.app;

public class MyApplicationCenter {
	
	private static MyApplicationManager appManager;
	
	public static void registerApplicationManager(MyApplicationManager myApplicationManager) {
		appManager = myApplicationManager;
	}
	
	public static MyApplicationManager getApplicationManager() {
		return appManager;
	}
}
