//	$Id$
//	$Source$

package net.loadbang.straker.mxj;

import java.util.ArrayList;
import java.util.List;

import net.loadbang.shado.exn.OperationException;
import net.loadbang.straker.FudgeTrack;
import net.loadbang.straker.IChannelledMidiSender;
import net.loadbang.straker.ITrack;
import net.loadbang.straker.Straker;
import net.loadbang.straker.VoidTrack;
import net.loadbang.straker.exn.DataException;
import net.loadbang.util.EnrichedMaxObject;

/**	This is a test MXJ object for use when developing and debugging straker;
 	eventually, the intention is that straker is configured and instantiated
 	from Python.
 	
	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net
 */

public class StrakerMXJWrapper extends EnrichedMaxObject implements IChannelledMidiSender {
	private Straker itsStraker;

	public StrakerMXJWrapper() {
		super("$Id$",
			  "net.loadbang.straker",
			  StrakerMXJWrapper.class
		 	 );
		
		List<ITrack> tracks = new ArrayList<ITrack>();
		tracks.add(new FudgeTrack("F1"));
		tracks.add(new FudgeTrack("F2"));
		tracks.add(new VoidTrack("V1"));
		tracks.add(new VoidTrack("V2"));
		
		try {
			itsStraker = new Straker(this, tracks, null);
		} catch (DataException exn) {
			getLogger().error("data error", exn);
			bail("[StrakerTest]: data error");
		} catch (OperationException exn) {
			getLogger().error("operation error", exn);
			bail("[StrakerTest]: operation error");
		}
	}
	
	@Override
	public void inlet(int pos) {
		itsStraker.clock(pos);
	}
	
	public void running(int how) {
		itsStraker.running(how != 0);
	}
	
	public void press(int x, int y, int how) {
		itsStraker.press(x, y, how);
	}
	
	public void shift(int x) {
		itsStraker.shift(x);
	}
	
	@Override
	public void notifyDeleted() {
		itsStraker.shutdown();
	}

	public void noteOut(int channel, int pitch, int velocity, int durationMSec) {
		outletHigh(0, new int[] { pitch, velocity, durationMSec, channel });
	}

	public void ctrlOut(int channel, int controlNum, int value) {
		outletHigh(1, new int[] { value, controlNum, channel });
	}
}
