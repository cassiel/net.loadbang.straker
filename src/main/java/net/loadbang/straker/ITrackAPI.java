//	$Id$
//	$Source$

package net.loadbang.straker;

import net.loadbang.shadox.DisplayTaskManager;

/**	Interface for sending MIDI (more correctly, for
 	creating MIDI-based events), and for other stuff
 	we might want to get from the Python code for a Track.

	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net
 */

public interface ITrackAPI {
	void noteOut(int pitch, int velocity, int durationMSec);
	void ctrlOut(int controlNum, int value);
	DisplayTaskManager getDisplayManager();
}
