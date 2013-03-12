package com.untzuntz.ustackserver.api;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.untzuntz.ustack.data.APIClient;
import com.untzuntz.ustackserverapi.APICalls;
import com.untzuntz.ustackserverapi.APIResponse;
import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.MethodDefinition;
import com.untzuntz.ustackserverapi.auth.AuthTypes;
import com.untzuntz.ustackserverapi.exceptions.UnknownObjectException;
import com.untzuntz.ustackserverapi.params.APICallParam;
import com.untzuntz.ustackserverapi.params.ParamNames;
import com.untzuntz.ustackserverapi.version.Versions;

public class PermissionsAPI {
	
	public void setup(String prefix)
	{
		MethodDefinition def = new MethodDefinition(prefix + "/resource/list", PermissionsAPI.class, "listResourceDefinition");
		def.enableGET(true);
		def.setSinceVersion(Versions.Version100);
		def.setDocumentationGroup("Roles & Permissions");
		def.authenticationMethod(AuthTypes.ClientKey);
		def.setOrder(100);
		def.setDescription("Returns a list of all resource definitions");
		APICalls.addRoute(def);
		
		def = new MethodDefinition(prefix + "/api/client/list", PermissionsAPI.class, "getApiClientList");
		def.enableGET(true);
		def.setSinceVersion(Versions.Version100);
		def.setDocumentationGroup("Roles & Permissions");
		def.authenticationMethod(AuthTypes.ClientKey);
		def.setOrder(100);
		def.setDescription("Returns a list of all api clients");
		APICalls.addRoute(def);

		def = new MethodDefinition(prefix + "/api/client", PermissionsAPI.class, "getApiClient");
		def.enableGET(true);
		def.setSinceVersion(Versions.Version100);
		def.setDocumentationGroup("Roles & Permissions");
		def.authenticationMethod(AuthTypes.ClientKey);
		def.addParameter(new APICallParam(ParamNames.client_id), true);
		def.setOrder(100);
		def.setDescription("Returns the information about a single API client");
		APICalls.addRoute(def);

	}
	
	public void listResourceDefinition(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams)
	{
		
	}
	
	public void saveResourceDefinition(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams) throws Exception
	{
		
	}
	
	public void deleteResourceDefinition(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams)
	{
		
	}
	
	public void saveRole(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams)
	{
		
	}
	
	public void deleteRole(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams)
	{
		
	}
	
	public void savePermission(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams)
	{
		
	}

	public void deletePermission(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams)
	{
		
	}

	public void savePlugin(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams)
	{
		
	}
	
	public void deletePlugin(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams)
	{
		
	}
	
	public void getApiClientList(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams)
	{
		
	}
	
	public void getApiClient(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams) throws Exception
	{
		APIClient client = APIClient.getAPIClient(callParams.get(ParamNames.client_id));
		if (client == null)
			throw new UnknownObjectException(String.format("Could not find API client '%s'", callParams.get(ParamNames.client_id)));

		DBObject ret = new BasicDBObject();
		
		ret.put("clientId", client.getClientId());
		
		BasicDBList outKeyList = new BasicDBList();
		BasicDBList keyList = client.getAPIKeys();
		for (int i = 0; i < keyList.size(); i++)
		{
			DBObject k = (DBObject)keyList.get(i);
			DBObject outKey = new BasicDBObject();
			outKey.put("key", client.getKey((String)k.get("uid")));
			if ("t".equalsIgnoreCase( (String)k.get("revoked") ))
				outKey.put("revoked", "true");

			outKeyList.add(outKey);
		}
		ret.put("keys", outKeyList);
		
		BasicDBList outResList = new BasicDBList();
		BasicDBList resList = client.getResourceLinkList();
		for (int i = 0; i < resList.size(); i++)
		{
			DBObject r = (DBObject)resList.get(i);
			DBObject outRes = new BasicDBObject();
			outRes.put("resource", (String)r.get("name"));
			outRes.put("role", (String)r.get("role"));
			outResList.add(outRes);
		}
		ret.put("access", outResList);
		
		APIResponse.httpOk(channel, APIResponse.success(ret), callParams);
	}
	
}
