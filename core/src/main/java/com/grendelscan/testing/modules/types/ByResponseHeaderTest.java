package com.grendelscan.testing.modules.types;

import com.grendelscan.scan.InterruptedScanException;

public interface ByResponseHeaderTest extends TestType
{
	public String[] getResponseHeaders();

	public void testByResponseHeader(int transactionID, String responseHeaderName, int testJobId) throws InterruptedScanException;
}
