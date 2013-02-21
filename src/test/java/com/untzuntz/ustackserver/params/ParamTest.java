package com.untzuntz.ustackserver.params;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.util.Calendar;

import org.junit.Test;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.params.APICallParam;
import com.untzuntz.ustackserverapi.params.AndParam;
import com.untzuntz.ustackserverapi.params.OrParam;
import com.untzuntz.ustackserverapi.params.ParamNames;
import com.untzuntz.ustackserverapi.params.Validated;
import com.untzuntz.ustackserverapi.params.types.BooleanParam;
import com.untzuntz.ustackserverapi.params.types.DateRangeParam;
import com.untzuntz.ustackserverapi.params.types.IntParam;
import com.untzuntz.ustackserverapi.params.types.JSONParam;
import com.untzuntz.ustackserverapi.params.types.LongParam;
import com.untzuntz.ustackserverapi.params.types.ParameterDefinitionInt;
import com.untzuntz.ustackserverapi.params.types.StringParam;
import com.untzuntz.ustackserverapi.params.types.URLParam;
import com.untzuntz.ustackserverapi.params.types.util.DateRange;
import com.untzuntz.ustackserverapi.version.Versions;

public class ParamTest {
	
	private APICallParam param1 = new APICallParam(ParamNames.TestA, Versions.Version100);
	private APICallParam param2 = new APICallParam(ParamNames.TestB, Versions.Version100);
	private APICallParam param3 = new APICallParam(ParamNames.TestC, Versions.Version100);

	@Test public void testBasicParamTypes() throws ParseException
	{
		// String
		assertException(new StringParam("test", "test", 5, 100), "1234"); // should fail - 4 < 5
		assertException(new StringParam("test", "test", 5), "1234"); // should fail - 4 < 5
		assertNoException(new StringParam("test", "test", 5), "12345"); // should pass - 5 == 5
		assertNoException(new StringParam("test", "test", null, 5), "12345"); // should pass - 5 == 5
		assertException(new StringParam("test", "test", null, 5), "123456"); // should pass - 6 > 5
		
		// Integer
		assertException(new IntParam("test", "test", 5, 100), "4"); // should fail - 4 < 5
		assertNoException(new IntParam("test", "test", 100), "4"); // should pass - no min specified
		assertNoException(new IntParam("test", "test", 5, 100), "50"); // should pass - 5 < 50 < 100
		assertNoException(new IntParam("test", "test", 5, 100), "100"); // should pass - 5 < 100 < 100
		assertException(new IntParam("test", "test", 100), "101"); // should fail - 101 > 100

		// Long
		assertException(new LongParam("test", "test", 5L, 100L), "4"); // should fail - 4 < 5
		assertNoException(new LongParam("test", "test", 100L), "4"); // should pass - no min specified
		assertNoException(new LongParam("test", "test", 5L, 100L), "50"); // should pass - 5 < 50 < 100
		assertNoException(new LongParam("test", "test", 5L, 100L), "100"); // should pass - 5 < 100 < 100
		assertException(new LongParam("test", "test", 100L), "101"); // should fail - 101 > 100
		
		// Boolean
		assertException(new BooleanParam("test", "test"), "t"); // should fail - not 'true' or 'false'
		assertException(new BooleanParam("test", "test"), "f"); // should fail - not 'true' or 'false'
		assertException(new BooleanParam("test", "test"), "A"); // should fail - not 'true' or 'false'
		assertException(new BooleanParam("test", "test"), "B"); // should fail - not 'true' or 'false'
		assertException(new BooleanParam("test", "test"), "1"); // should fail - not 'true' or 'false'
		assertException(new BooleanParam("test", "test"), "0"); // should fail - not 'true' or 'false'
		assertException(new BooleanParam("test", "test"), "RANDODATA"); // should fail - not 'true' or 'false'
		assertNoException(new BooleanParam("test", "test"), "true"); // should pass - 'true' or 'false'
		assertNoException(new BooleanParam("test", "test"), "false"); // should pass - 'true' or 'false'
		
		// URL
		assertException(new URLParam("test", "test", null), "RANDODATA"); // should fail - not a valid URL
		assertException(new URLParam("test", "test", "http"), "RANDODATA"); // should fail - not a valid URL
		assertException(new URLParam("test", "test", "http"), "ftp://test.url.com"); // should fail - valid url, but not proper type
		assertNoException(new URLParam("test", "test", null), "ftp://test.url.com"); // should pass - valid url
		assertNoException(new URLParam("test", "test", "http"), "http://test.url.com"); // should pass - valid url and type
		assertNoException(new URLParam("test", "test", "http"), "https://test.url.com"); // should pass - valid url and type
		assertException(new URLParam("test", "test", "https"), "http://test.url.com"); // should pass - valid url and type
		assertNoException(new URLParam("test", "test", "https"), "https://test.url.com"); // should pass - valid url and type
		
		// DateRange
		assertEquals("20121201000000", DateRange.fixDate("20121201", true));
		assertEquals("20121201050000", DateRange.fixDate("2012120105", true));
		try { DateRange.fixDate("201", true); fail(); } catch (ParseException pe) { }
		try { DateRange.fixDate("20120", true); fail(); } catch (ParseException pe) { }
		assertEquals("20121201235959", DateRange.fixDate("20121201", false));

		assertEquals("20121201235959", DateRange.df.format(new DateRange("20121201").getEnd()));
		assertEquals("20121201000000", DateRange.df.format(new DateRange("20121201=>20121205").getStart()));
		assertEquals("20121205235959", DateRange.df.format(new DateRange("20121201=>20121205").getEnd()));
		assertNull(new DateRange(">20121201").getEnd());
		assertEquals("20121201235959", DateRange.df.format(new DateRange(">20121201").getStart()));
		assertNull(new DateRange("<20121201").getStart());
		assertEquals("20121201000000", DateRange.df.format(new DateRange("<20121201").getEnd()));
		
		Calendar now = Calendar.getInstance();
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		now.add(Calendar.DATE, -10);
		assertEquals(DateRange.df.format(now.getTime()), DateRange.df.format(new DateRange("Last 10 days").getStart()));
		now.add(Calendar.DATE, 10);
		now.add(Calendar.DATE, 3 * -7);
		assertEquals(DateRange.df.format(now.getTime()), DateRange.df.format(new DateRange("Last 3 weeks").getStart()));
		now.add(Calendar.DATE, 3 * 7);
		now.add(Calendar.MONTH, -8);
		assertEquals(DateRange.df.format(now.getTime()), DateRange.df.format(new DateRange("Last 8 months").getStart()));
		now.add(Calendar.MONTH, 8);
		now.add(Calendar.YEAR, -2);
		assertEquals(DateRange.df.format(now.getTime()), DateRange.df.format(new DateRange("Last 2 years").getStart()));
		
		assertException(new DateRangeParam("test", "test"), "201");
		assertException(new DateRangeParam("test", "test"), "20121");
		assertException(new DateRangeParam("test", "test"), "201213");
		assertNoException(new DateRangeParam("test", "test"), "201212");
		assertNoException(new DateRangeParam("test", "test"), "20121205");
		assertNoException(new DateRangeParam("test", "test"), "20121205141512");
		assertException(new DateRangeParam("test", "test"), "201212051415124");
		assertException(new DateRangeParam("test", "test"), "20121205-20121210");
		assertNoException(new DateRangeParam("test", "test"), "20121205=>20121210");
		assertException(new DateRangeParam("test", "test"), ">20121205=>20121210");
		assertNoException(new DateRangeParam("test", "test"), ">20121205");
		assertNoException(new DateRangeParam("test", "test"), "<20121205");
		
		DateRange range = null;
		
		range = new DateRange("201301");
		assertEquals("20130101000000", DateRange.df.format(range.getStart()));
		assertEquals("20130131235959", DateRange.df.format(range.getEnd()));
		
		range = new DateRange("201302");
		assertEquals("20130201000000", DateRange.df.format(range.getStart()));
		assertEquals("20130228235959", DateRange.df.format(range.getEnd()));
		
		range = new DateRange("201304");
		assertEquals("20130401000000", DateRange.df.format(range.getStart()));
		assertEquals("20130430235959", DateRange.df.format(range.getEnd()));
		
		// JSON
		assertException(new JSONParam("test", "test"), "RANDODATA"); // should fail - not a valid JSON string
		assertNoException(new JSONParam("test", "test"), "{ hello : \"Test\" }"); // should fail - not a valid JSON string

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

	
	private void assertException(ParameterDefinitionInt pti, String data)
	{
		try {
			pti.validate(data);
			fail();
		} catch (APIException e) {
			
		}
	}

	private void assertNoException(ParameterDefinitionInt pti, String data)
	{
		try {
			pti.validate(data);
		} catch (APIException e) {
			System.out.println("Exception: " + e);
			fail();
		}
	}

}
