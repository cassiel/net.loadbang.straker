//	$Id$
//	$Source$

package net.loadbang.straker;

import net.loadbang.shado.Block;
import net.loadbang.shado.IPressRouter;
import net.loadbang.shado.exn.OperationException;
import net.loadbang.shado.types.LampState;
import net.loadbang.shadox.DisplayTaskManager;
import net.loadbang.shadox.XFrame;
import net.loadbang.straker.util.StrakerProperties;


/**	An overlay for the pane switcher which shows a timeline.

	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net
 */

public class TimeLineOverlay implements IDisplayable {
	private XFrame itsFrame;
	private Block itsSlice;
	private int itsMonomeWidth;
	private int itsMonomeHeight;
	private IPaneSwitcher itsSwitcher;
	
	public TimeLineOverlay(DisplayTaskManager manager, IPaneSwitcher switcher) {
		StrakerProperties properties = new StrakerProperties();

		itsMonomeWidth = properties.getMonomeWidth();
		itsMonomeHeight = properties.getMonomeHeight();

		itsFrame = new XFrame(manager);
		
		itsSlice = new Block(1, itsMonomeHeight).fill(LampState.FLIP);

		try {
			itsFrame.add(switcher.getContent(), 0, 0);
			itsFrame.add(itsSlice, 0, 0).hide(itsSlice);
		} catch (OperationException e) {
			e.printStackTrace();
		}
		
		itsSwitcher = switcher;
	}

	/**	Deal with incoming clock, passed in with global bar length.
 		Animates the current timeline.
 	
 		TODO: not clear why we can't do the modulus outside this. */

	public void showTimeLine(TrackDriver td, int step) {
		INotifiablePane p00 = itsSwitcher.getCurrentPane00();

		if (   p00 != null
			&& p00 instanceof IContentPane
			&& td.containsPane((IContentPane) p00)
		   ) {
			int loopLength = td.getTrack().getLoopLength();

			try {
				itsFrame.moveTo(itsSlice, step % loopLength % itsMonomeWidth, 0);
				itsFrame.flash(itsSlice, 2, null);
			} catch (OperationException e) {
				// TODO Auto-generated catch block
			}
		}
	}

	public IPressRouter getContent() {
		return itsFrame;
	}
}
