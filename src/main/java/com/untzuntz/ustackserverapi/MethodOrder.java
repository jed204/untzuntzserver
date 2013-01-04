package com.untzuntz.ustackserverapi;

import java.util.Comparator;

public class MethodOrder implements Comparator<MethodDefinition> {

	public int compare(MethodDefinition arg0, MethodDefinition arg1) {
		
		if (arg0.getDocumentationGroup() != null && arg1.getDocumentationGroup() != null && arg0.getDocumentationGroup().compareTo( arg1.getDocumentationGroup() ) != 0)
			return arg0.getDocumentationGroup().compareTo( arg1.getDocumentationGroup() );
		
		if (arg0.getOrder() == arg1.getOrder())
			return 0;
		
		if (arg0.getOrder() < arg1.getOrder())
			return -1;
		
		return 1;
	}

}
