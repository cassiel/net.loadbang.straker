//	$Id$
//	$Source$

package net.loadbang.straker;

import net.loadbang.shado.Block;
import net.loadbang.shado.IPressRouter;
import net.loadbang.shado.exn.OperationException;
import net.loadbang.shado.exn.RangeException;
import net.loadbang.shado.types.LampState;
import net.loadbang.shadox.DisplayTaskManager;
import net.loadbang.shadox.XFrame;
import net.loadbang.shadox.DisplayTaskManager.ScheduleTime;

/**	The central pane for enabling tracks, cueing phases, etc.

 	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net
 */

public class TrackControlPane implements IContentPane {
	private TrackControlSlider[] itsSliders;
	private IPressRouter itsMuted;
	private IPressRouter itsCued;
	private BinaryCounter itsBinaryCounter;
	private XFrame itsFrame;
	private int itsLastCounterPos = -1;
	/**	Mute settings: initially, all muted. */
	private int itsMuteMask = 0xFFFFFFFF;
	private int[] itsPhaseState;

	public TrackControlPane(DisplayTaskManager manager, TrackDriver[] trackDrivers, IStrakerState state)
		throws OperationException
	{
		int width = trackDrivers.length;

		itsFrame = new XFrame(manager);

		//	Array of individual sliders:
		itsSliders = new TrackControlSlider[width];
		
		//	Lower-level block with very short blink cycle: this is the "muted"
		//	layer.
		
		itsMuted = new Block(width, 8).fill(LampState.OFF);
		
		//	Higher-level block with long blink cycle, which should include
		//	that of the "enabled" block (so that it shows non-enabled
		//	channels). This is the "cueing" layer for channels which are
		//	waiting to come into the right phase.
		
		itsCued = new Block(width, 8).fill(LampState.OFF);
		
		//	Add the opaque barriers, muted below cued. The layers are visible,
		//	which means they are hiding things.

		itsFrame.add(itsMuted, 0, 0).add(itsCued, 0, 0);
		
		//	Blink the layers: hide means reveal what's below, so these
		//	intervals are inverted. We create a ScheduleTime so that
		//	we know the blinking is synchronised. (Needless to say, the
		//	blink intervals need to be the same.)

		ScheduleTime t = manager.newTime(1);
		itsFrame.blink(itsMuted, 11, 1, t);				//	obscure for 11/12.
		itsFrame.blink(itsCued, 2, 4, t);				//	reveal for 10/12.
		
		//	Create a row of channels, all initially muted.

		for (int i = 0; i < width; i++) {
			itsSliders[i] = new TrackControlSlider(this, trackDrivers[i], itsFrame, itsMuted, itsCued, state, i);
		}
		
		itsBinaryCounter = new BinaryCounter();
		itsFrame.add(itsBinaryCounter, 15, 0);
		
		itsPhaseState = new int[itsSliders.length];
	}

	public void cuePhases(int globalBarLength, int absolutePos) {
		int pos = absolutePos % globalBarLength;
		//	Count at quarter-speed for display.
		int counterPos = pos >> 2;
		int counterBarLength = globalBarLength >> 2;
		
		if (counterPos != itsLastCounterPos) {
			try {
				itsBinaryCounter.setValue(counterBarLength - 1 - counterPos % counterBarLength);
			} catch (RangeException e) { }

			itsFrame.refresh();
			itsLastCounterPos = counterPos;
		}		
		
		//	XXX we should really do a cue catch-up if the clock stops or if
		//	we locate somewhere else.

		if (pos == 0) {			//	Start of new gbar: switch phase if required
			for (TrackControlSlider slider: itsSliders) {
				slider.selectCuedPhase();
			}
		}
	}

	public void setFocussed(boolean how) { }

	public void setVisible(boolean how) { }
	
	/**	Callback from the track control slider: a thumb press to
	 	flip the mute state of a track.

	 	@param x the index of the track, 0..N.
	 */

	public void doMutePress(int x) {
		itsMuteMask = itsMuteMask ^ (1 << x);
		itsSliders[x].updateMuteState((itsMuteMask & (1 << x)) != 0);
	}
	
	public void doPhasePress(int x, int phase) {
		itsPhaseState[x] = phase;
		itsSliders[x].updatePhaseState(phase);
	}
	
	public int getMuteState() {
		return itsMuteMask;
	}
	
	public void setMuteState(int state) {
		itsMuteMask = state;
		
		for (int i = 0; i < itsSliders.length; i++) {
			itsSliders[i].updateMuteState((itsMuteMask & (1 << i)) != 0);
		}
	}
	
	public int[] getPhaseState() {
		return itsPhaseState;
	}
	
	/**	Restore the phase state (an array of phase numbers, one per
	 	track). We need to be somewhat resilient: we might change the
	 	number of tracks in our Python code.
	 */

	public void setPhaseState(int[] phaseState) {
		itsPhaseState = new int[itsSliders.length];
		System.arraycopy(phaseState, 0, itsPhaseState, 0,
						 Math.min(itsSliders.length, phaseState.length)
						);

		for (int i = 0; i < itsSliders.length; i++) {
			itsSliders[i].updatePhaseState(itsPhaseState[i]);
		}
	}

	public IPressRouter getContent() {
		return itsFrame;
	}

	public String getGlyph() {
		return "TK!";
	}
}
