package com.example.sample;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("chaos-test.feature")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.example.common.chaos.lib,com.example.sample")
public class SampleAppCucumberTest {
}
