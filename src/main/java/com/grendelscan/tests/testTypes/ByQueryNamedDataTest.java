/**
 * 
 */
package com.grendelscan.tests.testTypes;

import com.grendelscan.requester.http.dataHandling.containers.NamedDataContainer;
import com.grendelscan.scan.InterruptedScanException;

public interface ByQueryNamedDataTest extends TestType
{
	public void testByQueryNamedData(int transactionId, NamedDataContainer datum, int testJobId) throws InterruptedScanException;
}