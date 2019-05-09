package com.untzuntz.ustackserver;

import com.untzuntz.ustack.data.AccessToken;
import com.untzuntz.ustack.main.UAppCfg;
import com.untzuntz.ustack.main.UOpts;
import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.auth.GuestAccessTokenAuth;
import com.untzuntz.ustackserverapi.auth.LogoutUtil;
import com.untzuntz.ustackserverapi.params.ParamNames;
import org.junit.Test;

import java.util.UUID;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class TokenLogoutTest {

    @Test
    public void testTokenFormat() throws Exception {
        // create standard token
        String token1 = AccessToken.encode("Test1", "Test1@test.com", 500000);

        // create token w/ custom data
        String token2 = AccessToken.encodeJwt("Test2", "Test2@test.com", 500000, "1.2.3.4", null);

        assertFalse(token1.equals(token2));

        AccessToken.AccessTokenDetails d1 = AccessToken.decode(token1);

        assertEquals("Test1", d1.getClientId());
        assertEquals("Test1@test.com", d1.getUserName());
        assertNull(d1.getIpAddress());
        assertNull(d1.getCustomData());

    }

    @Test
    public void testTokenLogout() throws Exception {

        UOpts.setCacheFlag(true);
        System.setProperty(UAppCfg.CACHE_HOST_STRING, "localhost:11211");

        // create a token
        String userName = "TestCaseUser@" + UUID.randomUUID().toString() + ".com";
        String token = AccessToken.encode("test-api", userName, 60000);

        // confirm token auth is success
        CallParameters params = new CallParameters("/");
        params.setParameterValue("token", token);

        GuestAccessTokenAuth gta = new GuestAccessTokenAuth();
        gta.authenticate(null, null, params);

        // 'logout'
        AccessToken.AccessTokenDetails decodedToken = AccessToken.decode( params.get(ParamNames.token) );
        LogoutUtil.logoutToken(decodedToken);

        Thread.sleep(500);

        // confirm token is not valid
        try {
            gta.authenticate(null, null, params);
            fail();
        } catch (APIException e) {

        }

    }

}
