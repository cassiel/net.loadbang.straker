//	$Id$
//	$Source$

package net.loadbang.straker;

/**	The pane switcher is something which knows about scrolling the view
 	from one pane to another, and deals with focus and visibility
 	events. It doesn't have any larger view of the context (such as a
 	general menu system or array of views).
 	
 	The scrolling name refers to the movement of the frame, not
 	the contents.
 	
	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net
 */

public interface IPaneSwitcher extends IDisplayable {
	enum Direction {
		UP(0, 1), DOWN(0, -1), LEFT(1, 0), RIGHT(-1, 0);

		//	These values are the direction sense of the
		//	contents, not the frame.
		private int itsXSense, itsYSense;

		Direction(int xSense, int ySense) {
			itsXSense = xSense;
			itsYSense = ySense;
		}

		public int getXSense() { return itsXSense; }
		public int getYSense() { return itsYSense; }
	};

	/**	Scroll to and activate a new pane, discarding the old one
	 	once the scroll is done.
	 */

	void scrollToPane(INotifiablePane newPane, Direction direction);

	INotifiablePane getCurrentPane00();
}
