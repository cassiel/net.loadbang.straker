//	$Id$
//	$Source$

package net.loadbang.straker;

/**	Interface for "hinting" the state of the system (such as track and pane
 	selection) which might be useful for building bits of supplemental
 	UI in Max.
 	
 	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net
 */

public interface IStateHinter {
	/**	Selecting a track from the UI.

		@param trackNum the track number, 1..n.
	 */
	
	void selectingTrack(int trackNum);

	/**	Selecting a pane from the UI. This will always be called during
	 	a track selection as well. Note that this will be called for (non-track)
	 	system panes as well: currently, the only one we have is "TR" for
	 	track control.
	 	
	 	@param glyph The character glyph of the pane.
	 */

	void selectingPane(String glyph);
}
