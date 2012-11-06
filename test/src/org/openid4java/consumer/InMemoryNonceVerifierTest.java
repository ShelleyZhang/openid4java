/*
 * Copyright 2006-2008 Sxip Identity Corporation
 */

package org.openid4java.consumer;

import static org.junit.Assert.*;

import org.openid4java.server.IncrementalNonceGenerator;
import org.openid4java.server.NonceGenerator;

/**
 * @author Marius Scurtescu, Johnny Bufu
 */
public class InMemoryNonceVerifierTest extends AbstractNonceVerifierTest
{
	@Override
	public NonceVerifier createVerifier(int maxAge)
	{
		return new InMemoryNonceVerifier(maxAge);
	}

	@Override
	public void testNonceCleanup() throws Exception
	{
		NonceGenerator nonceGenerator = new IncrementalNonceGenerator();
		_nonceVerifier = createVerifier(1);
		String nonce = nonceGenerator.next();
		assertEquals(NonceVerifier.OK, _nonceVerifier.seen("http://example.com", nonce));
		Thread.sleep(1000);

		InMemoryNonceVerifier inMemoryVerifier = (InMemoryNonceVerifier) _nonceVerifier;
		assertEquals(null, inMemoryVerifier.get(nonce));
	}
}
