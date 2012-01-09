//	$Id$
//	$Source$

package net.loadbang.straker;

import net.loadbang.shado.Frame;
import net.loadbang.shado.IRenderable;
import net.loadbang.shado.IRenderer;
import net.loadbang.shadox.DisplayTaskManager;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class PaneSwitcherTest {
	private Mockery itsContext = new JUnit4Mockery();
	
	public interface INotifiablePane_1 extends INotifiablePane { };
	public interface INotifiablePane_2 extends INotifiablePane { };

	@Test
	public void willSetVisibleAndFocussed() throws Exception {
		final Sequence seq = itsContext.sequence("S");
		final INotifiablePane pane1 = itsContext.mock(INotifiablePane_1.class);
		final INotifiablePane pane2 = itsContext.mock(INotifiablePane_2.class);
		final Frame f1 = new Frame(), f2 = new Frame();
		final IRenderer renderer = itsContext.mock(IRenderer.class);
		DisplayTaskManager manager = new DisplayTaskManager(renderer, 10);
		
		itsContext.checking(new Expectations() {{
			one(pane1).setVisible(true); inSequence(seq);
			one(pane1).setFocussed(true); inSequence(seq);
			
			one(pane1).setFocussed(false); inSequence(seq);
			one(pane2).setVisible(true); inSequence(seq);
			one(pane1).setVisible(false); inSequence(seq);
			one(pane2).setFocussed(true); inSequence(seq);
			
			ignoring(pane1).getContent(); will(returnValue(f1));
			ignoring(pane2).getContent(); will(returnValue(f2));
			
			ignoring(renderer).render(with(any(IRenderable.class)));
		}});
	
		IPaneSwitcher p = new PaneSwitcher(manager, 16, 8);
		p.scrollToPane(pane1, IPaneSwitcher.Direction.DOWN);
		Thread.sleep(500);
		p.scrollToPane(pane2, IPaneSwitcher.Direction.UP);
		Thread.sleep(500);
	}
	
	@Test
	public void canSkipScrollToSamePane() throws Exception {
		final INotifiablePane pane1 = itsContext.mock(INotifiablePane_1.class);
		final INotifiablePane pane2 = itsContext.mock(INotifiablePane_2.class);
		final Frame f1 = new Frame(), f2 = new Frame();
		final IRenderer renderer = itsContext.mock(IRenderer.class);
		DisplayTaskManager manager = new DisplayTaskManager(renderer, 50);
		
		itsContext.checking(new Expectations() {{
			ignoring(pane1).setVisible(with(any(boolean.class)));
			ignoring(pane1).setFocussed(with(any(boolean.class)));
			ignoring(pane2).setVisible(with(any(boolean.class)));
			ignoring(pane2).setFocussed(with(any(boolean.class)));

			ignoring(pane1).getContent(); will(returnValue(f1));
			ignoring(pane2).getContent(); will(returnValue(f2));
			
			ignoring(renderer).render(with(any(IRenderable.class)));
		}});
	
		IPaneSwitcher p = new PaneSwitcher(manager, 16, 8);
		p.scrollToPane(pane1, IPaneSwitcher.Direction.DOWN);
		Thread.sleep(100);			//	Mid-scroll.
		p.scrollToPane(pane2, IPaneSwitcher.Direction.UP);
		Thread.sleep(100);			//	Mid-scroll.
		p.scrollToPane(pane1, IPaneSwitcher.Direction.DOWN);
		Thread.sleep(500);
	}
}
