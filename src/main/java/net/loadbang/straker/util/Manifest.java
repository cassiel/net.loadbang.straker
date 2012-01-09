//	$Id$
//	$Source$

package net.loadbang.straker.util;

public interface Manifest {
	public class Properties {
		/**	Prefix for the the property bundles (we append ".props"). */
		public final static String PROPERTY_PATH = "net.loadbang.straker";

		public static final String GENERAL_BUNDLE = PROPERTY_PATH + ".props.GENERAL";
	
		/**	Tags for properties in the bundle/JAR. */

		static public class Bundle {
			public static final String MONOME_WIDTH = "monome-width";
			public static final String MONOME_HEIGHT = "monome-height";
			public static final String MONOME_HOST = "monome-host";
			public static final String MONOME_PORT = "monome-port";
			public static final String MONOME_PREFIX = "monome-prefix";
			public static final String TICK_INTERVAL_MSEC = "tick-interval-msec";
		}
	}
}
