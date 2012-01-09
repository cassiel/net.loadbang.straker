//	$Id$
//	$Source$

package net.loadbang.straker;

import net.loadbang.shado.Frame;
import net.loadbang.shado.IPressRouter;


public class FudgeTrack extends Track {
	private int itsStep = -1;
	private int itsBasePitch = 36;
	private int itsPitch = itsBasePitch;
	private Frame itsFrame = new Frame();
	
	private IContentPane itsTestPanes[];
	private String itsName;
	
	public FudgeTrack(String name) {
		itsTestPanes = new IContentPane[2];
		itsTestPanes[0] = makePane("AA");
		itsTestPanes[1] = makePane("BB");
		itsName = name;
	}
	
	private IContentPane makePane(final String glyph) {
		return new IContentPane() {
			public Integer getTimeLine00(int globalBarLength, int absolutePos) {
				return null;
			}

			public IPressRouter getContent() {
				return itsFrame;
			}

			public void setFocussed(boolean how) {
			}

			public void setVisible(boolean how) {
			}

			public String getGlyph() {
				return glyph;
			}
		};
	}

	public void atStep(int n) {
		itsStep  = n;
		noteOut(itsPitch, 64, 250);
	}

	public int getLoopLength() {
		return 16;
	}

	public int getNumPanes() {
		return 2;
	}

	public IContentPane getPane(int n) {
		return itsTestPanes[n];
	}

	@Deprecated
	public int getTimeBase() {
		return 4;
	}

	public void setPhase(int phase) {
		System.out.println("setPhase " + phase);
		itsPitch = itsBasePitch + phase * 12;
	}

	public void subStep(int n) {
		if (itsStep == 3 && n == 2) {
			noteOut(itsBasePitch, 64, 250);
		}
	}

	public String getName() {
		return itsName;
	}

	public Object getState00() { return null; }
}
