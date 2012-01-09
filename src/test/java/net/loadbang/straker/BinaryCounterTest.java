//	$Id$
//	$Source$

package net.loadbang.straker;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BinaryCounterTest {
	@Test
	public void counterPixelsOK() throws Exception {
		BinaryCounter counter = new BinaryCounter();
		
		for (int i = 0; i < 256; i++) {
			testCounter(counter, i);
		}
	}

	private void testCounter(BinaryCounter counter, int n) throws Exception {
		counter.setValue(n);

		for (int i = 0; i < 8; i++) {
			assertEquals("value " + n + " counter bit " + i,
						 ((n >> i) & 1) != 0,
						 counter.getRenderedLamp(0, i)
						);
		}
	}
}
