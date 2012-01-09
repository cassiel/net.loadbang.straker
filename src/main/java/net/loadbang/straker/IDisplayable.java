//	$Id$
//	$Source$

package net.loadbang.straker;

import net.loadbang.shado.IPressRouter;

public interface IDisplayable {
	/**	Get the main content renderable (frame, whatever). */
	IPressRouter getContent();

}
