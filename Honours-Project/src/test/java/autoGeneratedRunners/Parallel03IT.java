package autoGeneratedRunners;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
        strict = true,
        features = {"C:/Users/Thinkpad/Documents/GitHub/Honours-Project/Honours-Project/src/test/resources/features/Server_scenarios.feature"},
        plugin = {"json:C:/Users/Thinkpad/Documents/GitHub/Honours-Project/Honours-Project/target/cucumber-parallel/3.json"},
        monochrome = false,
        tags = {},
        glue = {"stepDefs"})
public class Parallel03IT {
}