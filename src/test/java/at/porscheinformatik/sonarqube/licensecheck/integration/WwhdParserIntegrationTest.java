package at.porscheinformatik.sonarqube.licensecheck.integration;

import at.porscheinformatik.sonarqube.licensecheck.ProjectResolver;
import at.porscheinformatik.sonarqube.licensecheck.golang.WwhdParser;
import at.porscheinformatik.sonarqube.licensecheck.interfaces.Scanner;
import at.porscheinformatik.sonarqube.licensecheck.model.Dependency;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class WwhdParserIntegrationTest {

    private static File projectRoot;

    @Before
    public void setup() throws IOException {
        final File parentFolder = new File(this.getClass().getClassLoader().getResource("golang/go-licenses.txt").getPath()).getParentFile();
        projectRoot = ProjectResolver.prepareProject(() -> parentFolder, (file) -> {
        });
    }

    @Test
    public void scan() {
        Scanner scanner = new WwhdParser();

        List<Dependency> dependencies = scanner.scan(projectRoot);

        assertThat(dependencies, hasSize(123));
        assertThat(dependencies, hasItem(new Dependency("github.com/xeipuuv/gojsonreference", null, (String) null)));
        assertThat(dependencies, hasItem(new Dependency("github.com/hashicorp/hcl/hcl/printer", null, "MPL-2.0")));
    }
}
