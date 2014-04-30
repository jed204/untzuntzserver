package com.untzuntz.ustackserverapi.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.untzuntz.coredata.BaseData;
import com.untzuntz.ustack.data.UntzDBObject;

public class FormatUtil {

	/**
	 * Converts BaseData objects to a BasicDBList for response to client
	 * @param objects
	 * @return
	 */
	public static DBObject asDBList(String name, List<? extends BaseData> objects)
	{
		DBObject ret = new BasicDBObject();
		if (objects == null)
			return ret;
		
		BasicDBList list = new BasicDBList();
		for (BaseData obj : objects)
			list.add( obj.toDBObject()  );

		ret.put(name, list);
		
		return ret;
	}
	
	/**
	 * Converts UntzDBObject objects to a BasicDBList for response to client
	 * @param objects
	 * @return
	 */
	 public static DBObject asUDBList(String name, List<? extends UntzDBObject> objects)
	 {
	     DBObject ret = new BasicDBObject();
	     BasicDBList list = new BasicDBList();
	     for (UntzDBObject obj : objects)
	       list.add( obj  );
	 
	    ret.put(name, list);
	 
	   return ret;
	 }


	/**
	 * Formats a Date based on the provided format. If the date is null, 'Unknown' will be returned
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String asDate(Date date, String format) 
	{
		return asDate(date, format, "Unknown");
	}

	/**
	 * Formats a Date based on the provided format. If the date is null, the default value will be returned
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	
	public static String asDate(Date date, String format, String dft){
		if (date == null)
			return dft;
		
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}
		 
	/**
	 * Provided two integers returns a percentage integer between 0 and 100
	 * @param low
	 * @param high
	 * @return
	 */
	public static int percentage(int low, int high) {
		
		if (high == 0)
			return 0;
		
		float percFloat = (float)((float)low / (float)high);
		
		return (int)(100.0f * percFloat);
	}

}
