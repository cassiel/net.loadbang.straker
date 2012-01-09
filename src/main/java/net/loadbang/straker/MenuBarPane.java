//	$Id$
//	$Source$

package net.loadbang.straker;

import net.loadbang.shado.Frame;
import net.loadbang.shado.IPressRouter;
import net.loadbang.shado.exn.OperationException;
import net.loadbang.shadox.DisplayTaskManager;
import net.loadbang.shadox.DisplayTaskManager.ScheduleTime;
import net.loadbang.straker.IPaneSwitcher.Direction;
import net.loadbang.straker.assets.PaneGlyphs;
import net.loadbang.straker.assets.TrackGlyphs;
import net.loadbang.straker.widgets.LinearSelector;

/**	The central pane for presenting an array of icon images for tracks.
 	The control is notionally two-dimensional: we can select tracks, and
 	for any track we can select a particular pane.
 	
 	The MenuBarPane is notifiable because we scroll to/from it. (We
 	might at some stage do something with the visible/focussed
 	calls.)
 	
 	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net
 */

public class MenuBarPane implements INotifiablePane {
	static private final int TRACK_ROW = 6;
	static private final int PAGE_ROW = 7;
	
	private TrackDriver[] itsDrivers;
	private Frame itsFrame;
	private IPaneSwitcher itsTrackScroller, itsIdentScroller;
	private int itsCurrentTrackNum;
	
	/**	Track the pane index purely to get scrolling in the right direction. */
	private int itsCurrentPaneIndex;
	private LinearSelector itsPaneSelector;
	private IPressRouter itsTrackSelector;
	private IContentPane[] itsSystemPanes;
	private IStateHinter itsHinter;
	
	public MenuBarPane(DisplayTaskManager manager,
					   IContentPane[] systemPanes,
					   TrackDriver[] trackDrivers,
					   IStateHinter hinter
					  )
		throws OperationException
	{
		itsFrame = new Frame();
		itsSystemPanes = systemPanes;
		itsDrivers = trackDrivers;
		
		itsHinter = hinter;
		
		itsTrackScroller =
			new PaneSwitcher(manager, 5, 5);	//	TODO: manifest!
		
		itsIdentScroller =
			new PaneSwitcher(manager, 7, 5);
		
		ScheduleTime now = manager.newTime(0);
		
		itsPaneSelector = makePaneSelector(manager, now);
		itsTrackSelector = makeTrackSelector(manager, trackDrivers.length, now);
		
		//	Note that the selector for tracks is indented: this is so that
		//	the first track selector lines up with the selector for its
		//	first page.

		itsFrame.add(itsIdentScroller.getContent(), 0, 0)
			    .add(itsTrackScroller.getContent(), 11, 0)
			    .add(itsTrackSelector, systemPanes.length, TRACK_ROW)
			    .add(itsPaneSelector, 0, PAGE_ROW);
		
		selectTrack(1);
	}
	
	private LinearSelector makePaneSelector(DisplayTaskManager manager, ScheduleTime start) {
		return new LinearSelector(manager, 0, start) {
			@Override
			protected void select(int pos) {
				selectPane(pos);
				setSelection(pos);
			}
		};
	}

	private IPressRouter makeTrackSelector(DisplayTaskManager manager, int width, ScheduleTime start) {
		return new LinearSelector(manager, width, start) {
			@Override
			protected void select(int pos) {
				selectTrack(pos + 1);
				setSelection(pos);
			}
		};
	}

	/**	Select a pane, complete with animation. (So, it's a slight waste that
		we do that in the constructor.) Oblivious to a pane index out of bounds.
		Sets the remembered track and pane.
		
	 	@param trackNum the track number, 1..16
	 */

	private void selectTrack(int trackNum) {
		TrackDriver driver = itsDrivers[trackNum - 1];
		
		itsHinter.selectingTrack(trackNum);

		scrollToTrack(trackNum);		//	TODO: also show cued (!) phase.
		itsCurrentTrackNum = trackNum;
		
		itsPaneSelector.setWidth(driver.getTrack().getNumPanes() + itsSystemPanes.length);
		selectPane(driver.getCurrentPaneIndex() + itsSystemPanes.length);
	}
	
	private void selectPane(int paneIndex) {
		if (paneIndex >= itsSystemPanes.length) {			//	Non-system selection
			TrackDriver driver = itsDrivers[itsCurrentTrackNum - 1];
			ITrack track = driver.getTrack();
			String glyph = track.getPane(paneIndex - itsSystemPanes.length).getGlyph();
			scrollToPaneGlyph(paneIndex, glyph);
			scrollToTrack(itsCurrentTrackNum);
			driver.setCurrentPaneIndex(paneIndex - itsSystemPanes.length);
			itsHinter.selectingPane(glyph);
		} else {
			String glyph = itsSystemPanes[paneIndex].getGlyph();
			scrollToPaneGlyph(paneIndex, glyph);
			scrollToTrack(-1);
			itsHinter.selectingPane(glyph);
		}

		//setTrackSelectVisible(paneIndex >= itsSystemPanes.length);
		itsPaneSelector.setSelection(paneIndex);
	}

	private void setTrackSelectVisible(boolean how) {
		try {
			if (how) {
				itsFrame.moveTo(itsTrackSelector, itsSystemPanes.length, TRACK_ROW);
			} else {
				itsFrame.moveTo(itsTrackSelector, 0, -1);
			}
		} catch (OperationException e) {
			e.printStackTrace();
		}
	}

	/**	Scroll to a particular pane. If the new pane index is the same
	 	as the old one, but the ident strings differ (which will happen
	 	if scrolling between heterogeneous tracks) we just scroll up.
	 */

	private void scrollToPaneGlyph(int newPaneIndex, String menuIdent) {
		final INotifiablePane pane = PaneGlyphs.getGlyphPane(menuIdent);

		Direction dir = IPaneSwitcher.Direction.UP;
		
		if (newPaneIndex > itsCurrentPaneIndex) {
			dir = IPaneSwitcher.Direction.RIGHT;
		} else if (newPaneIndex < itsCurrentPaneIndex) {
			dir = IPaneSwitcher.Direction.LEFT;
		}

		itsIdentScroller.scrollToPane(pane, dir);
		itsCurrentPaneIndex = newPaneIndex;
	}

	private void scrollToTrack(int trackNum) {
		final INotifiablePane pane = TrackGlyphs.getTrackNumPane(trackNum);
		itsTrackScroller.scrollToPane(
			pane,
			trackNum > itsCurrentTrackNum
				? IPaneSwitcher.Direction.RIGHT
				: IPaneSwitcher.Direction.LEFT
		);
	}

	public void setFocussed(boolean how) {
	}

	public void setVisible(boolean how) {
	}

	public IPressRouter getContent() {
		return itsFrame;
	}

	public INotifiablePane getCurrentPane() {
		if (itsCurrentPaneIndex >= itsSystemPanes.length) {
			return itsDrivers[itsCurrentTrackNum - 1]
			       .getTrack()
			       .getPane(itsCurrentPaneIndex - itsSystemPanes.length);
		} else {
			return itsSystemPanes[itsCurrentPaneIndex];
		}
	}

	public String getGlyph() {
		return "";
	}
}
