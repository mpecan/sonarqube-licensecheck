package at.porscheinformatik.sonarqube.licensecheck.integration;

import at.porscheinformatik.sonarqube.licensecheck.gradle.GradleDependencyScanner;
import at.porscheinformatik.sonarqube.licensecheck.gradle.GradleProjectResolver;
import at.porscheinformatik.sonarqube.licensecheck.model.Dependency;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.sonar.api.config.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    public void shouldDisableGradleDependenciesScan() throws IOException {
        GradleProjectResolver.loadGradleWrapper(projectRoot, version);
        Configuration configuration = Mockito.mock(Configuration.class);
        Mockito.when(configuration.getBoolean(Matchers.anyString())).thenReturn(Optional.of(true));
        GradleDependencyScanner gradleDependencyScanner = new GradleDependencyScanner(configuration);

        List<Dependency> dependencies = gradleDependencyScanner.scan(projectRoot);

        assertThat(dependencies, hasSize(0));
    }
}
