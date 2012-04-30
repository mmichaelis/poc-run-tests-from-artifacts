# Proof of Concept: Running Tests from thirdparty-jar

## The Story

**In order to** run a given set of tests in different workspaces and/or environments
**As** a tester
**I want to** execute deployed tests from the repository bundled as JAR.

## Discussion

Some of you might know this use case:

* You have one product which combines many submodules.

* All submodules are developed by independent development teams.

* Each submodule has its own integration tests.

* What is missing is a system test for all the submodules combined in the product.

* What you wish to do is to run tests from the submodules again on the product.

## The Problem

Exactly this use case can be found in the [Maven Failsafe Plugin FAQ][]:
[How can I reuse my test code in other modules?][FAQ-reuse-test-code]

The FAQ points to the [Maven Guide to using attached tests][maven-guide-attached-tests] (which again points to the
[test-jar-Mojo][]). Quite helpful, as you learn that there is a packaging-type `test-jar` which you might use for this
purpose.

Unfortunately there is one problem: While from the artifact side everything is well prepared the main Plugins to
run the tests don't support this feature, which are:

* [Maven Failsafe Plugin][]
* [Maven Surefire Plugin][]

This issue is reported as [SUREFIRE-569][] and is unresolved since 2009. So it is unlikely that it will ever be fixed.

## The Solutions

### Extract Using Dependency Plugin

A suggested solution in the comments to [SUREFIRE-569][] is to use the dependency plugin and extract the test sources
to the pass {{target/test-classes}}.

**Drawback:** You have to fiddle around with the build process.

### Use Suite Files

An alternative is to agree on a kind of Test-API between the main project and its submodules. Every submodule could
deliver a suite file like for example:

```java
@RunWith(Suite.class)
@Suite.SuiteClasses({
  Module1FirstITest.class,
  Module1SecondITest.class
})
public class Module1ITestSuite {
}
```

Now the main project can use these suite files (packaged with packaging type `test-jar`) to run the tests as suite:

```java
@RunWith(Suite.class)
@Suite.SuiteClasses({Module1ITestSuite.class, Module2ITestSuite.class})
public class SystemITestSuite {
}
```

**Drawback:**

* You will need to manually add test classes as suite.

**Advantages:**

* Your submodule projects can easily control which tests will be executed in the system test.

* The system test can exclude tests from submodules (if they are not ready yet for example). As further enhancement you
  might decide to have such a hook-class for every submodule so that you can even exclude them at command line.

* The reporting looks nice. For example in your IDE you will have a result tree like this:

> * `SystemITestSuite`
>     * `Model1ITestSuite`
>         * `Module1FirstITest`
>             * `shouldWorkThat`
>             * `shouldWorkThis`
>     * `Model2ITestSuite`

## Advanced Use Case

If this is really an advanced use case is hard to answer. I guess this is the default use case when having system
tests. You most likely will have a server to test. And this server has a host and port to connect to. While this
is a local test server for the submodule the whole product might run on a totally different server.

So the question is: *How to run the same tests with a different environment?*

The easiest answer on this I found is using [Spring][]. You can provide configurations as context-configurations and
access them in your tests via Injection. I have two solutions in mind which I will mention here:

### Environment Class with @Value-annotations

First of all you might use an environment class as managed bean which holds properties like this:

```java
@Value("${server.host:localhost}")
private String host;
```

Your tests will use this configuration to get all environmental parameters.

**Drawback:** The properties which you might set from the outside are hidden in this class. You have to document them
clearly and be careful on refactorings. Actually you will never be able to rename the properties. And for the product
tests it requires that all properties from the different modules come with a clear naming scheme to avoid collisions.

Having this you might end up with properties like `module1.server.host` and `module2.server.host` where actually both
are the same. Or even worse with `module1.server.host` and `module1.server.port` and in contrast wit
`module2.server.url` which is actually the same as `http://${module1.server.host}:${module1.server.port}/`.

### Environment Class instantiated in Context Configuration

To solve the previous drawback is to leave it up to the tests to configure the properties. Environment classes are
instantiated through the Context configuration.

The API you define is the location of the context configuration and the class which contains the configuration. Your
submodule tests then will need a base class configured like this:

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/META-INF/testartifacts/myproject-itest-context.xml"})
public abstract class MyProjectBaseITestCase {
}
```

And in the folder `src/test/resources/META-INF/testartifacts` of your submodule's integration tests you add a file
`myproject-itest-context.xml`.

Now as being the product who wants to run the tests all you have to do is override `myproject-itest-context.xml`.

The XML-File itself might look like this:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd">

  <!-- Override through system properties; user defaults in USER_HOME/.module.test.properties -->

  <context:property-placeholder
          ignore-resource-not-found="true"
          ignore-unresolvable="true"
          local-override="true"
          system-properties-mode="OVERRIDE"
          order="1"
          location="file:${user.home}/.module.test.properties"/>

  <context:component-scan base-package="poc.runtest.testartifacts.itests"/>

  <bean id="myproject_environment" class="poc.runtest.testartifacts.itests.MyProjectEnvironment">
    <property name="serverName" value="Project: ${server.name:NameFromProjectXml}"/>
    <property name="serverUrl" value="http://${server.host:HostFromProjectXml}:${server.port:PortFromProjectXml}/"/>
  </bean>

</beans>
```

And the environment-snippet for the product might look like this:

```xml
<bean id="myproject_environment" class="poc.runtest.testartifacts.itests.MyProjectEnvironment">
  <property name="serverName" value="System: ${server.name:NameFromSystemXml}"/>
  <property name="serverUrl" value="http://${server.host:HostFromSystemXml}:${server.port:PortFromSystemXml}/"/>
</bean>
```

## Test It

Run `mvn clean verify` to see the system tests running by the failsafe-plugin.

[Maven Failsafe Plugin]: <http://maven.apache.org/plugins/maven-failsafe-plugin/> "Maven Failsafe Plugin"
[Maven Surefire Plugin]: <http://maven.apache.org/plugins/maven-surefire-plugin/index.html> "Maven Surefire Plugin"
[Maven Failsafe Plugin FAQ]: <http://maven.apache.org/plugins/maven-failsafe-plugin/faq.html> "maven-failsafe-plugin: Frequently Asked Questions"
[FAQ-reuse-test-code]: <http://maven.apache.org/plugins/maven-failsafe-plugin/faq.html#reuse-test-code> "maven-failsafe-plugin: Frequently Asked Questions"
[test-jar-Mojo]: <http://maven.apache.org/plugins/maven-jar-plugin/test-jar-mojo.html> "maven-jar-plugin: test-jar"
[maven-guide-attached-tests]: <http://maven.apache.org/guides/mini/guide-attached-tests.html> "Maven Guide for executing attached tests"
[SUREFIRE-569]: <http://jira.codehaus.org/browse/SUREFIRE-569> "[SUREFIRE-569] There should be a way to run unit tests from a dependency jar"
[Spring]: <http://www.springsource.org/> "SpringSource.org"