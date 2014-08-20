package com.untzuntz.ustackserverapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class APICalls {

	static final List<APIException> errors = new ArrayList<APIException>();
	
	public static List<APIException> getErrors() {
		return errors;
	}
	
	public static void addException(APIException e) {
		errors.add(e);
	}
	
    static final Map<String, MethodDefinition> routing = new HashMap<String, MethodDefinition>();

    public static List<MethodDefinition> getMethods() {
    	List<MethodDefinition> list = new ArrayList<MethodDefinition>();
    	Iterator<String> it = routing.keySet().iterator();
    	while (it.hasNext()) {
    		String route = it.next();
    		list.add(routing.get(route));
    	}
    	return list;
    }
    
    public static void clearRoutes() {
    	routing.clear();
    }
    
    public static void addRoute(MethodDefinition def) {
    	routing.put(def.getPath(), def);
    }
    
    public static MethodDefinition getCallByURI(String path) {
    	return routing.get(path);
    }

}
