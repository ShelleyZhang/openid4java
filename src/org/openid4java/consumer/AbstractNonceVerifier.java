/*
 * Copyright 2006-2008 Sxip Identity Corporation
 */

package org.openid4java.consumer;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openid4java.util.InternetDateFormat;

/**
 * @author Marius Scurtescu, Johnny Bufu
 */
public abstract class AbstractNonceVerifier implements NonceVerifier
{
	private static Log _log = LogFactory.getLog(AbstractNonceVerifier.class);

	private static final boolean DEBUG = _log.isDebugEnabled();

	protected int _maxAgeSeconds;

	/**
	 * @param maxAge
	 *            maximum token age in seconds
	 */
	protected AbstractNonceVerifier(int maxAge)
	{
		_maxAgeSeconds = maxAge;
	}

	@Override
	public int getMaxAge()
	{
		return _maxAgeSeconds;
	}

	@Override
	public void setMaxAge(int ageSeconds)
	{
		_maxAgeSeconds = ageSeconds;
	}

	/**
	 * Checks if nonce date is valid and if it is in the max age boundary. Other checks
	 * are delegated to {@link #seen(java.util.Date, String, String)}
	 */
	@Override
	public int seen(String opUrl, String nonce)
	{
		if (DEBUG)
			_log.debug("Verifying nonce: " + nonce);

		Date now = new Date();

		try
		{
			Date nonceDate = new InternetDateFormat().parse(nonce);

			if (isTooOld(now, nonceDate))
			{
				_log.warn("Nonce is too old: " + nonce);
				return TOO_OLD;
			}

			return seen(now, opUrl, nonce);
		}
		catch (ParseException e)
		{
			_log.error("Error verifying the nonce: " + nonce, e);
			return INVALID_TIMESTAMP;
		}
	}

	/**
	 * Subclasses should implement this method and check if the nonce was seen before. The
	 * nonce timestamp was verified at this point, it is valid and it is in the max age
	 * boudary.
	 * 
	 * @param now
	 *            The timestamp used to check the max age boudary.
	 */
	protected abstract int seen(Date now, String opUrl, String nonce);

	protected boolean isTooOld(Date now, Date nonce)
	{
		long age = now.getTime() - nonce.getTime();

		return age > _maxAgeSeconds * 1000;
	}
}
