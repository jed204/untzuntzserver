package com.untzuntz.ustackserverapi.util;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.List;

public class DataUtil {

	/**
	 * Checks if the list is null, return an empty list
	 * @param other
	 * @return
	 */
	public static <T> List<T> safeList(List<T> other)
	{
		return other == null ? Collections.<T>emptyList() : other;
	}
	
	/**
	 * Checks if the array is null, return an empty array
	 * @param other
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] safeArray(T[] other, Class<T> clazz)
	{
		if (other == null)
			return (T[])Array.newInstance(clazz, 0);
		
		return other;
	}

	/**
	 * Closes a stream, handling all exceptions
	 * @param in
	 */
	public static void close(java.io.Closeable in) 
	{
		if (in == null)
			return;
		
		try {
			in.close();
		} catch (Exception e) { 
			// do nothing
		}
	}

}
