package com.grendelscan.testing.modules.types;

import com.grendelscan.scan.InterruptedScanException;

/**
 * 
 * @author David Byrne
 */
public interface FinalAnalysisTest extends TestType
{
	public void runAnalysis(int testJobId) throws InterruptedScanException;
}
