package poc.runtest.testartifacts.itests;

import poc.runtest.testartifacts.project.MyProject;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @since 4/30/12
 */
public class MyProjectITest extends MyProjectBaseITestCase {
  @Test
  public void shouldStoreBooleanFalseValue() {
    final MyProject project = new MyProject();
    project.setFlag(false);
    assertFalse(project.isFlag());
  }

  @Test
  public void shouldStoreBooleanTrueValue() {
    final MyProject project = new MyProject();
    project.setFlag(true);
    assertTrue(project.isFlag());
  }
}
