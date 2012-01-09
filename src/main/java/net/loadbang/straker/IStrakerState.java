//	$Id$
//	$Source$

package net.loadbang.straker;

public interface IStrakerState {
	/**	Is the (external) clock running? Needed when deciding how to cue phases. */
	boolean isClockRunning();
}
