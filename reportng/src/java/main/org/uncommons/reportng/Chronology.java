package org.uncommons.reportng;

import java.util.Date;
import java.util.Set;
import java.util.SortedSet;

import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Iterables.filter;
import com.google.common.collect.TreeMultimap;

/**
 * Represents the execution history of a testsuite.
 * TODO: Can suites execute concurrently? I think not.
 */
public class Chronology 
{
    public static class TimedMethod implements Comparable<TimedMethod>
    {
        private final ITestResult result;
        
        public TimedMethod(ITestResult result)
        {
            this.result = result;
        }
        
        public String getName()
        {
            return result.getName();
        }
        
        public String getClassName()
        {
            return result.getMethod().getRealClass().getSimpleName();
        }
        
        public String getParameters()
        {
            return Joiner.on(", ").join(result.getParameters());
        }
        
        public ITestNGMethod getMethod() 
        {
            return result.getMethod();
        }

        public Date getStartTime()
        {
            return new Date(result.getStartMillis());
        }
        
        public long getStartTimeMillis()
        {
            return result.getStartMillis();
        }
        
        public long getEndTimeMillis()
        {
            return result.getEndMillis();
        }
        
        public int getDurationMillis()
        {
            return (int) (result.getEndMillis() - result.getStartMillis());
        }
        
        public boolean isSkipped()
        {
            return result.getStatus() == ITestResult.SKIP;
        }
        
        public boolean isSuccess()
        {
            return result.isSuccess();
        }
        
        public boolean isFailure()
        {
            return result.getStatus() == ITestResult.FAILURE;
        }
        
        public Throwable getFailure()
        {
            return result.getThrowable();
        }
        
        public String getStackTrace()
        {
            Throwable exception = getFailure();
            return Joiner.on('\n').join(exception.getStackTrace());
        }

        public String getThreadName() 
        {
            return result.getMethod().getId();
        }

        @Override
        public int compareTo(TimedMethod o) 
        {
            return (int) (getStartTimeMillis() - o.getStartTimeMillis());
        }
    }
    
    private static final Predicate<TimedMethod> IS_TEST_FILTER = 
        new Predicate<TimedMethod>() {
            @Override public boolean apply(TimedMethod input) {
                return input.getMethod().isTest();
            }
        };
        
    private final TreeMultimap<String,TimedMethod> testingThreads;
    private final ISuite suite;
    private int averageDuration;
    private long suiteEndTime = Long.MIN_VALUE;
    private long suiteStartTime = Long.MAX_VALUE;
    
    public Chronology(ISuite suite) 
    {
        this.suite = suite;
        this.testingThreads = TreeMultimap.create();
        
        partitionMethodsByThread();
    }

    private void partitionMethodsByThread() 
    {
        for (ISuiteResult result : suite.getResults().values())
        {
            ITestContext context = result.getTestContext();
            add(context.getPassedTests());
            add(context.getFailedButWithinSuccessPercentageTests());
            add(context.getFailedConfigurations());
            add(context.getFailedTests());
            add(context.getSkippedTests());
        }
    }
    
    private void add(IResultMap resultMap)
    {
        if ( resultMap != null )
        {
            Set<ITestResult> all = resultMap.getAllResults();
            if ( all != null )
            {
                for (ITestResult result : all) 
                {
                    TimedMethod timedMethod = new TimedMethod(result);
                    testingThreads.put(result.getMethod().getId(), timedMethod);
                    updateCumulativeMovingAverage(timedMethod);
                    updateSuiteTimes(timedMethod);
                }
            }
        }
    }
    
    private void updateSuiteTimes(TimedMethod timedMethod) 
    {
        if ( timedMethod.getStartTimeMillis() < suiteStartTime )
        {
            suiteStartTime = timedMethod.getStartTimeMillis();
        }
        
        if ( timedMethod.getEndTimeMillis() > suiteEndTime ) 
        {
            suiteEndTime = timedMethod.getEndTimeMillis();
        }
    }

    private void updateCumulativeMovingAverage(TimedMethod timedMethod) 
    {
        int update = (int)((timedMethod.getDurationMillis() - averageDuration) / (float)testingThreads.size());
        averageDuration += update;
    }

    public ISuite getSuite()
    {
        return suite;
    }
    
    public int getThreadCount() 
    {
        return testingThreads.keySet().size();
    }
    
    public Set<String> getThreadNames()
    {
        return testingThreads.keySet();
    }

    public SortedSet<TimedMethod> getMethods(String threadName) 
    {
        return testingThreads.get(threadName);
    }
    
    public long getThreadDuration(String threadName)
    {
        TimedMethod last = getMethods(threadName).last();
        return last.getEndTimeMillis() - getSuiteStartTime();
    }

    public long getSuiteStartTime()
    {
        return suiteStartTime;
    }
    
    public int getAverageDuration() 
    {
        return averageDuration;
    }

    public long getTotalDuration() 
    {
        return suiteEndTime - suiteStartTime;
    }

    public int getTotalTests()
    {
        return size(filter(testingThreads.values(), IS_TEST_FILTER));
    }
    
    public TimedMethod getPreceedingMethod(TimedMethod method) 
    {
        SortedSet<TimedMethod> methodsInThread = getMethods(method.getThreadName());
        SortedSet<TimedMethod> preceeding = methodsInThread.headSet(method);
        return preceeding.isEmpty() ? null : preceeding.last();
    }
}
