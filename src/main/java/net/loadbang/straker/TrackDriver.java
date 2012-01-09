//	$Id$
//	$Source$

package net.loadbang.straker;

import net.loadbang.shado.exn.OperationException;
import net.loadbang.shadox.DisplayTaskManager;

/**	Encapsulation of a holder for a track. It has a little bit of internal
	state: the last-declared step.

	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net
 */

public class TrackDriver implements ITrackAPI {
	/**	Muted state - initially true - tracks that in {@link TrackControlSlider}. */
	private boolean itsMuted = true;
	private DisplayTaskManager itsManager;
	private IChannelledMidiSender itsMidiSender;
	private int itsMidiChannel;
	private ITrack itsTrack;
	private int itsLastLocatedStep = -1;
	private int itsCurrentPaneIndex;

	/**	Constructor. All the step and substep division is calculated on
		the fly by calling into the channel dynamically to determine its
		timebase.
	 */

	public TrackDriver(DisplayTaskManager manager,
					   IChannelledMidiSender midiSender,
					   int midiChannel,
					   ITrack track
					  ) throws OperationException {
		itsManager = manager;
		itsMidiSender = midiSender;
		itsMidiChannel = midiChannel;
		itsTrack = track;
		track.setDriver(this);
	}
	
	public ITrack getTrack() {
		return itsTrack;
	}
	
	public boolean containsPane(IContentPane pane) {
		int n = itsTrack.getNumPanes();

		for (int i = 0; i < n; i++) {
			if (itsTrack.getPane(i) == pane) {
				return true;
			}
		}
		
		return false;
	}

	/**	Deal with a global clock, counting in sub-steps, origin at zero. */
	
	@Deprecated
	public Integer clock00(int globalBarLength, int absolutePos) {
		int timebase = itsTrack.getTimeBase();
		int loopLength = itsTrack.getLoopLength();
		Integer idx00;
		
		int pos = absolutePos % globalBarLength;
		int step = (pos / timebase) % loopLength;
		int subStep = pos % timebase;
		
		//	TODO: given a funny global bar length, it's possible that we'll
		//	hit step 0 twice in succession, so we always fire if sub-step is
		//	zero, even for the same step. (Think about scrubbing the locator.)

		if (step != itsLastLocatedStep || subStep == 0) {
			itsTrack.atStep(step);
			itsLastLocatedStep = step;
			idx00 = step;
		} else {
			idx00 = null;
		}
		
		itsTrack.subStep(subStep);
		return idx00;
	}

	public void noteOut(int pitch, int velocity, int durationMSec) {
		if (!itsMuted) {
			itsMidiSender.noteOut(itsMidiChannel, pitch, velocity, durationMSec);
		}
	}

	public void ctrlOut(int controlNum, int value) {
		if (!itsMuted) {
			itsMidiSender.ctrlOut(itsMidiChannel, controlNum, value);
		}
	}

	public void setPhase(int phase) {
		itsTrack.setPhase(phase);
	}

	/**	Track the index of the currently selected pane in this track. */
	public int getCurrentPaneIndex() {
		return itsCurrentPaneIndex;
	}

	public void setCurrentPaneIndex(int paneIndex) {
		itsCurrentPaneIndex = paneIndex;
	}
	
	public DisplayTaskManager getDisplayManager() {
		return itsManager;
	}

	public void setMuteState(boolean muted) {
		itsMuted = muted;
	}
}
