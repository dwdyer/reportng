package org.uncommons.reportng;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.annotations.Test;
import org.uncommons.reportng.Chronology.TimedMethod;

public class ChronologyTest 
{
    private static class TestResultBuilder 
    {
        ITestResult result       = mock(ITestResult.class);
        ITestNGMethod testMethod = mock(ITestNGMethod.class);
        
        public TestResultBuilder()
        {
            when(result.getMethod()).thenReturn(testMethod);
        }
        
        public TestResultBuilder withThread(String id)
        {
            when(testMethod.getId()).thenReturn(id);
            return this;
        }
        
        public TestResultBuilder withStart(long runStartTime) 
        {
            when(result.getStartMillis()).thenReturn(runStartTime);
            return this;
        }
        
        public TestResultBuilder withEnd(long runEndTime) 
        {
            when(result.getEndMillis()).thenReturn(runEndTime);
            return this;
        }
        
        public TestResultBuilder withName(String methodName) 
        {
            when(result.getName()).thenReturn(methodName);
            return this;
        }
        
        public ITestResult build()
        {
            return result;
        }
    }
    
    private static class MockSuiteBuilder
    {
        ISuite suite         = mock(ISuite.class);
        ISuiteResult result  = mock(ISuiteResult.class);
        ITestContext context = mock(ITestContext.class);
        IResultMap resultMap = mock(IResultMap.class);
        
        Set<ITestResult> results = new HashSet<ITestResult>(4);
        
        public MockSuiteBuilder add(TestResultBuilder... results)
        {
            for (TestResultBuilder result : results) 
            {
                add(result);
            }
            return this;
        }

        private void add(TestResultBuilder method) 
        {
            results.add(method.build());
        }
        
        public ISuite build()
        {
            Map<String, ISuiteResult> suiteResults = new HashMap<String, ISuiteResult>();
            suiteResults.put("what.is.this.key?", result);
            when(suite.getResults()).thenReturn(suiteResults);
            when(result.getTestContext()).thenReturn(context);
            when(context.getPassedTests()).thenReturn(resultMap);
            when(resultMap.getAllResults()).thenReturn(results);
            return suite;
        }
    }
    
    private TestResultBuilder method()
    {
        return new TestResultBuilder();
    }
    
    private ISuite suite(TestResultBuilder... methods)
    {
        MockSuiteBuilder suite = new MockSuiteBuilder();
        suite.add(methods);
        return suite.build();
    }
    
    @Test
    public void testThreadCountWhenOnlyOne()
    {
        ISuite suite = suite(
            method().withThread("main@1234"),
            method().withThread("main@1234"));
        Chronology chronology = new Chronology(suite);
        assertEquals(chronology.getThreadCount(), 1);
    }
    
    @Test
    public void testThreadCountWhenMoreThanOne()
    {
        ISuite suite = suite(
            method().withThread("thread-1@1234"), 
            method().withThread("thread-2@1235"), 
            method().withThread("thread-2@1235"));
        Chronology chronology = new Chronology(suite);
        assertEquals(chronology.getThreadCount(), 2);
    }
    
    public void testExecutionOrderOfTests()
    {
        ISuite suite = suite(
            method().withName("testB").withThread("thread-1@1234").withStart(200).withEnd(300),
            method().withName("testC").withThread("thread-1@1234").withStart(300).withEnd(400),
            method().withName("testA").withThread("thread-1@1234").withStart(100).withEnd(200));
        
        Chronology chronology = new Chronology(suite);
        String threadName = chronology.getThreadNames().iterator().next();
        Iterator<TimedMethod> methods = chronology.getMethods(threadName).iterator();
        assertEquals(methods.next().getName(), "testA");
        assertEquals(methods.next().getName(), "testB");
        assertEquals(methods.next().getName(), "testC");
    }
    
    @Test
    public void testGetDuration()
    {
        ISuite suite = suite(
            method().withName("testB").withThread("thread-1@1234").withStart(250).withEnd(300),
            method().withName("testC").withThread("thread-1@1234").withStart(300).withEnd(500),
            method().withName("testA").withThread("thread-1@1234").withStart(100).withEnd(250));
        
        Chronology chronology = new Chronology(suite);
        String threadName = chronology.getThreadNames().iterator().next();
        Iterator<TimedMethod> methods = chronology.getMethods(threadName).iterator();
        assertEquals(methods.next().getDurationMillis(), 150);
        assertEquals(methods.next().getDurationMillis(), 50);
        assertEquals(methods.next().getDurationMillis(), 200);
    }
    
    @Test
    public void testGetAverageTestDuration()
    {
        ISuite suite = suite(
            method().withName("testB").withThread("thread-1@1234").withStart(100).withEnd(400),  // 300
            method().withName("testC").withThread("thread-1@1234").withStart(400).withEnd(600),  // 200
            method().withName("testA").withThread("thread-2@1234").withStart(100).withEnd(200)); // 100
        
        Chronology chronology = new Chronology(suite);
        assertEquals(chronology.getAverageDuration(), 200);
    }
    
    @Test(enabled=false)
    public void testGetTotalDuration()
    {
        ISuite suite = suite(
            method().withName("testB").withThread("thread-1@1234").withStart(100).withEnd(400),  // 300
            method().withName("testC").withThread("thread-1@1234").withStart(400).withEnd(600),  // 200
            method().withName("testA").withThread("thread-2@1234").withStart(100).withEnd(200)); // 100
        
        Chronology chronology = new Chronology(suite);
        assertEquals(chronology.getTotalDuration(), 500);
    }
    
    @Test
    public void testGetPreceedingMethod()
    {
        ISuite suite = suite(
            method().withName("testB").withThread("thread-1@1234").withStart(100).withEnd(400),  // 300
            method().withName("testC").withThread("thread-1@1234").withStart(400).withEnd(600),  // 200
            method().withName("testA").withThread("thread-2@1234").withStart(100).withEnd(200)); // 100
        
        Chronology chronology = new Chronology(suite);
        SortedSet<TimedMethod> thread1 = chronology.getMethods("thread-1@1234");
        assertNull(chronology.getPreceedingMethod(thread1.first()));
        assertSame(chronology.getPreceedingMethod(thread1.last()), thread1.first());
    }
}
