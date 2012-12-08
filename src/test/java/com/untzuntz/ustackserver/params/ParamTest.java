package com.untzuntz.ustackserver.params;

import org.junit.Test;
import static org.junit.Assert.fail;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.ParamTypeInt;
import com.untzuntz.ustackserverapi.params.APICallParam;
import com.untzuntz.ustackserverapi.params.AndParam;
import com.untzuntz.ustackserverapi.params.OrParam;
import com.untzuntz.ustackserverapi.params.ParamNames;
import com.untzuntz.ustackserverapi.params.Validated;
import com.untzuntz.ustackserverapi.params.types.BooleanParam;
import com.untzuntz.ustackserverapi.params.types.IntParam;
import com.untzuntz.ustackserverapi.params.types.LongParam;
import com.untzuntz.ustackserverapi.params.types.StringParam;
import com.untzuntz.ustackserverapi.params.types.URLParam;
import com.untzuntz.ustackserverapi.version.Versions;

public class ParamTest {
	
	private APICallParam param1 = new APICallParam(ParamNames.TestA, Versions.Version100);
	private APICallParam param2 = new APICallParam(ParamNames.TestB, Versions.Version100);
	private APICallParam param3 = new APICallParam(ParamNames.TestC, Versions.Version100);

	@Test public void testBasicParamTypes()
	{
		// String
		assertException(new StringParam(5, 100), "1234"); // should fail - 4 < 5
		assertException(new StringParam(5), "1234"); // should fail - 4 < 5
		assertNoException(new StringParam(5), "12345"); // should pass - 5 == 5
		assertNoException(new StringParam(null, 5), "12345"); // should pass - 5 == 5
		assertException(new StringParam(null, 5), "123456"); // should pass - 6 > 5
		
		// Integer
		assertException(new IntParam(5, 100), "4"); // should fail - 4 < 5
		assertNoException(new IntParam(100), "4"); // should pass - no min specified
		assertNoException(new IntParam(5, 100), "50"); // should pass - 5 < 50 < 100
		assertNoException(new IntParam(5, 100), "100"); // should pass - 5 < 100 < 100
		assertException(new IntParam(100), "101"); // should fail - 101 > 100

		// Long
		assertException(new LongParam(5L, 100L), "4"); // should fail - 4 < 5
		assertNoException(new LongParam(100L), "4"); // should pass - no min specified
		assertNoException(new LongParam(5L, 100L), "50"); // should pass - 5 < 50 < 100
		assertNoException(new LongParam(5L, 100L), "100"); // should pass - 5 < 100 < 100
		assertException(new LongParam(100L), "101"); // should fail - 101 > 100
		
		// Boolean
		assertException(new BooleanParam(), "t"); // should fail - not 'true' or 'false'
		assertException(new BooleanParam(), "f"); // should fail - not 'true' or 'false'
		assertException(new BooleanParam(), "A"); // should fail - not 'true' or 'false'
		assertException(new BooleanParam(), "B"); // should fail - not 'true' or 'false'
		assertException(new BooleanParam(), "1"); // should fail - not 'true' or 'false'
		assertException(new BooleanParam(), "0"); // should fail - not 'true' or 'false'
		assertException(new BooleanParam(), "RANDODATA"); // should fail - not 'true' or 'false'
		assertNoException(new BooleanParam(), "true"); // should pass - 'true' or 'false'
		assertNoException(new BooleanParam(), "false"); // should pass - 'true' or 'false'
		
		// URL
		assertException(new URLParam(null), "RANDODATA"); // should fail - not a valid URL
		assertException(new URLParam("http"), "RANDODATA"); // should fail - not a valid URL
		assertException(new URLParam("http"), "ftp://test.url.com"); // should fail - valid url, but not proper type
		assertNoException(new URLParam(null), "ftp://test.url.com"); // should pass - valid url
		assertNoException(new URLParam("http"), "http://test.url.com"); // should pass - valid url and type
		assertNoException(new URLParam("http"), "https://test.url.com"); // should pass - valid url and type
		assertException(new URLParam("https"), "http://test.url.com"); // should pass - valid url and type
		assertNoException(new URLParam("https"), "https://test.url.com"); // should pass - valid url and type
	}
	
	@Test public void testORParams()
	{
		OrParam orA = new OrParam("TestA").add(param1).add(param2);
		CallParameters params = new CallParameters("/");
		
		assertException(orA, params); // should fail - no params
		
		params.setParameterValue(param1.getParamDetails().getName(), "50");
		assertNoException(orA, params); // should pass - 'param1' set
		
		params.setParameterValue(param2.getParamDetails().getName(), "45");
		assertNoException(orA, params); // should pass - 'param1' and 'param2' set
	}
	
	@Test public void testANDParams()
	{
		AndParam andA = new AndParam("TestB").add(param1).add(param2);
		CallParameters params = new CallParameters("/");
		
		assertException(andA, params); // should fail - no params
		
		params.setParameterValue(param1.getParamDetails().getName(), "50");
		assertException(andA, params); // should fail - only 'param1' set
		
		params.setParameterValue(param2.getParamDetails().getName(), "45");
		assertNoException(andA, params); // should pass - 'param1' and 'param2' set
	}
	
	/*
	 * Tests param3 AND (param1 OR param2)
	 */
	@Test public void testMultiGroup()
	{
		CallParameters params = new CallParameters("/");
		
		AndParam andA = new AndParam("TestAND").add(param3).add(new OrParam("TestOR").add(param1).add(param2));
		
		assertException(andA, params); // should fail - no params

		params.setParameterValue(param3.getParamDetails().getName(), "50");
		assertException(andA, params); // should fail - 'param3' but no 'param1' OR 'param2'

		params.setParameterValue(param1.getParamDetails().getName(), "55");
		params.setParameterValue(param3.getParamDetails().getName(), "50");
		assertNoException(andA, params); // should pass - 'param1' and 'param3' set
		
		params.setParameterValue(param2.getParamDetails().getName(), "50");
		assertNoException(andA, params); // should pass - 'param1', 'param2', 'param3' set
	}
	
	private void assertException(Validated val, CallParameters params) 
	{
		try {
			val.validate(params);
			fail();
		} catch (APIException e) {
			
		}
	}
	
	private void assertNoException(Validated val, CallParameters params) 
	{
		try {
			val.validate(params);
		} catch (APIException e) {
			System.out.println("Exception: " + e);
			fail();
		}
	}

	
	private void assertException(ParamTypeInt pti, String data)
	{
		try {
			pti.validate(data);
			fail();
		} catch (APIException e) {
			
		}
	}

	private void assertNoException(ParamTypeInt pti, String data)
	{
		try {
			pti.validate(data);
		} catch (APIException e) {
			System.out.println("Exception: " + e);
			fail();
		}
	}

}
