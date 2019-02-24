package at.porscheinformatik.sonarqube.licensecheck.integration;

import at.porscheinformatik.sonarqube.licensecheck.gradle.GradleDependencyScanner;
import at.porscheinformatik.sonarqube.licensecheck.gradle.GradleProjectResolver;
import at.porscheinformatik.sonarqube.licensecheck.model.Dependency;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.sonar.api.config.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static at.porscheinformatik.sonarqube.licensecheck.sonarqube.SonarqubeConfigurationHelper.mockConfiguration;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class GradleDisableTest {

    private static File projectRoot;

    @Before
    public void setup() throws IOException {
        projectRoot = GradleProjectResolver.prepareGradleProject();
    }


    @Parameterized.Parameters
    public static List<String> data() {
        return Arrays.asList("5.1.1", "4.10.3", "3.5.1");
    }

    @Parameterized.Parameter
    public String version;

    @Test
    public void scanWithMatch() throws IOException {
        GradleProjectResolver.loadGradleWrapper(projectRoot, version);
        Configuration configuration = Mockito.mock(Configuration.class);
        mockConfiguration(configuration);
        GradleDependencyScanner gradleDependencyScanner = new GradleDependencyScanner(configuration);

        List<Dependency> dependencies = gradleDependencyScanner.scan(projectRoot);

        assertThat(dependencies, hasSize(13));
        assertThat(dependencies, hasItem(
            new Dependency("org.spockframework:spock-core",
                "1.1-groovy-2.4",
                "The Apache Software License, Version 2.0")));
        assertThat(dependencies, hasItem(
            new Dependency("org.tukaani:xz",
                "1.5",
                "Public Domain")));
    }
}
