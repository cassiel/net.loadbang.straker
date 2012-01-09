//	$Id$
//	$Source$

package net.loadbang.straker;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import net.loadbang.shado.Frame;
import net.loadbang.shado.IPressRouter;
import net.loadbang.shado.ViewPort;
import net.loadbang.shado.exn.OperationException;
import net.loadbang.shadox.DisplayTaskManager;
import net.loadbang.shadox.ICompletion;
import net.loadbang.shadox.XFrame;

/**	Scrolling pane implementation. This implementation allows a
 	scroll area which is smaller than the entire device, for
 	building menu systems.
 	
 	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net
 */

public class PaneSwitcher implements IPaneSwitcher, INotifiablePane, Runnable {
	/**	The currently focussed pane, if any. */
	private INotifiablePane itsCurrentPane00;
	
	/**	The 2-up frame we use for scrolling. By convention, after
	 	any scroll, the newly active frame is repositioned to (0, 0). */

	private Frame its2UpFrame;
	
	/**	The enclosing frame, within which we scroll the 2-up. */
	private XFrame itsScrollerFrame;
	
	/**	The ViewPort for cropping the frame (and ignoring presses). */
	private ViewPort itsViewPort;
	
	private DisplayTaskManager itsManager;

	final private int itsPortWidth;
	final private int itsPortHeight;
	
	private class ScrollQueueEntry {
		INotifiablePane itsNewPane;
		Direction itsDirection;
		
		ScrollQueueEntry(INotifiablePane newPane, Direction direction) {
			this.itsNewPane = newPane;
			this.itsDirection = direction;
		}
	}
	
	/**	A strictly bounded queue of scroll requests. Since we only
		really care about the most recent scroll request, this queue
		has capacity of 1 and is cleared before each item is added.
	 */

	private BlockingQueue<ScrollQueueEntry> itsScrollQueue;
	
	/**	Semaphore to suspend the scroll queue servicer while the
	 	main worker thread is actually doing a scroll.
	 */
	
	private class ActiveSemaphore { boolean itsActive; }
	private ActiveSemaphore itsScrollActive = new ActiveSemaphore();
	
	/**	Build a pane switcher on a particular slice of the display; this
		assumes that we want to reserve the bottom for fixed buttons.
		The scrolling distance (vertically) is reduced to be the port
		size, not the entire device size.
 	
	 	@param manager the display manager (for animation)
	 	@param width the scrolling area's width
		@param height the scrolling area's height
	 */
	
	public PaneSwitcher(DisplayTaskManager manager, int width, int height) {
		itsPortWidth = width;
		itsPortHeight = height;

		itsCurrentPane00 = null;
		its2UpFrame = new Frame();
		
		itsScrollerFrame = new XFrame(manager);
		
		itsViewPort = new ViewPort(itsScrollerFrame, 0, 0, itsPortWidth, height);

		try {
			itsScrollerFrame.add(its2UpFrame, 0, 0);
		} catch (OperationException e) {
			e.printStackTrace();
		}

		itsManager = manager;
		itsScrollQueue = new ArrayBlockingQueue<ScrollQueueEntry>(1);
		
		Thread t = new Thread(this, "pane switcher: " + this);
		t.setDaemon(true);
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();
	}
	
	public IPressRouter getContent() {
		return itsViewPort;
	}
	
	/**	Kick off a scroll to the new pane. The current one, if any, will
	 	be at (0, 0). */
	
	public void scrollToPane(final INotifiablePane newPane, Direction direction) {
		itsScrollQueue.clear();
		itsScrollQueue.add(new ScrollQueueEntry(newPane, direction));
	}
	
	/**	The task for the scrolling worker. Once it manages to get a scrolling
	 	task from the queue, it kicks off the scroll (using the shado scheduler)
	 	and waits for the scroll to signal completion.
	 */

	private void scrollTask() throws InterruptedException {
		ScrollQueueEntry item = itsScrollQueue.take();
		
		if (item.itsNewPane != itsCurrentPane00) {		
			synchronized (itsScrollActive) {
				itsScrollActive.itsActive = true;
				runScrollJob(item.itsNewPane, item.itsDirection);
				
				while (itsScrollActive.itsActive) {
					itsScrollActive.wait();
				}
			}
		}
	}

	public void runScrollJob(final INotifiablePane newPane, Direction direction) {
		//	negation: turn content scrolling sense into position sense of new content.
		int newX = -direction.getXSense() * itsPortWidth;
		int newY = -direction.getYSense() * itsPortHeight;
		
		ICompletion completion =
			new ICompletion() {
				//	TODO: think about interlock between completion and initiation.
				public void completed(boolean how) {
					try {
						//	Remove the old pane from the composite frame:
						if (itsCurrentPane00 != null) {
							itsCurrentPane00.setVisible(false);
							its2UpFrame.remove(itsCurrentPane00.getContent());
						}
						
						//	Establish the new pane:
						itsCurrentPane00 = newPane;
						
						//	Normalise origins:
						its2UpFrame.moveTo(newPane.getContent(), 0, 0);
						itsScrollerFrame.moveTo(its2UpFrame, 0, 0);
						
						itsCurrentPane00.setFocussed(true);
					} catch (OperationException exn) {
						exn.printStackTrace();
					}

					synchronized (itsScrollActive) {
						itsScrollActive.itsActive = false;
						itsScrollActive.notify();
					};
				}
			};
			
		if (itsCurrentPane00 != null) {
			itsCurrentPane00.setFocussed(false);
		}
		
		newPane.setVisible(true);
		
		try {
			its2UpFrame.add(newPane.getContent(), newX, newY);
			
			if (direction.getXSense() != 0) {
				itsScrollerFrame.scrollX(its2UpFrame, -newX, 1, itsManager.newTime(0), completion);
			} else {
				itsScrollerFrame.scrollY(its2UpFrame, -newY, 1, itsManager.newTime(0), completion);
			}
		} catch (OperationException exn) {
			exn.printStackTrace();
		}
	}

	public INotifiablePane getCurrentPane00() {
		return itsCurrentPane00;
	}

	public void run() {
		while (true) {
			try { scrollTask(); } catch (InterruptedException e) { e.printStackTrace(); }
		}
	}

	public void setFocussed(boolean how) { }
	public void setVisible(boolean how) { }

	public String getGlyph() { return ""; }
}
