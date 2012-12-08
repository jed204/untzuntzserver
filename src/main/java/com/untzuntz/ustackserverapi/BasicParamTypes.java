package com.untzuntz.ustackserverapi;

import com.untzuntz.ustackserverapi.params.types.BooleanParam;
import com.untzuntz.ustackserverapi.params.types.IntParam;
import com.untzuntz.ustackserverapi.params.types.LongParam;
import com.untzuntz.ustackserverapi.params.types.StringParam;
import com.untzuntz.ustackserverapi.params.types.URLParam;

public final class BasicParamTypes {

	public static final StringParam String = new StringParam(0, 255);
	public static final IntParam Integer = new IntParam(0, 100);
	public static final LongParam Long = new LongParam(0L, java.lang.Long.MAX_VALUE);
	public static final BooleanParam Boolean = new BooleanParam();
	public static final URLParam HTTPURL = new URLParam("http");
	public static final URLParam HTTPSURL = new URLParam("https:");
	
}
