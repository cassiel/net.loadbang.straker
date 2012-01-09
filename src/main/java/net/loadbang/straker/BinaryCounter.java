//	$Id$
//	$Source$

package net.loadbang.straker;

import net.loadbang.shado.Block;
import net.loadbang.shado.exn.RangeException;
import net.loadbang.shado.types.LampState;

/**	A binary counter column, LSB at the top.

	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net
 */

public class BinaryCounter extends Block {
	public BinaryCounter() {
		super(1, 8);
	}

	public void setValue(int n) throws RangeException {
		for (int i = 0; i < 8; i++) {
			setLamp(0, i, ((n >> i) & 1) != 0 ? LampState.ON : LampState.OFF);
		}
	}
}
