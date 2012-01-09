//	$Id$
//	$Source$

package net.loadbang.straker;

/**	Interface for sending MIDI (more correctly, for
 	creating MIDI-based events).

	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net
 */

public interface IChannelledMidiSender {
	void noteOut(int channel, int pitch, int velocity, int durationMSec);
	void ctrlOut(int channel, int controlNum, int value);
}
