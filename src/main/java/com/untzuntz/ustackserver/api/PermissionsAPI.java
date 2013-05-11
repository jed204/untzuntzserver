package com.untzuntz.ustackserver.api;

import java.util.List;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.untzuntz.ustack.aaa.ResourceDefinition;
import com.untzuntz.ustack.aaa.ResourceLink;
import com.untzuntz.ustack.aaa.RoleDefinition;
import com.untzuntz.ustack.data.APIClient;
import com.untzuntz.ustackserverapi.APICalls;
import com.untzuntz.ustackserverapi.APIResponse;
import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.MethodDefinition;
import com.untzuntz.ustackserverapi.auth.AuthTypes;
import com.untzuntz.ustackserverapi.exceptions.UnknownObjectException;
import com.untzuntz.ustackserverapi.params.APICallParam;
import com.untzuntz.ustackserverapi.params.ParamNames;
import com.untzuntz.ustackserverapi.util.APIPerms;
import com.untzuntz.ustackserverapi.util.FormatUtil;
import com.untzuntz.ustackserverapi.version.Versions;

public class PermissionsAPI {
	
	public void setup(String prefix)
	{
		/*
		 * Resources
		 */
		MethodDefinition def = new MethodDefinition(prefix + "/resource/list", PermissionsAPI.class, "listResourceDefinition");
		def.enableGET(true);
		def.setSinceVersion(Versions.Version100);
		def.setDocumentationGroup("Roles & Permissions");
		def.authenticationMethod(AuthTypes.ClientKey);
		def.authenticationGroup(APIPerms.APIResourceManager);
		def.setOrder(110);
		def.setDescription("Returns a list of all resource definitions");
		APICalls.addRoute(def);
		
		def = new MethodDefinition(prefix + "/resource", PermissionsAPI.class, "getResourceDefinition");
		def.enablePOST(true);
		def.setSinceVersion(Versions.Version100);
		def.setDocumentationGroup("Roles & Permissions");
		def.authenticationMethod(AuthTypes.ClientKey);
		def.authenticationGroup(APIPerms.APIResourceManager);
		def.setOrder(100);
		def.setDescription("Returns info/roles/permissions/etc for a resource definition");
		def.addParameter(new APICallParam(ParamNames.resource_name), true);
		def.overrideDocumentationResponse("{\"apiResult\": {\"status\": \"SUCCESS\"}}");
		APICalls.addRoute(def);
		
		def = new MethodDefinition(prefix + "/resource/save", PermissionsAPI.class, "saveResourceDefinition");
		def.enablePOST(true);
		def.setSinceVersion(Versions.Version100);
		def.setDocumentationGroup("Roles & Permissions");
		def.authenticationMethod(AuthTypes.ClientKey);
		def.authenticationGroup(APIPerms.APIResourceManager);
		def.setOrder(100);
		def.setDescription("Saves/creates a resource definition");
		def.addParameter(new APICallParam(ParamNames.resource_name), true);
		def.addParameter(new APICallParam(ParamNames.resource_type), true);
		def.addParameter(new APICallParam(ParamNames.link_action_class));
		def.addParameter(new APICallParam(ParamNames.managed_by));
		def.addParameter(new APICallParam(ParamNames.can_manage));
		def.addParameter(new APICallParam(ParamNames.support_email));
		def.addParameter(new APICallParam(ParamNames.internal_name));
		def.overrideDocumentationResponse("{\"apiResult\": {\"status\": \"SUCCESS\"}}");
		APICalls.addRoute(def);
		
		def = new MethodDefinition(prefix + "/resource/delete", PermissionsAPI.class, "deleteResourceDefinition");
		def.enablePOST(true);
		def.setSinceVersion(Versions.Version100);
		def.setDocumentationGroup("Roles & Permissions");
		def.authenticationMethod(AuthTypes.ClientKey);
		def.authenticationGroup(APIPerms.APIResourceManager);
		def.setOrder(120);
		def.setDescription("Removes a resource definition");
		def.addParameter(new APICallParam(ParamNames.resource_name), true);
		def.overrideDocumentationResponse("{\"apiResult\": {\"status\": \"SUCCESS\"}}");
		APICalls.addRoute(def);
		
		/*
		 * Roles
		 */
		def = new MethodDefinition(prefix + "/resource/role/save", PermissionsAPI.class, "saveRole");
		def.enablePOST(true);
		def.setSinceVersion(Versions.Version100);
		def.setDocumentationGroup("Roles & Permissions");
		def.authenticationMethod(AuthTypes.ClientKey);
		def.authenticationGroup(APIPerms.APIRoleManager);
		def.setOrder(130);
		def.setDescription("Saves/creates a role on a resource");
		def.addParameter(new APICallParam(ParamNames.resource_name), true);
		def.addParameter(new APICallParam(ParamNames.role_name), true);
		def.overrideDocumentationResponse("{\"apiResult\": {\"status\": \"SUCCESS\"}}");
		APICalls.addRoute(def);
		
		def = new MethodDefinition(prefix + "/resource/role/delete", PermissionsAPI.class, "deleteRole");
		def.enablePOST(true);
		def.setSinceVersion(Versions.Version100);
		def.setDocumentationGroup("Roles & Permissions");
		def.authenticationMethod(AuthTypes.ClientKey);
		def.authenticationGroup(APIPerms.APIRoleManager);
		def.setOrder(140);
		def.setDescription("Removes an existing role from a resource");
		def.addParameter(new APICallParam(ParamNames.resource_name), true);
		def.addParameter(new APICallParam(ParamNames.role_name), true);
		def.overrideDocumentationResponse("{\"apiResult\": {\"status\": \"SUCCESS\"}}");
		APICalls.addRoute(def);
		
		/*
		 * Permissions
		 */
		def = new MethodDefinition(prefix + "/resource/role/permission/add", PermissionsAPI.class, "addPermission");
		def.enablePOST(true);
		def.setSinceVersion(Versions.Version100);
		def.setDocumentationGroup("Roles & Permissions");
		def.authenticationMethod(AuthTypes.ClientKey);
		def.authenticationGroup(APIPerms.APIRoleManager);
		def.setOrder(150);
		def.setDescription("Adds a permission to a role");
		def.addParameter(new APICallParam(ParamNames.resource_name), true);
		def.addParameter(new APICallParam(ParamNames.role_name), true);
		def.addParameter(new APICallParam(ParamNames.permission), true);
		def.overrideDocumentationResponse("{\"apiResult\": {\"status\": \"SUCCESS\"}}");
		APICalls.addRoute(def);
		
		def = new MethodDefinition(prefix + "/resource/role/permission/delete", PermissionsAPI.class, "deletePermission");
		def.enablePOST(true);
		def.setSinceVersion(Versions.Version100);
		def.setDocumentationGroup("Roles & Permissions");
		def.authenticationMethod(AuthTypes.ClientKey);
		def.authenticationGroup(APIPerms.APIRoleManager);
		def.setOrder(160);
		def.setDescription("Removes an existing permission from a role");
		def.addParameter(new APICallParam(ParamNames.resource_name), true);
		def.addParameter(new APICallParam(ParamNames.role_name), true);
		def.addParameter(new APICallParam(ParamNames.permission), true);
		def.overrideDocumentationResponse("{\"apiResult\": {\"status\": \"SUCCESS\"}}");
		APICalls.addRoute(def);
		
		/*
		 * API Client
		 */
		def = new MethodDefinition(prefix + "/api/client/list", PermissionsAPI.class, "getApiClientList");
		def.enableGET(true);
		def.setSinceVersion(Versions.Version100);
		def.setDocumentationGroup("Roles & Permissions");
		def.authenticationMethod(AuthTypes.ClientKey);
		def.authenticationGroup(APIPerms.APIClientManager);
		def.setOrder(170);
		def.setDescription("Returns a list of all api clients");
		def.addParameter(new APICallParam(ParamNames.page, "0"));
		def.addParameter(new APICallParam(ParamNames.items_per_page, "10"));
		APICalls.addRoute(def);

		def = new MethodDefinition(prefix + "/api/client", PermissionsAPI.class, "getApiClient");
		def.enableGET(true);
		def.setSinceVersion(Versions.Version100);
		def.setDocumentationGroup("Roles & Permissions");
		def.authenticationMethod(AuthTypes.ClientKey);
		def.authenticationGroup(APIPerms.APIClientManager);
		def.addParameter(new APICallParam(ParamNames.target_client_id), true);
		def.setOrder(180);
		def.setDescription("Returns the information about a single API client");
		def.overrideDocumentationResponse("{\"apiResult\": {\"status\": \"SUCCESS\"}}");
		APICalls.addRoute(def);

		def = new MethodDefinition(prefix + "/api/client/create", PermissionsAPI.class, "createApiClient");
		def.enablePOST(true);
		def.setSinceVersion(Versions.Version100);
		def.setDocumentationGroup("Roles & Permissions");
		def.authenticationMethod(AuthTypes.ClientKey);
		def.authenticationGroup(APIPerms.APIClientManager);
		def.addParameter(new APICallParam(ParamNames.target_client_id), true);
		def.setOrder(190);
		def.setDescription("Creates a new API client");
		def.overrideDocumentationResponse("{\"apiResult\": {\"status\": \"SUCCESS\"}}");
		APICalls.addRoute(def);

		def = new MethodDefinition(prefix + "/api/client/add_resource", PermissionsAPI.class, "addApiClientResource");
		def.enablePOST(true);
		def.setSinceVersion(Versions.Version100);
		def.setDocumentationGroup("Roles & Permissions");
		def.authenticationMethod(AuthTypes.ClientKey);
		def.authenticationGroup(APIPerms.APIClientManager);
		def.addParameter(new APICallParam(ParamNames.target_client_id), true);
		def.addParameter(new APICallParam(ParamNames.resource_name), true);
		def.addParameter(new APICallParam(ParamNames.role_name), true);
		def.setOrder(200);
		def.setDescription("Adds a Resource to an existing API client");
		def.overrideDocumentationResponse("{\"apiResult\": {\"status\": \"SUCCESS\"}}");
		APICalls.addRoute(def);

	}
	
	/**
	 * Listing of existing resource definitions
	 */
	public void listResourceDefinition(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams)
	{
		List<ResourceDefinition> resources = ResourceDefinition.getAll();
		APIResponse.httpOk(channel, APIResponse.success(FormatUtil.asDBList("resources", resources)), callParams);
	}
	
	/**
	 * Returns info/roles/permissions/etc for a resource definition
	 */
	public void getResourceDefinition(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams) throws Exception
	{
		ResourceDefinition rd = ResourceDefinition.getByName(callParams.get(ParamNames.resource_name));
		if (rd == null)
			throw new UnknownObjectException("Could not find resource");

		APIResponse.httpOk(channel, APIResponse.success(rd), callParams);
	}
	
	/**
	 * Saves/creates a resource definition
	 */
	public void saveResourceDefinition(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams) throws Exception
	{
		ResourceDefinition rd = ResourceDefinition.getByName(callParams.get(ParamNames.resource_name));
		
		if (rd == null)
			rd = ResourceDefinition.createResource(callParams.get(ParamNames.resource_name), callParams.get(ParamNames.resource_type));
		
		if ("<REMOVE>".equals(callParams.get(ParamNames.link_action_class)))
			rd.setLinkActionClass(null);
		else if (callParams.get(ParamNames.link_action_class) != null)
			rd.setLinkActionClass(callParams.get(ParamNames.link_action_class));

		if (callParams.get(ParamNames.managed_by) != null)
		{
			String[] managedByArray = callParams.get(ParamNames.managed_by);
			
			rd.clearManagedBy();
			for (String managedBy : managedByArray)
				rd.addManagedBy(managedBy);
		}
		
		if (callParams.get(ParamNames.can_manage) != null)
		{
			String[] canManageArray = callParams.get(ParamNames.can_manage);
			rd.clearCanManage();
			for (String canManage : canManageArray)
				rd.addCanManage(canManage);
		}
		
		if ("<REMOVE>".equals(callParams.get(ParamNames.support_email)))
			rd.setSupportEmail(null);
		else if (callParams.get(ParamNames.support_email) != null)
			rd.setSupportEmail(callParams.get(ParamNames.support_email));
		
		if ("<REMOVE>".equals(callParams.get(ParamNames.internal_name)))
			rd.setInternalName(null);
		else if (callParams.get(ParamNames.internal_name) != null)
			rd.setInternalName(callParams.get(ParamNames.internal_name));
		
		rd.save(callParams.get(ParamNames.client_id));
		
		APIResponse.httpOk(channel, APIResponse.success(), callParams);
	}
	
	/**
	 * Removes a resource definition
	 */
	public void deleteResourceDefinition(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams) throws Exception
	{
		ResourceDefinition rd = ResourceDefinition.getByName(callParams.get(ParamNames.resource_name));
		if (rd == null)
			throw new UnknownObjectException("Could not find resource");

		rd.delete();
		
		APIResponse.httpOk(channel, APIResponse.success(), callParams);

	}
	
	/**
	 * Save/create a role
	 */
	public void saveRole(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams) throws Exception
	{
		ResourceDefinition rd = ResourceDefinition.getByName(callParams.get(ParamNames.resource_name));
		if (rd == null)
			throw new UnknownObjectException("Could not find resource");
		
		RoleDefinition role = rd.getRoleByName(callParams.get(ParamNames.role_name));
		if (role == null)
		{
			role = new RoleDefinition(callParams.get(ParamNames.role_name));
			rd.addRole(role);
			rd.save(callParams.get(ParamNames.client_id));
		}
	
		APIResponse.httpOk(channel, APIResponse.success(), callParams);
	}
	
	/**
	 * Removes an existing role from a resource
	 */
	public void deleteRole(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams) throws Exception
	{
		ResourceDefinition rd = ResourceDefinition.getByName(callParams.get(ParamNames.resource_name));
		if (rd == null)
			throw new UnknownObjectException("Could not find resource");
		
		RoleDefinition role = rd.getRoleByName(callParams.get(ParamNames.role_name));
		if (role == null)
			throw new UnknownObjectException("Could not find role");

		rd.deleteRole(role);
		rd.save(callParams.get(ParamNames.client_id));
		
		APIResponse.httpOk(channel, APIResponse.success(), callParams);
	}

	/**
	 * Create a permission on a role
	 */
	public void addPermission(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams) throws Exception
	{
		ResourceDefinition rd = ResourceDefinition.getByName(callParams.get(ParamNames.resource_name));
		if (rd == null)
			throw new UnknownObjectException("Could not find resource");
		
		RoleDefinition role = rd.getRoleByName(callParams.get(ParamNames.role_name));
		if (role == null)
			throw new UnknownObjectException("Could not find role");

		role.addPermission(callParams.get(ParamNames.permission));
		rd.setRole(role);
		rd.save(callParams.get(ParamNames.client_id));
		
		APIResponse.httpOk(channel, APIResponse.success(), callParams);
	}

	/**
	 * Removes a permission from a role
	 */
	public void deletePermission(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams) throws Exception
	{
		ResourceDefinition rd = ResourceDefinition.getByName(callParams.get(ParamNames.resource_name));
		if (rd == null)
			throw new UnknownObjectException("Could not find resource");
		
		RoleDefinition role = rd.getRoleByName(callParams.get(ParamNames.role_name));
		if (role == null)
			throw new UnknownObjectException("Could not find role");

		role.deletePermission(callParams.get(ParamNames.permission));
		rd.save(callParams.get(ParamNames.client_id));
		
		APIResponse.httpOk(channel, APIResponse.success(), callParams);
	}

	public void savePlugin(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams)
	{
		
	}
	
	public void deletePlugin(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams)
	{
		
	}
	
	/**
	 * Returns a listing of API clients
	 */
	public void getApiClientList(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams)
	{
		BasicDBList clientList = new BasicDBList();
		
		List<DBObject> clients = APIClient.search(null, null, APIClient.getDBCollection(), null);
		for (DBObject client : clients)
			clientList.add(getAPIClientResponse(new APIClient(client)));
		
		APIResponse.httpOk(channel, APIResponse.success(new BasicDBObject("clients", clientList)), callParams);
	}
	
	/**
	 * Creates a new API client
	 */
	public void createApiClient(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams) throws Exception
	{
		APIClient client = APIClient.createAPI(callParams.get(ParamNames.client_id), callParams.get(ParamNames.target_client_id));
		client.save(callParams.get(ParamNames.client_id));
		
		APIResponse.httpOk(channel, APIResponse.success(getAPIClientResponse(client)), callParams);
	}
	
	/**
	 * Adds a resource link to an existing API client
	 */
	public void addApiClientResource(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams) throws Exception
	{
		APIClient client = APIClient.getAPIClient(callParams.get(ParamNames.target_client_id));
		if (client == null)
			throw new UnknownObjectException(String.format("Could not find API client '%s'", callParams.get(ParamNames.target_client_id)));

		ResourceDefinition rd = ResourceDefinition.getByName(callParams.get(ParamNames.resource_name));
		if (rd == null)
			throw new UnknownObjectException("Could not find resource");
		
		RoleDefinition role = rd.getRoleByName(callParams.get(ParamNames.role_name));
		if (role == null)
			throw new UnknownObjectException("Could not find role");

		client.addResourceLink(new ResourceLink(rd, role.getName()));
		client.save(callParams.get(ParamNames.client_id));

		APIResponse.httpOk(channel, APIResponse.success(), callParams);
	}
	
	/**
	 * Returns details about an API client
	 */
	public void getApiClient(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams) throws Exception
	{
		APIClient client = APIClient.getAPIClient(callParams.get(ParamNames.target_client_id));
		if (client == null)
			throw new UnknownObjectException(String.format("Could not find API client '%s'", callParams.get(ParamNames.target_client_id)));

		APIResponse.httpOk(channel, APIResponse.success(getAPIClientResponse(client)), callParams);
	}

	/**
	 * Returns API Client details
	 * 
	 * @param client
	 * @return
	 */
	private DBObject getAPIClientResponse(APIClient client)
	{
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

		return ret;
	}
	
}
