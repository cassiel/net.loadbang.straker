//	$Id$
//	$Source$

package net.loadbang.straker;

import net.loadbang.shadox.DisplayTaskManager;


/**	Abstract superclass which provides utility methods.

	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net
 */

public abstract class Track implements ITrack {
	private ITrackAPI itsTrackAPI;
	
	/**	A "late" initialisation with the display task manager. By
	 	default we don't care about it.
 	
 		@param manager the display task manager.
	 */
	
	public void setupWithManager(DisplayTaskManager manager) { }

	public void setDriver(ITrackAPI trackAPI) {
		itsTrackAPI = trackAPI;
	}
	
	protected void noteOut(int pitch, int velocity, int durationMSec) {
		itsTrackAPI.noteOut(pitch, velocity, durationMSec);
	}
	
	protected void ctrlOut(int controlNum, int value) {
		itsTrackAPI.ctrlOut(controlNum, value);
	}
}
