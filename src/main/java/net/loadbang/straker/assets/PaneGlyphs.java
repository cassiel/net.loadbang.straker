//	$Id$
//	$Source$

package net.loadbang.straker.assets;

import java.util.HashMap;

import net.loadbang.shado.Block;
import net.loadbang.shado.Frame;
import net.loadbang.shado.IPressRouter;
import net.loadbang.shado.exn.OperationException;
import net.loadbang.shado.types.LampState;
import net.loadbang.straker.INotifiablePane;

/**	A cache of 7 x 5 two-letter combos. We support A-Z only, and
 	even then it's not exhaustive.

	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net
 */

public class PaneGlyphs extends Glyphs {
	private static Block voidBlock = new Block(3, 5).fill(LampState.ON);
	
	private static HashMap<Character, Block> itsChars = new HashMap<Character, Block>();

	static {
	    itsChars.put('A',	makeBlock("111 101 111 101 101"));
	    itsChars.put('B',	makeBlock("110 101 110 101 110"));
	    itsChars.put('C',	makeBlock("111 100 100 100 111"));
	    itsChars.put('D',	makeBlock("110 101 101 101 110"));
	    itsChars.put('E',	makeBlock("111 100 110 100 111"));
	    itsChars.put('F',	makeBlock("111 100 110 100 100"));
	    itsChars.put('G',	makeBlock("111 100 101 101 111"));
	    itsChars.put('H',	makeBlock("101 101 111 101 101"));
	    itsChars.put('I',	makeBlock("010 010 010 010 010"));
	    itsChars.put('J',	makeBlock("111 010 010 010 110"));
	    itsChars.put('K',	makeBlock("101 110 100 110 101"));
	    itsChars.put('L',	makeBlock("100 100 100 100 111"));
		/*	M. */
		/*	N. */
	    itsChars.put('O',	makeBlock("111 101 101 101 111"));
	    itsChars.put('P',	makeBlock("111 101 111 100 100"));
		/*	Q. */
	    itsChars.put('R',	makeBlock("111 101 111 110 101"));
	    itsChars.put('S',	makeBlock("111 100 111 001 111"));
	    itsChars.put('T',	makeBlock("111 010 010 010 010"));
	    itsChars.put('U',	makeBlock("101 101 101 101 111"));
	    itsChars.put('V',	makeBlock("101 101 101 101 010"));
		/*	W. */
	    itsChars.put('X',	makeBlock("101 101 010 101 101"));
	    itsChars.put('Y',	makeBlock("101 101 010 010 010"));
	    itsChars.put('Z',	makeBlock("111 001 010 100 111"));
	    itsChars.put('0',	makeBlock("111 101 101 101 111"));
	    itsChars.put('1',	makeBlock("110 010 010 010 010"));
	    itsChars.put('2',	makeBlock("111 001 111 100 111"));
	    itsChars.put('3',	makeBlock("111 001 011 001 111"));
	    itsChars.put('4',	makeBlock("101 101 111 001 001"));
	    itsChars.put('5',	makeBlock("111 100 111 001 111"));
	    itsChars.put('6',	makeBlock("111 100 111 101 111"));
	    itsChars.put('7',	makeBlock("111 001 001 001 001"));
	    itsChars.put('8',	makeBlock("111 101 111 101 111"));
	    itsChars.put('9',	makeBlock("111 101 111 001 001"));
	    itsChars.put('-',	makeBlock("000 000 111 000 000"));
	    itsChars.put('.',	makeBlock("000 000 000 000 100"));
	};
	
	private static HashMap<String, INotifiablePane> itsCachedGlyphs =
		new HashMap<String, INotifiablePane>();
	
	public synchronized static INotifiablePane getGlyphPane(String ident) {
		INotifiablePane p00 = itsCachedGlyphs.get(ident);
		
		if (p00 == null) {
			try {
				p00 = makeGlyphPane(ident);
				itsCachedGlyphs.put(ident, p00);
			} catch (OperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return p00;
	}

	private static INotifiablePane makeGlyphPane(String ident) throws OperationException {
		ident = (ident + "...").substring(0, 3);
		//	Some cleverness here to stop us adding the same letter twice to one frame
		//	if we scroll between similar strings.
		final Frame f = new Frame().add(find(ident.charAt(0)), 0, 0)
							       .add(new Frame().add(find(ident.charAt(1)), 4, 0), 0, 0)
								   /*.add(new Frame().add(find(ident.charAt(2)), 8, 0), 0, 0)*/;
		
		//	TODO: this is a common pattern: factor it out.
		return new INotifiablePane() {
			public IPressRouter getContent() { return f; }
			public void setFocussed(boolean how) { }
			public void setVisible(boolean how) { }
			public String getGlyph() { return ""; }
		};
	}

	private static IPressRouter find(char ch) {
		IPressRouter p00 = itsChars.get(ch);
		if (p00 == null) { return voidBlock; } else { return p00; }
	}
}
