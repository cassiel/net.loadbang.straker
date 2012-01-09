//	$Id: Straker.java,v adff71a130cc 2011/10/10 21:38:35 nick $
//	$Source: /Users/nick/workspace/MaxMSP/DEVELOPMENT_0/mxj-development/straker/java/net/loadbang/straker/Straker.java,v $

package net.loadbang.straker;

import java.net.UnknownHostException;
import java.util.List;

import net.loadbang.osc.exn.CommsException;
import net.loadbang.shado.IBinaryOutputter;
import net.loadbang.shado.MonomeSerialOSCOutputter;
import net.loadbang.shado.BinaryRenderer;
import net.loadbang.shado.SerialOSCBinaryOutputter;
import net.loadbang.shado.exn.OperationException;
import net.loadbang.shadox.DisplayTaskManager;
import net.loadbang.straker.exn.DataException;
import net.loadbang.straker.util.StrakerProperties;

/**	The main container for the straker sequencer.

	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net
 */

public class Straker implements IStrakerState {
	/**	For now: a single bank of channels. */
	private DisplayTaskManager itsManager;
	private Bank itsBank;
	private boolean itsClockRunning = false;
	
	public Straker(IChannelledMidiSender midiSender,
				   List<ITrack> tracks,
				   IStateHinter hinter
				  ) throws DataException, OperationException {
		StrakerProperties props = new StrakerProperties();
		
		int width = props.getMonomeWidth();
		String host = props.getMonomeHost();
		int port = props.getMonomePort();
		String prefix = props.getMonomePrefix();

		IBinaryOutputter outputter;
		
		try {
			//	We're now using the new SerialOSC machinery, so new protocol generator:
			outputter = new SerialOSCBinaryOutputter(host, port, width, prefix);
		} catch (UnknownHostException e) {
			throw new OperationException("unknown host: " + host, e);
		} catch (CommsException e) {
			throw new OperationException("comms", e);
		}

		BinaryRenderer renderer = new BinaryRenderer(width, props.getMonomeHeight(), outputter);

		itsManager = new DisplayTaskManager(renderer, props.getTickIntervalMSec());
		
		if (tracks.size() == 0) {
			tracks.add(new VoidTrack("VOID"));	//	I know - side-effects the caller - but you
												//	shouldn't do this in the first place.
		}
		
		for (ITrack t: tracks) { t.setupWithManager(itsManager); }

		itsBank = new Bank(itsManager, midiSender, tracks, hinter, this);
	}
	
	/**	Get the mute state (called from Python when the setup is saved). */
	
	public int getMuteState() {
		return itsBank.getMuteState();
	}
	
	/**	Set the mute state (called from Python when the setup is restored). */

	public void setMuteState(int state) {
		itsBank.setMuteState(state);
	}
	
	public int getNumPhases() {
		return itsBank.getPhaseState().length;
	}
	
	public int[] getPhaseState() {
		return itsBank.getPhaseState();
	}
	
	public void setPhaseState(int[] phaseState) {
		itsBank.setPhaseState(phaseState);
	}
	public void clock(int pos) {
		itsBank.clock(pos);
	}

	public void shutdown() {
		System.out.println("Straker: shutdown");
		itsManager.clear();
	}

	public void running(boolean how) {
		itsClockRunning = how;
	}

	public void press(int x, int y, int how) {
		itsBank.press(x, y, how);
	}

	public void shift(int x) {
		itsBank.shift(x != 0);
	}

	public boolean isClockRunning() {
		return itsClockRunning;
	}
}
