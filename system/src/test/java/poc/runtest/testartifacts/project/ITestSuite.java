package poc.runtest.testartifacts.project;

import poc.runtest.testartifacts.itests.MyProjectITestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @since 4/30/12
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({MyProjectITestSuite.class})
public class ITestSuite {
}
