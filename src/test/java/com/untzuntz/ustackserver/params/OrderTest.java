package com.untzuntz.ustackserver.params;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.untzuntz.ustackserverapi.APICalls;
import com.untzuntz.ustackserverapi.MethodDefinition;
import com.untzuntz.ustackserverapi.MethodOrder;

public class OrderTest {

	@Test public void testMethodOrder()
	{
		APICalls.clearRoutes();
		
		MethodDefinition md1 = new MethodDefinition("testGroupA1", OrderTest.class, "test");
		MethodDefinition md2 = new MethodDefinition("testGroupA2", OrderTest.class, "test");
		MethodDefinition md3 = new MethodDefinition("testGroupA3", OrderTest.class, "test");
		MethodDefinition md4 = new MethodDefinition("testGroupB1", OrderTest.class, "test");
		MethodDefinition md5 = new MethodDefinition("testGroupC1", OrderTest.class, "test");
		MethodDefinition md6 = new MethodDefinition("testGroupC2", OrderTest.class, "test");
		MethodDefinition md7 = new MethodDefinition("testGroupD1", OrderTest.class, "test");
		md1.setDocumentationGroup("A");
		md2.setDocumentationGroup("A");
		md3.setDocumentationGroup("A");
		md4.setDocumentationGroup("B");
		md5.setDocumentationGroup("C");
		md6.setDocumentationGroup("C");
		md7.setDocumentationGroup("D");
		md1.setOrder(10);
		md2.setOrder(11);
		md3.setOrder(12);
		md4.setOrder(1);
		md5.setOrder(2);
		md6.setOrder(3);
		md7.setOrder(4);
		
		// purposely add to the list out of order
		APICalls.addRoute(md6);
		APICalls.addRoute(md4);
		APICalls.addRoute(md2);
		APICalls.addRoute(md1);
		APICalls.addRoute(md7);
		APICalls.addRoute(md3);
		APICalls.addRoute(md5);

        List<MethodDefinition> methods = APICalls.getMethods();
        
        for (MethodDefinition def : methods)
        {
        	System.err.println(String.format("%s -> %s (%d)", def.getPath(), def.getDocumentationGroup(), def.getOrder()));
        }

		// order the list
        Collections.sort(methods, new MethodOrder());

        // confirm expected order
        assertEquals(methods.get(0).getPath(), "testGroupB1");
        assertEquals(methods.get(1).getPath(), "testGroupC1");
        assertEquals(methods.get(2).getPath(), "testGroupC2");
        assertEquals(methods.get(3).getPath(), "testGroupD1");
        assertEquals("testGroupA1", methods.get(4).getPath());
        assertEquals("testGroupA2", methods.get(5).getPath());
        assertEquals("testGroupA3", methods.get(6).getPath());
        
        for (MethodDefinition def : methods)
        {
        	System.err.println(String.format("%s -> %s (%d)", def.getPath(), def.getDocumentationGroup(), def.getOrder()));
        }
	}
	
}
