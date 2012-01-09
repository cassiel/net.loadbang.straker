//	$Id$
//	$Source$

package net.loadbang.straker.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**	Simple property manager. */

public class StrakerProperties {
	private ResourceBundle itsBundleProperties =
		ResourceBundle.getBundle(Manifest.Properties.GENERAL_BUNDLE);
	
	//	TODO: log missing resources or badly formatted ones.

	private String getStringProperty(String propertyName, String defaultValue) {
		try {
			return itsBundleProperties.getString(propertyName);
		} catch (MissingResourceException exn) {
			return defaultValue;
		}
	}
	
	private int getIntegerProperty(String propertyName, int defaultValue) {
		String val = getStringProperty(propertyName, Integer.toString(defaultValue));

		try {
			return Integer.parseInt(val);
		} catch (NumberFormatException exn) {
			return defaultValue;
		}
	}
	
	public int getMonomeWidth() {
		return getIntegerProperty(Manifest.Properties.Bundle.MONOME_WIDTH, 8);
	}

	public int getMonomeHeight() {
		return getIntegerProperty(Manifest.Properties.Bundle.MONOME_HEIGHT, 8);
	}

	public String getMonomeHost() {
		return getStringProperty(Manifest.Properties.Bundle.MONOME_HOST, "localhost");
	}

	public int getMonomePort() {
		return getIntegerProperty(Manifest.Properties.Bundle.MONOME_PORT, 8080);
	}

	public String getMonomePrefix() {
		return getStringProperty(Manifest.Properties.Bundle.MONOME_PREFIX, "/shado");
	}

	public int getTickIntervalMSec() {
		return getIntegerProperty(Manifest.Properties.Bundle.TICK_INTERVAL_MSEC, 50);
	}
}
