package autoGeneratedRunners;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
        strict = true,
        features = {"C:/Users/Thinkpad/Documents/GitHub/Honours-Project/project3004-master/src/test/resources/features/Client_one_scenarios.feature"},
        plugin = {"json:C:/Users/Thinkpad/Documents/GitHub/Honours-Project/project3004-master/target/cucumber-parallel/2.json"},
        monochrome = false,
        tags = {},
        glue = {"stepDefs"})
public class Parallel02IT {
}
