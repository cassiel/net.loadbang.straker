//	$Id$
//	$Source$

package net.loadbang.straker.assets;

import net.loadbang.osc.exn.DataException;
import net.loadbang.shado.Block;
import net.loadbang.shado.IPressRouter;
import net.loadbang.straker.INotifiablePane;

public class Glyphs {
	protected static Block makeBlock(String spec) {
		try {
			return new Block(spec);
		} catch (DataException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected static INotifiablePane makePane(String spec) {
		final Block b = makeBlock(spec);

		return new INotifiablePane() {
			public IPressRouter getContent() { return b; }
			public void setFocussed(boolean how) { }
			public void setVisible(boolean how) { }
			public String getGlyph() { return ""; }
		};
	}
}
