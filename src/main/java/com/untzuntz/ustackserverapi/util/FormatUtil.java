package com.untzuntz.ustackserverapi.util;

import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.untzuntz.ustack.data.UntzDBObject;

public class FormatUtil {

	/**
	 * Converts UntzDBObject objects to a BasicDBList for response to client
	 * @param objects
	 * @return
	 */
	public static DBObject asDBList(String name, List<? extends UntzDBObject> objects)
	{
		DBObject ret = new BasicDBObject();
		BasicDBList list = new BasicDBList();
		for (UntzDBObject obj : objects)
			list.add( obj  );

		ret.put(name, list);
		
		return ret;
	}
}
