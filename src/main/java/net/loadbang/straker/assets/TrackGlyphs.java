//	$Id$
//	$Source$

package net.loadbang.straker.assets;

import net.loadbang.straker.INotifiablePane;

/**	Compressed-ish (5 x 5) idents for tracks 1..16.

	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net
 */

public class TrackGlyphs extends Glyphs {
	private static INotifiablePane[] itsGlyphs = new INotifiablePane[] {
		/*	1. */	makePane("00001 00001 00001 00001 00001"),
		/*	2. */	makePane("00111 00001 00111 00100 00111"),
		/*	3. */	makePane("00111 00001 00011 00001 00111"),
		/*	4. */	makePane("00101 00101 00111 00001 00001"),
		/*	5. */	makePane("00111 00100 00111 00001 00111"),
		/*	6. */	makePane("00111 00100 00111 00101 00111"),
		/*	7. */	makePane("00111 00001 00001 00001 00001"),
		/*	8. */	makePane("00111 00101 00111 00101 00111"),
		/*	9. */	makePane("00111 00101 00111 00001 00001"),
		/* 10. */	makePane("10111 10101 10101 10101 10111"),
		/* 11. */	makePane("00101 00101 00101 00101 00101"),
		/* 12. */	makePane("10111 10001 10111 10100 10111"),
		/* 13. */	makePane("10111 10001 10111 10001 10111"),
		/* 14. */	makePane("10101 10101 10111 10001 10001"),
		/* 15. */	makePane("10111 10100 10111 10001 10111"),
		/* 16. */	makePane("10111 10100 10111 10101 10111")
	};

	private static INotifiablePane itsBogusGlyph =
		makePane("00000 00000 00111 00000 00000");

	public static INotifiablePane getTrackNumPane(int trackNum) {
		if (trackNum <= 0 || trackNum > itsGlyphs.length) {
			return itsBogusGlyph;
		} else {
			return itsGlyphs[trackNum - 1];
		}
	}
}
