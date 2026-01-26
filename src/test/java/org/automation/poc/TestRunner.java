package org.automation.poc;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"org.automation.poc.stepdefinitions", "org.automation.poc"},
    tags = "@smoke",
    plugin = {
        "pretty",
        "html:target/cucumber-reports/cucumber.html",
        "json:target/cucumber-reports/cucumber.json",
        "usage:target/cucumber-reports/cucumber-usage.json"
    },
    monochrome = true,
    dryRun = false
)

@Test
public class TestRunner extends AbstractTestNGCucumberTests {

    // Override DataProvider to control parallel execution of scenarios
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
