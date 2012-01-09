//	$Id$
//	$Source$

package net.loadbang.straker;

import java.util.ArrayList;
import java.util.List;

import net.loadbang.shado.IRenderer;
import net.loadbang.shadox.DisplayTaskManager;
import net.loadbang.shadox.XFrame;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class BankTest {
	private Mockery itsContext = new JUnit4Mockery();

	@Test
	public void canChangePhase() throws Exception {
		final IRenderer renderer = itsContext.mock(IRenderer.class);
		final IChannelledMidiSender midiSender = itsContext.mock(IChannelledMidiSender.class);
		DisplayTaskManager manager = new DisplayTaskManager(renderer, 50);
		final ITrack track = itsContext.mock(ITrack.class);
		final IContentPane pane = itsContext.mock(IContentPane.class);
		final IStateHinter hinter = itsContext.mock(IStateHinter.class);
		final IStrakerState state = itsContext.mock(IStrakerState.class);
		
		itsContext.checking(new Expectations() {{
			allowing(track).setDriver(with(any(TrackDriver.class)));
			allowing(track).getNumPanes(); will(returnValue(10));
			allowing(track).getPane(with(any(Integer.class))); will(returnValue(pane));
			allowing(pane).getGlyph(); will(returnValue("--"));
			allowing(track).getTimeBase(); will(returnValue(4));
			allowing(track).getLoopLength(); will(returnValue(16));
			allowing(state).isClockRunning(); will(returnValue(true));
			ignoring(track).atStep(0);
			ignoring(track).subStep(with(any(Integer.class)));
			ignoring(renderer).render(with(any(XFrame.class)));
			ignoring(hinter).selectingPane(with(any(String.class)));
			ignoring(hinter).selectingTrack(with(any(Integer.class)));
			one(track).setPhase(5);
		}});

		List<ITrack> tracks = new ArrayList<ITrack>();
		tracks.add(track);
		
		Bank bank = new Bank(manager, midiSender, tracks, hinter, state);
		
		bank.clock(0);
		
		bank.showTrackControl();		//	Allow the press to get routed!
		Thread.sleep(500);
		
		bank.press(0, 7, 1);			//	Bottom row: phase 0 to phase 5.
		bank.clock(64 * 4);				//	Note: dependent on a small enough global bar length.
	}
	
	@Test
	public void canChangePhaseImmediatelyWhenStopped() throws Exception {
		final IRenderer renderer = itsContext.mock(IRenderer.class);
		final IChannelledMidiSender midiSender = itsContext.mock(IChannelledMidiSender.class);
		DisplayTaskManager manager = new DisplayTaskManager(renderer, 50);
		final ITrack track = itsContext.mock(ITrack.class);
		final IContentPane pane = itsContext.mock(IContentPane.class);
		final IStateHinter hinter = itsContext.mock(IStateHinter.class);
		final IStrakerState state = itsContext.mock(IStrakerState.class);
		
		itsContext.checking(new Expectations() {{
			allowing(track).setDriver(with(any(TrackDriver.class)));
			allowing(track).getNumPanes(); will(returnValue(10));
			allowing(track).getPane(with(any(Integer.class))); will(returnValue(pane));
			allowing(pane).getGlyph(); will(returnValue("--"));
			allowing(state).isClockRunning(); will(returnValue(false));
			ignoring(renderer).render(with(any(XFrame.class)));
			ignoring(hinter).selectingPane(with(any(String.class)));
			ignoring(hinter).selectingTrack(with(any(Integer.class)));
			one(track).setPhase(5);
		}});

		List<ITrack> tracks = new ArrayList<ITrack>();
		tracks.add(track);
		
		Bank bank = new Bank(manager, midiSender, tracks, hinter, state);
		
		bank.showTrackControl();		//	Allow the press to get routed!
		Thread.sleep(500);
		
		bank.press(0, 7, 1);			//	Bottom row: phase 0 to phase 5.
		//	...and don't move the clock position at all.
	}
}
