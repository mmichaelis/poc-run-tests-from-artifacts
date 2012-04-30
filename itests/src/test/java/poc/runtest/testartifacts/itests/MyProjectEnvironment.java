package poc.runtest.testartifacts.itests;

import org.springframework.beans.factory.annotation.Value;

import javax.inject.Named;

/**
 * @since 4/30/12
 */
public class MyProjectEnvironment {
  private String serverUrl;
  private String serverName;

  public String getServerUrl() {
    return serverUrl;
  }

  public void setServerUrl(final String serverUrl) {
    this.serverUrl = serverUrl;
  }

  public String getServerName() {
    return serverName;
  }

  public void setServerName(final String serverName) {
    this.serverName = serverName;
  }
}
