//	$Id$
//	$Source$

package net.loadbang.straker;

import net.loadbang.shado.Frame;
import net.loadbang.shado.IPressRouter;

/**	A void channel with one empty page, and which does nothing. */

public class VoidTrack extends Track {
	private Frame itsEmptyFrame = new Frame();
	private String itsName;

	public VoidTrack(String name) {
		itsName = name;
	}

	public void atStep(int n) {	}

	public int getLoopLength() { return 1; }

	public int getNumPanes() { return 1; }

	public IContentPane getPane(int n) {
		return new IContentPane() {
			public Integer getTimeLine00(int globalBarLength, int absolutePos) {
				return null;
			}

			public IPressRouter getContent() {
				return itsEmptyFrame;
			}

			public void setFocussed(boolean how) { }

			public void setVisible(boolean how) { }

			public String getGlyph() {
				return "--!";
			}
		};
	}

	public int getTimeBase() { return 1; }

	public void subStep(int n) { }

	public void setPhase(int phase) { }

	public String getName() {
		return itsName;
	}
	
	public Object getState00() { return null; }
}
