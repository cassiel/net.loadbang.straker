//	$Id$
//	$Source$

package net.loadbang.straker;

import net.loadbang.shadox.DisplayTaskManager;


/**	Interface for a track object; we will have some simple Java
	implementations, but the idea is that they should be implemented
	in Python for full generality.

	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net
 */

public interface ITrack {
	/**	A "late" initialisation with the display task manager (which isn't
	 	available when we first create the tracks - straker's constructor
	 	takes the tracks first).
	 	
	 	@param manager the display task manager.
	 */

	void setupWithManager(DisplayTaskManager manager);
	
	/**	Provide information about the pages we can display. */
	
	int getNumPanes();
	
	/**	Get the pane for the N'th page.

	 	@param n page number, 0..N-1
		@return a full content pane
	 */
	
	IContentPane getPane(int n);
	
	/**	Switch to the N'th phase. (Currently, we allow six!). We
	 	expect this to happen just before we would get an atStep(0).
	 	TODO: can the loop, timebase etc. be different in different
	 	phases?

		@param phase the phase number, 0..N (current range is six).
	 */

	void setPhase(int phase);
	
	/**	Deliver the timebase, in sub-steps. This is the only clock-divider
	 	functionality provided here; sub-steps are fired at the rate of the
	 	incoming clock. Obviously, the faster the timebase,
		the fewer subdivisions we'll have available.
	 */
	
	int getTimeBase();
	
	/**	Deliver the loop length, in whole steps. */
	
	int getLoopLength();
	
	/**	Accept a step call with a current step number (0..looplength-1). Don't
	 	do output at this stage: wait for the next subStep. */

	void atStep(int n);
	
	/**	Accept a sub-step call (0..timeBase-1). subStep(0) is normally
		called just after atStep(n), although the sub-step might be different
		if we've moved the locator somewhere.
	 */

	void subStep(int n);

	void setDriver(ITrackAPI midiSender);
	
	/**	Return a (unique) name for this track: the name can be used as a key
	 	for storage.
	 */

	String getName();
	
	/**	Generate a storable (via pickle in Python) state for the track. We
	 	can't return anything in Java since Jython won't pickle it, so the
	 	only valid Java implementations will return null.

	 	@return null - always! - Jython implementations will override.
	 */
	
	Object getState00();
}
