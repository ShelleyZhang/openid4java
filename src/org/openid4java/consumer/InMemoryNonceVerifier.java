/*
 * Copyright 2006-2008 Sxip Identity Corporation
 */

package org.openid4java.consumer;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * @author Marius Scurtescu, Johnny Bufu
 */
public class InMemoryNonceVerifier extends AbstractNonceVerifier
{
	private static Log _log = LogFactory.getLog(InMemoryNonceVerifier.class);

	private static final boolean DEBUG = _log.isDebugEnabled();

	private final Cache<String, String> cache;

	public InMemoryNonceVerifier()
	{
		this(60);
	}

	public InMemoryNonceVerifier(int maxAge)
	{
		super(maxAge);
		cache =
			CacheBuilder.newBuilder().maximumSize(1000).concurrencyLevel(16)
				.expireAfterWrite(maxAge, TimeUnit.SECONDS).recordStats().build();
	}

	@Override
	protected int seen(Date now, String opUrl, String nonce)
	{
		String pair = opUrl + '#' + nonce;

		String cachedPair = cache.getIfPresent(pair);
		if (cachedPair != null)
		{
			_log.error("Possible replay attack! Already seen nonce: " + nonce);
			return SEEN;
		}

		cache.put(pair, pair);

		if (DEBUG)
			_log.debug("Nonce verified: " + nonce);

		return OK;
	}

	protected String get(String nonce)
	{
		return cache.getIfPresent(nonce);
	}
}
