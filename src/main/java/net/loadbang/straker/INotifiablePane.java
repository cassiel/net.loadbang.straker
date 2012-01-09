//	$Id$
//	$Source$

package net.loadbang.straker;

/**	A high-level interface for something which can be scrolled
 	into and out of view - tracks have these, and the central straker
 	UI will have some (such as the track selector).

	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net
 */

public interface INotifiablePane extends IDisplayable {
	/**	Visibility flag: is any part of this pane in view (it may be partial
	 	if we're scrolling).
	 */
	
	void setVisible(boolean how);

	/**	Set the focus of this pane: when true, the pane is active
	 	and accepting clicks.
	 */

	void setFocussed(boolean how);
}
