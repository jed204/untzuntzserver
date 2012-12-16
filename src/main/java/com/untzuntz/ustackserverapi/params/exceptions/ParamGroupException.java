package com.untzuntz.ustackserverapi.params.exceptions;

import java.util.ArrayList;
import java.util.List;

import com.untzuntz.ustackserverapi.APIException;

public class ParamGroupException extends APIException {

	private static final long serialVersionUID = 1L;

	private String groupName;
	private List<APIException> childExceptions;
	
	public ParamGroupException(String groupName) {
		this.groupName = groupName;
		this.childExceptions = new ArrayList<APIException>();
	}
	
	public void addException(APIException grp) {
		childExceptions.add(grp);
	}
	
	public String getGroupName() {
		return groupName;
	}
	
	public List<APIException> getChildExceptions() {
		return childExceptions;
	}
	
}
