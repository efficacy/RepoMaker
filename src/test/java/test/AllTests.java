package test;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
    
    public static TestSuite suite() {
        TestSuite ret = new TestSuite();

        ret.addTestSuite(DepFetcherTest.class);
        ret.addTestSuite(PomCrawlerTest.class);
        ret.addTestSuite(ErrorTests.class);
        ret.addTestSuite(SuccessTests.class);

        return ret;
    }

}
