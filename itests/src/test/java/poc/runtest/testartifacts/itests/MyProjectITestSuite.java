package poc.runtest.testartifacts.itests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @since 4/30/12
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({MyProjectITest.class, MyProjectEnvironmentITest.class})
public class MyProjectITestSuite {
}
