//	$Id$
//	$Source$

package net.loadbang.straker;

import net.loadbang.shado.Block;
import net.loadbang.shado.Frame;
import net.loadbang.shado.IPressRouter;
import net.loadbang.shado.IRenderable;
import net.loadbang.shado.exn.OperationException;
import net.loadbang.shado.types.LampState;
import net.loadbang.shadox.XFrame;

/**	A class representing a slider in the track/phase window.
	The actual slider is three pixels high, but we lay it over an
	eight-pixel strip to detect button presses intended to scroll it.
	
 	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net
 */

public class TrackControlSlider {
	/**	Are we currently muted? (Initially: yes.) This is a rather ugly bit
	 	of local state to make sure we handle layers properly when we
	 	change phase. We shouldn't really have to track mute state here
	 	at all. */

	private boolean itsMuted;
	
	/**	Current active phase (0..5). */
	private int itsActivePhase;
	
	/**	Cued phase (== active phase if none cued) (0..5). */
	private int itsCuedPhase;

	/**	The owning frame. */
	private XFrame itsFrame;

	private IRenderable itsMutedBarrier;

	private IRenderable itsCuedBarrier;

	private TrackDriver itsTrackDriver;

	/**	The composite slider (top, thumb, bottom). */
	private IPressRouter itsSliderAssembly;

	private TrackControlPane itsPane;
	private int itsX;

	private IStrakerState itsState;

	/**	Constructor: takes the enclosing frame, and a couple of renderables:
	 	the barrier for muted items, and for cued items. This slider inserts
	 	itself in the frame, initially "off" (so, under the "muted" layer).

		@param track the track which this slider is controlling
	 	@param frame the enclosing frame
	 	@param mutedBarrier the barrier for muting (sparse flash)
	 	@param cuedBarrier the barrier for cueing (thick flash)
	 	@param x the display column (0..15)
	 	@throws OperationException 
	 */
	public TrackControlSlider(TrackControlPane pane,
							  TrackDriver trackDriver,
							  XFrame frame,
							  IRenderable mutedBarrier,
							  IRenderable cuedBarrier,
							  IStrakerState state,
							  int x
							 )
		throws OperationException
	{
		itsPane = pane;
		itsTrackDriver = trackDriver;
		itsState = state;
		
		//	We model the slider by creating an illumimated (LampState.ON)
		//	three-pixel "thumb" topped and tailed with active regions
		//	for detecting up and down.

		IPressRouter thumb = new Block(1, 3) {
			@Override
			public boolean press(int x, int y, int how) {
				if (how != 0) { thumbPress(); }
				return true;
			}
		}.fill(LampState.ON);
		
		IPressRouter topMargin = new Block(1, 5) {
			@Override
			public boolean press(int x, int y, int how) {
				if (how != 0) { topPress(y); }
				return true;
			}			
		};
		
		IPressRouter botMargin = new Block(1, 5) {
			@Override
			public boolean press(int x, int y, int how) {
				if (how != 0) { botPress(y); }
				return true;
			}			
		};
		
		//	Create a composite frame (which is the one we
		//	actually scroll). Position the thumb at the
		//	origin.
		
		itsSliderAssembly =
			new Frame().add(topMargin, 0, -5)
				       .add(thumb, 0, 0)
				       .add(botMargin, 0, 3);
		
		//	Composite slider just below muted layer (i.e. muted=on).

		frame.add(itsSliderAssembly, x, 0).below(itsSliderAssembly, mutedBarrier);
		
		itsX = x;

		itsMuted = true;
		itsActivePhase = 0;
		itsCuedPhase = 0;

		itsFrame = frame;
		itsMutedBarrier = mutedBarrier;
		itsCuedBarrier = cuedBarrier;
	}
	
	private void thumbPress() {
		itsPane.doMutePress(itsX);
	}
	
	public void updateMuteState(boolean muted) {
		itsTrackDriver.setMuteState(muted);
		itsMuted = muted;
		updateViewStack();
	}
	
	/**	Deal with a button press above the thumb.

	 	@param y the row of the click within the
	 	upper ribbon.
	 */

	private void topPress(int y) {
		itsPane.doPhasePress(itsX, getThumbTopY() - 5 + y);
	}
	
	/**	Deal with a button press below the thumb.

 		@param y the row of the click within the
 		lower ribbon.
	 */

	private void botPress(int y) {
		itsPane.doPhasePress(itsX, getThumbTopY() + 1 + y);
	}
	
	public void updatePhaseState(int phase) {
		cuePhase(phase);
		scrollThumb(phase);
	}

	private void scrollThumb(int y) {
		try {
			itsFrame.scrollY(itsSliderAssembly, y, 1, null, new VoidCompletion());
		} catch (OperationException e) {
			e.printStackTrace();
		}
	}

	/**	Get the Y value of the top of the thumb at present (it
	 	may be scrolling).
	 */

	private int getThumbTopY() {
		try {
			return itsFrame.getY(itsSliderAssembly);
		} catch (OperationException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**	Change frame ordering etc. to reflect our current
	 	state, and refresh if necessary.
	 */

	private void updateViewStack() {
		try {
			if (itsMuted) {
				itsFrame.below(itsSliderAssembly, itsMutedBarrier);
			} else if (itsActivePhase != itsCuedPhase){
				itsFrame.above(itsSliderAssembly, itsMutedBarrier);
			} else {
				itsFrame.above(itsSliderAssembly, itsCuedBarrier);
			}
			
			itsFrame.refresh();
		} catch (OperationException exn) {
			exn.printStackTrace();
		}
	}
	
	private void cuePhase(int phase) {
		itsCuedPhase = phase;
		
		if (itsState.isClockRunning()) {
			updateViewStack();
		} else {
			//	Clock stopped, so we're "editing" - select phase immediately.
			selectCuedPhase();
		}
	}

	/**	Select any cued phase (we've just hit a gbar boundary,
	 	or perhaps the clock has stopped).
	 */

	public void selectCuedPhase() {
		if (itsActivePhase != itsCuedPhase) {
			itsTrackDriver.setPhase(itsCuedPhase);
			itsActivePhase = itsCuedPhase;
			updateViewStack();
		}
	}
}
