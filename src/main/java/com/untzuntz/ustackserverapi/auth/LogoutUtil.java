package com.untzuntz.ustackserverapi.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.untzuntz.ustack.data.AccessToken;
import com.untzuntz.ustack.data.UDataCache;
import com.untzuntz.ustack.main.UOpts;
import com.untzuntz.ustackserverapi.exceptions.UserAuthenticationException;
import org.apache.log4j.Logger;

public class LogoutUtil {

    static Logger logger               	= Logger.getLogger(LogoutUtil.class);

    public static void logoutToken(AccessToken.AccessTokenDetails token) {

        long remainingTokenLife = token.getExpirationAge() - System.currentTimeMillis();
        int remainingTokenLifeSec = (int)(remainingTokenLife / 1000L);

        String key = String.format("logout_%s_%d", token.getUserName(), token.getExpirationAge());
        if (UOpts.getCacheEnabled()) {
            UDataCache.getInstance().set(key, remainingTokenLifeSec, "T");
        }
        logger.info("Logging out : " + key + " (" + remainingTokenLifeSec + ")");

    }

    public static void checkTokenLogout(AccessToken.AccessTokenDetails token) throws UserAuthenticationException {

        String key = String.format("logout_%s_%d", token.getUserName(), token.getExpirationAge());
        if (UOpts.getCacheEnabled() && UDataCache.getInstance().get(key) != null) {
            throw new UserAuthenticationException("User Logged Out", "LoggedOut");
        }
    }

    public static void checkTokenLogout(DecodedJWT token) throws UserAuthenticationException {

        String key = String.format("logout_jwt_%s", token.getClaim("id"));
        if (UOpts.getCacheEnabled() && UDataCache.getInstance().get(key) != null) {
            throw new UserAuthenticationException("User Logged Out", "LoggedOut");
        }
    }

}
