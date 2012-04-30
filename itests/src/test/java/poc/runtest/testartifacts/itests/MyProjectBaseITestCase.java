package poc.runtest.testartifacts.itests;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @since 4/30/12
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/META-INF/testartifacts/myproject-itest-context.xml"})
public abstract class MyProjectBaseITestCase {
}
