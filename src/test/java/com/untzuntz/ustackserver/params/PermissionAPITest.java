package com.untzuntz.ustackserver.params;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.untzuntz.ustack.aaa.ResourceDefinition;
import com.untzuntz.ustack.aaa.ResourceLink;
import com.untzuntz.ustack.aaa.RoleDefinition;
import com.untzuntz.ustack.data.APIClient;
import com.untzuntz.ustackserver.Main;
import com.untzuntz.ustackserver.api.PermissionsAPI;
import com.untzuntz.ustackserver.util.BaseTest;
import com.untzuntz.ustackserver.util.TestCallParams;
import com.untzuntz.ustackserverapi.params.ParamNames;

public class PermissionAPITest extends BaseTest {

	private static long runId;
	@BeforeClass public static void setupPaths()
	{
		runId = System.currentTimeMillis();
		new PermissionsAPI().setup("test");
	}
	
	@Test public void testResourceRolePermCreate() throws Exception
	{
//		DBObject res = new BasicDBObject();
//		res.put("resource_name", "TestResource-" + runId);
//		res.put("resource_type", "APIAccess");
//		
//		// create a resource def
//		handleCall("test/resource/save", getAPIClientParams(getAPIClient()).append(ParamNames.x_object, res.toString()));
//		assertEquals(HttpResponseStatus.OK.getCode(), channel.getResponseCode());
//		assertEquals("SUCCESS", apiResult.get("status"));
//		
//		// create a role
//		res.put("role_name", "TestRole1");
//		handleCall("test/resource/role/save", getAPIClientParams(getAPIClient()).append(ParamNames.x_object, res.toString()));
//		assertEquals(HttpResponseStatus.OK.getCode(), channel.getResponseCode());
//		assertEquals("SUCCESS", apiResult.get("status"));
//		
//		// create a permission on the role
//		res.put("permission", "FirstPermission");
//		handleCall("test/resource/role/permission/add", getAPIClientParams(getAPIClient()).append(ParamNames.x_object, res.toString()));
//		assertEquals(HttpResponseStatus.OK.getCode(), channel.getResponseCode());
//		assertEquals("SUCCESS", apiResult.get("status"));
//		
//		// get resource listing
//		handleCall("test/resource/list", getAPIClientParams(getAPIClient()));
//		assertEquals(HttpResponseStatus.OK.getCode(), channel.getResponseCode());
//		assertEquals("SUCCESS", apiResult.get("status"));
//
//		BasicDBList resources = (BasicDBList)responseObject.get("resources");
//		assertNotNull(resources);
//		assertTrue(resources.size() > 0);
//		
//		// get resource we created
//		handleCall("test/resource", getAPIClientParams(getAPIClient()).append(ParamNames.resource_name, (String)res.get("resource_name")));
//		assertEquals(HttpResponseStatus.OK.getCode(), channel.getResponseCode());
//		assertEquals("SUCCESS", apiResult.get("status"));
//		
//		ResourceDefinition def = new ResourceDefinition(responseObject);
//		assertNotNull(def);
//		
//		// confirm role
//		RoleDefinition roleDef = def.getRoleByName( (String)res.get("role_name") );
//		assertNotNull(roleDef);
//		
//		// confirm permission
//		assertTrue(roleDef.hasPermission( (String)res.get("permission") ));
	}
	
	public static TestCallParams getAPIClientParams(APIClient client) {
		return new TestCallParams(ParamNames.client_id, client.getClientId()).append(ParamNames.api_key, client.getValidKey()).append(ParamNames.client_ver,  "testcase");
	}

	public static APIClient getAPIClient() throws Exception {
		
		ResourceDefinition rs = Main.setupAPIResource();
				
		String clientId = "test-api-client";
		ResourceLink rl = new ResourceLink(rs, "API Manager");
		
		APIClient u = APIClient.getAPIClient(clientId);
		if (u == null)
		{
			u = APIClient.createAPI("TestCase", clientId);
			u.generateKey("TestCase");
			u.addResourceLink("TestCase", rl);
			u.save("Test Case");
		}
		
		return u;
	}

}
