//	$Id$
//	$Source$

package net.loadbang.straker.widgets;

import net.loadbang.shado.Block;
import net.loadbang.shado.exn.OperationException;
import net.loadbang.shado.types.LampState;
import net.loadbang.shadox.DisplayTaskManager;
import net.loadbang.shadox.XFrame;
import net.loadbang.shadox.DisplayTaskManager.ScheduleTime;

/**	A widget which presents a row of LEDs as a radio-button selection. The length
 	of the widget can be changed dynamically. Abstract because it needs callback
 	routine for selection.

	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net
 */

abstract public class LinearSelector extends XFrame {
	private Block itsUnderlayBlock00;
	private Block itsCursor;
	
	public LinearSelector(DisplayTaskManager manager, int width, ScheduleTime t) {
		super(manager);
		
		itsCursor = new Block(1, 1).fill(LampState.FLIP);
		
		try {
			add(itsCursor, 0, 0);
		} catch (OperationException e) {
			e.printStackTrace();
		}

		blink(itsCursor, 5, 3, t);

		setWidth(width);
	}
	
	/**	Change the width of the selector. This removes any
	 	current selection.
	 	
	 	@param newWidth the new width
	 */

	public void setWidth(int newWidth) {
		try {
			setupUnderlay(newWidth);
		} catch (OperationException e) {
			e.printStackTrace();
		}

		setSelection(-1);		
	}
	
	private void setupUnderlay(int width) throws OperationException {
		if (itsUnderlayBlock00 != null) {
			remove(itsUnderlayBlock00);
		}
		
		itsUnderlayBlock00 =
			new Block(width, 1) {
				@Override
				public boolean press(int x, int y, int how) {
					if (how != 0) {
						select(x);
					}

					return true;
				}
			}.fill(LampState.ON);
		
		add(itsUnderlayBlock00, 0, 0);
	}
	
	/**	Change the current displayed selection.

	 	@param pos the new selection (0..width-1), or -1 for none.
	 */

	public void setSelection(int pos) {
		try {
			if (pos < 0) {		//	Hide selection.
				moveTo(itsCursor, 0, 0);
				bottom(itsCursor);
			} else {
				moveTo(itsCursor, pos, 0);
				top(itsCursor);
			}
		} catch (OperationException e) {
			e.printStackTrace();
		}
	}
	
	/**	Called when a selection is made. The callee is responsible
	 	for updating the selection (via {@link setSelection}) - there
	 	is no local control.
	 	
	 	@param pos the selection which was made (0..width-1).
	 */

	abstract protected void select(int pos);
}
