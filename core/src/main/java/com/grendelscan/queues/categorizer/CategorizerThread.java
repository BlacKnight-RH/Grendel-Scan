package com.grendelscan.queues.categorizer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.categorizers.interfaces.TransactionCategorizer;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.queues.AbstractScanQueue;
import com.grendelscan.queues.AbstractTransactionBasedQueueThread;
import com.grendelscan.queues.QueueThreadGroup;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.testing.jobs.TestJob;
import com.grendelscan.testing.modules.AbstractTestModule;

public class CategorizerThread extends AbstractTransactionBasedQueueThread
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CategorizerThread.class);

    public CategorizerThread(final QueueThreadGroup threadGroup)
    {
        super(threadGroup);
    }

    @Override
    protected AbstractScanQueue getQueue()
    {
        return Scan.getInstance().getCategorizerQueue();
    }

    private void joinJobLists(final Map<AbstractTestModule, Set<TestJob>> target, final Map<AbstractTestModule, Set<TestJob>> source)
    {
        for (AbstractTestModule module : source.keySet())
        {
            if (target.containsKey(module))
            {
                Set<TestJob> tests = target.get(module);
                for (TestJob job : source.get(module))
                {
                    tests.add(job);
                }
            }
            else
            {
                target.put(module, source.get(module));
            }
        }
    }

    @Override
    protected void processNextTransaction(final StandardHttpTransaction transaction) throws InterruptedScanException
    {
        Map<AbstractTestModule, Set<TestJob>> jobs = new HashMap<AbstractTestModule, Set<TestJob>>();
        if (transaction.getRequestOptions().tokenSubmission)
        {
            /*
             * Token submissions should only be reviewed for token output, and not used for other types of testing
             */
            handlePause_isRunning();
            Map<AbstractTestModule, Set<TestJob>> tempJobs = Scan.getInstance().getCategorizers().getByOutputContextCategorizer().analyzeTransaction(transaction);
            joinJobLists(jobs, tempJobs);
        }
        else
        {
            for (TransactionCategorizer categorizer : Scan.getInstance().getCategorizers().getTransactionCategorizers())
            {
                handlePause_isRunning();
                Map<AbstractTestModule, Set<TestJob>> tempJobs = categorizer.analyzeTransaction(transaction);
                joinJobLists(jobs, tempJobs);
            }
        }
        LOGGER.trace(jobs.size() + " tests created for transaction #" + transaction.getId());
        Scan.getInstance().getTesterQueue().submitJobs(jobs);
    }

}
