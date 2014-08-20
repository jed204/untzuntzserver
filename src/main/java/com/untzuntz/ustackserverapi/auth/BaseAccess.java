package com.untzuntz.ustackserverapi.auth;

import com.untzuntz.ustack.data.UDataCache;
import com.untzuntz.ustack.main.UOpts;

public abstract class BaseAccess implements AuthorizationInt {

	/**
	 * Check the cache to determine if this user has access
	 * 
	 * @param accountId
	 * @param studyId
	 * @return
	 */
	public CachedAccess cacheAccess(String key)
	{
		if (UOpts.getCacheEnabled())
		{
			if ("T".equals( (String)UDataCache.getInstance().get(key) ))
				return CachedAccess.True;
			else if ("F".equals( (String)UDataCache.getInstance().get(key) ))
				return CachedAccess.False;
		}
		
		return CachedAccess.None;
	}
	
	/**
	 * Set the cache based on the result of an access lookup
	 * 
	 * @param accountId
	 * @param studyId
	 * @param access
	 */
	public void setCachedAccess(String key, CachedAccess access)
	{
		if (UOpts.getCacheEnabled()) // positive cache
			UDataCache.getInstance().set(key, access.getCacheTTL(), access.getType());
	}
}