package com.untzuntz.ustack.data;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.untzuntz.ustack.aaa.LinkActionInterface;
import com.untzuntz.ustack.aaa.ResourceLink;

public class UserLinkAction implements LinkActionInterface {

	public DBObject getResourceLinkExtras(ResourceLink link) {
		DBObject ret = new BasicDBObject();
		ret.put("userName", link.get("userName"));
		ret.put("linkText", link.get("userName"));
		return ret;
	}

	public void linkCreated(UntzDBObject user, ResourceLink link) {
	}

	public void linkRemoved(UntzDBObject user, ResourceLink link) {
	}

	@Override
	public void setType(String type) {
	}

	@Override
	public void setRole(String role) {
	}

	@Override
	public void setUser(UserAccount user) {
	}

	@Override
	public UserAccount getUser() {
		return null;
	}
	
	
}
