//	$Id$
//	$Source$

package net.loadbang.straker;

import java.util.List;

import net.loadbang.shado.PressManager;
import net.loadbang.shado.exn.OperationException;
import net.loadbang.shadox.DisplayTaskManager;
import net.loadbang.straker.util.StrakerProperties;

/**	A bank of channels (which, in the most versatile case, will be injected
	via Python).
	
	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net
 */

public class Bank {
	private TrackDriver[] itsTracks;
	private PressManager itsPressManager;
	private IPaneSwitcher itsPaneSwitcher;
	private TimeLineOverlay itsPaneOverlay;
	
	/**	P3-style Gbar - used to reset patterns, and also used to kick
		off cued phase changes. */

	private int itsGlobalBarLength;
	private TrackControlPane itsTrackControlPane;
	private MenuBarPane itsMenuBarPane;
	
	public Bank(DisplayTaskManager manager,
				IChannelledMidiSender midiSender,
				List<ITrack> tracks,
				IStateHinter hinter,
				IStrakerState state
			   )
		throws OperationException
	{
		StrakerProperties properties = new StrakerProperties();
		int numTracks = tracks.size();

		//	Initialise with default void channel. */
		itsTracks = new TrackDriver[numTracks];
		
		for (int i = 0; i < numTracks; i++) {
			itsTracks[i] = new TrackDriver(manager, midiSender, i + 1, tracks.get(i));
		}
		
		itsTrackControlPane = new TrackControlPane(manager, itsTracks, state);
		itsMenuBarPane =
			new MenuBarPane(manager,
							new IContentPane[] { itsTrackControlPane },
							itsTracks, hinter
						   );
		
		itsPaneSwitcher =
			new PaneSwitcher(manager, properties.getMonomeWidth(), properties.getMonomeHeight());
		
		itsPaneOverlay = new TimeLineOverlay(manager, itsPaneSwitcher);

		manager.getMainFrame().add(itsPaneOverlay.getContent(), 0, 0);
		itsPressManager = new PressManager(manager.getMainFrame());

		itsGlobalBarLength = 64;
	}
	
	public int getMuteState() {
		return itsTrackControlPane.getMuteState();
	}
	
	public void setMuteState(int state) {
		itsTrackControlPane.setMuteState(state);
	}
	
	public int[] getPhaseState() {
		return itsTrackControlPane.getPhaseState();
	}
	
	public void setPhaseState(int[] phaseState) {
		itsTrackControlPane.setPhaseState(phaseState);
	}
	
	/**	Deal with a clock message: send the clock to all tracks. We wrap by global
	 	bar length. */
	
	public void clock(int absolutePos) {
		itsTrackControlPane.cuePhases(itsGlobalBarLength, absolutePos);

		for (TrackDriver t: itsTracks) {
			Integer step00 = t.clock00(itsGlobalBarLength, absolutePos);
			if (step00 != null) { itsPaneOverlay.showTimeLine(t, step00); }
		}
	}

	public void press(int x, int y, int how) {
		itsPressManager.press(x, y, how);
	}
	
	/**	Scroll directly to track control pane. In practice we don't (yet)
	 	use this (except in unit testing).
	 */

	public void showTrackControl() {
		itsPaneSwitcher.scrollToPane(itsTrackControlPane, IPaneSwitcher.Direction.UP);
	}
	
	public void shift(boolean how) {
		if (how) {
			itsPaneSwitcher.scrollToPane(itsMenuBarPane, IPaneSwitcher.Direction.UP);
		} else {
//			itsPaneSwitcher.scrollToPane(itsTrackControlPane, IPaneSwitcher.Direction.DOWN);
			itsPaneSwitcher.scrollToPane(itsMenuBarPane.getCurrentPane(), IPaneSwitcher.Direction.DOWN);
		}
	}
}
