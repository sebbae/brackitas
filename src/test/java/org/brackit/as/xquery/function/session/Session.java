package org.brackit.as.xquery.function.session;

import org.brackit.xquery.QueryContext;
import org.brackit.xquery.node.SimpleStore;
import org.brackit.xquery.xdm.Store;
import org.junit.Test;

public class Session {
	
	private static QueryContext ctx;

	private static Store store;
	
	static {
		store = new SimpleStore();
		ctx = new QueryContext(store);
	}
	
	@Test
	public void testClear() throws Exception {
		
//		XQuery x = new ASXQuery();
//		x.setPrettyPrint(true);
//		x.serialize(ctx, System.out);
//		assertEquals(expected, actual);
//		Functions.predefine(new Eval(new QNm(Namespaces.BIT_NSURI,
//				Namespaces.BIT_PREFIX, "eval"), new Signature(new SequenceType(
//				AtomicType.STR, Cardinality.ZeroOrOne), // result
//				new SequenceType(AtomicType.STR, Cardinality.One))));		
//		
	}	
	
}
