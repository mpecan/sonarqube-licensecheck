package at.porscheinformatik.sonarqube.licensecheck.integration;

import at.porscheinformatik.sonarqube.licensecheck.ProjectResolver;
import at.porscheinformatik.sonarqube.licensecheck.interfaces.Scanner;
import at.porscheinformatik.sonarqube.licensecheck.ios.PlistParser;
import at.porscheinformatik.sonarqube.licensecheck.model.Dependency;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class PlistParserIntegrationTest {


    private static File projectRoot;

    @Before
    public void setup() throws IOException {
        final File parentFolder = new File(this.getClass().getClassLoader().getResource("ios/Pods-some-item-acknowledgements.plist").getPath()).getParentFile();
        projectRoot = ProjectResolver.prepareProject(() -> parentFolder, (file) -> {
        });
    }

    @Test
    public void shouldParseDependenciesFromLicenseReportFile() {
        Scanner scanner = new PlistParser();
        final List<Dependency> dependencies = scanner.scan(projectRoot);

        assertThat(dependencies, notNullValue());
        assertThat(dependencies, hasSize(greaterThan(5)));
        assertThat(dependencies.stream().map(Dependency::getLicense).filter(Objects::nonNull).collect(Collectors.toList()), hasSize(greaterThan(5)));
    }

}
