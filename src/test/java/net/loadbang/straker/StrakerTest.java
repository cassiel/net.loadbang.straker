//	$Id$
//	$Source$

package net.loadbang.straker;

import java.util.ArrayList;
import java.util.List;

import net.loadbang.shadox.DisplayTaskManager;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class StrakerTest {
	private Mockery itsContext = new JUnit4Mockery();

	@Test
	public void strakerInitialisesTracksWithDisplayTaskManager() throws Exception {
		final ITrack track = itsContext.mock(ITrack.class);
		IChannelledMidiSender midiSender = itsContext.mock(IChannelledMidiSender.class);
		final IContentPane pane = itsContext.mock(IContentPane.class);
		final IStateHinter hinter = itsContext.mock(IStateHinter.class);

		itsContext.checking(new Expectations() {{
			ignoring(track).setDriver(with(any(TrackDriver.class)));
			allowing(track).getNumPanes(); will(returnValue(1));
			allowing(track).getPane(with(any(Integer.class))); will(returnValue(pane));
			allowing(pane).getGlyph(); will(returnValue("--"));
			ignoring(hinter).selectingPane(with(any(String.class)));
			ignoring(hinter).selectingTrack(with(any(Integer.class)));
			one(track).setupWithManager(with(any(DisplayTaskManager.class)));
		}});
		
		List<ITrack> tracks = new ArrayList<ITrack>();
		tracks.add(track);

		/*ignore*/ new Straker(midiSender, tracks, hinter);
	}
}
