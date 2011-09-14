package com.beansight.android;

import greendroid.app.GDApplication;


public class BeansightApplication extends GDApplication {
	public static String BEANSIGHT_PREFS = "beansightPrefs";

	// A GDApplication needs a HOmeActivity for the toolbar.
    @Override
    public Class<?> getHomeActivityClass() {
        return HomeActivity.class;
    }
	
}
