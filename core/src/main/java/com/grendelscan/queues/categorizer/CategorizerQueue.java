package com.grendelscan.queues.categorizer;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.grendelscan.queues.AbstractQueueThread;
import com.grendelscan.queues.AbstractTransactionBasedQueue;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.Scan;

public class CategorizerQueue extends AbstractTransactionBasedQueue
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CategorizerQueue.class);
	private final static String SQL_TABLE_NAME = "categorizer_queue";
	
	/** Creates a new instance of requesterQueue */
	public CategorizerQueue()
	{
		super("Categorizer queue", SQL_TABLE_NAME);
	}
	
	@Override
	public void addTransaction(StandardHttpTransaction transaction)
	{
		super.addTransaction(transaction);
	}

	@Override
	public void addTransactions(Collection<StandardHttpTransaction> transactions)
	{
		super.addTransactions(transactions);
	}

	@Override
	protected int getMaxThreadCount()
	{
		return Scan.getScanSettings().getMaxCategorizerThreads();
	}


	@Override
	protected AbstractQueueThread getNewThread()
	{
		return new CategorizerThread(getThreadGroup());
	}



	@Override
	protected boolean checkSubmittedTransaction(StandardHttpTransaction transaction)
	{
		if (transaction.isSuccessfullExecution())
		{
			return true;
		}
		Exception e = new IllegalStateException("Unexecuted transaction submitted for categorization");
		LOGGER.error(e.toString(), e);
		return false;
	}

	@Override
	protected String getDBPath()
	{
		return "categorizer-queue.db";
	}

}