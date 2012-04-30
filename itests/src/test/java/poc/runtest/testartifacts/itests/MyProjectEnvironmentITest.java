package poc.runtest.testartifacts.itests;

import org.junit.Test;
import poc.runtest.testartifacts.project.MyProject;

import javax.inject.Inject;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @since 4/30/12
 */
public class MyProjectEnvironmentITest extends MyProjectBaseITestCase {
  @Inject
  private MyProjectEnvironment environment;

  @Test
  public void shouldReturnNonNullForServerUrl() {
    assertNotNull(environment.getServerUrl());
  }

  @Test
  public void shouldReturnNonNullForServerName() {
    assertNotNull(environment.getServerName());
    System.out.println("Server Name: " + environment.getServerName());
  }
}
