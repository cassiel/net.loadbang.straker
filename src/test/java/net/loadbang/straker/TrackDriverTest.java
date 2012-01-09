//	$Id$
//	$Source$

package net.loadbang.straker;

import net.loadbang.shado.IRenderer;
import net.loadbang.shadox.DisplayTaskManager;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class TrackDriverTest {
	private Mockery itsContext = new JUnit4Mockery();

	@Test
	public void willLocateToZero() throws Exception {
		final IRenderer renderer = itsContext.mock(IRenderer.class);
		final ITrack track = itsContext.mock(ITrack.class);
		final IChannelledMidiSender midiSender = itsContext.mock(IChannelledMidiSender.class);
		
		itsContext.checking(new Expectations() {{
			ignoring(track).setDriver(with(any(TrackDriver.class)));
			one(track).getTimeBase(); will(returnValue(4));
			one(track).getLoopLength(); will(returnValue(8));
			one(track).atStep(0);
			one(track).subStep(0);
		}});

		DisplayTaskManager manager = new DisplayTaskManager(renderer, 50);
		TrackDriver driver = new TrackDriver(manager, midiSender, 1, track);
		driver.clock00(9999999, 0);
	}
	
	@Test
	public void willOnlyStepWhenNecessary() throws Exception {
		final IRenderer renderer = itsContext.mock(IRenderer.class);
		final ITrack chan = itsContext.mock(ITrack.class);
		final IChannelledMidiSender midiSender = itsContext.mock(IChannelledMidiSender.class);
		
		itsContext.checking(new Expectations() {{
			ignoring(chan).setDriver(with(any(TrackDriver.class)));
			atLeast(1).of(chan).getTimeBase(); will(returnValue(4));
			atLeast(1).of(chan).getLoopLength(); will(returnValue(8));
			one(chan).atStep(0);
			one(chan).subStep(0);
			one(chan).subStep(1);
		}});

		DisplayTaskManager manager = new DisplayTaskManager(renderer, 50);
		TrackDriver driver = new TrackDriver(manager, midiSender, 1, chan);
		driver.clock00(9999999, 0);
		driver.clock00(9999999, 1);
	}
	
	@Test
	public void willJumpToLocation() throws Exception {
		final IRenderer renderer = itsContext.mock(IRenderer.class);
		final ITrack chan = itsContext.mock(ITrack.class);
		final IChannelledMidiSender midiSender = itsContext.mock(IChannelledMidiSender.class);
		
		itsContext.checking(new Expectations() {{
			ignoring(chan).setDriver(with(any(TrackDriver.class)));
			one(chan).getTimeBase(); will(returnValue(4));
			one(chan).getLoopLength(); will(returnValue(8));
			one(chan).atStep(1);
			one(chan).subStep(1);
		}});

		DisplayTaskManager manager = new DisplayTaskManager(renderer, 50);
		TrackDriver driver = new TrackDriver(manager, midiSender, 1, chan);
		driver.clock00(9999999, 5);
	}
	
	//	TODO: some tests for the gbar machinery.
}
