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

import static org.junit.Assert.fail;

public class TokenLogoutTest {

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
