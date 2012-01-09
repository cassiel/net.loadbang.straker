package net.loadbang.straker;

/**	A high-level interface for something which can occupy the
	monome "screen" - tracks have these, and the central straker
	UI will have some (such as the track selector). This is
	a specific pane which can have a timeline bar superimposed
	over it.

	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net
 */

public interface IContentPane extends INotifiablePane {
	/**	Get the two-character glyph for this pane (assuming we scroll to it). */
	String getGlyph();
}
