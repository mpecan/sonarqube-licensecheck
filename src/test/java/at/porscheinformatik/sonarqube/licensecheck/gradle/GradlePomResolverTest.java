package at.porscheinformatik.sonarqube.licensecheck.gradle;

import at.porscheinformatik.sonarqube.licensecheck.model.Dependency;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GradlePomResolverTest {


    private static File projectRoot;

    // todo: mock GradleInvoker

    @Before
    public void setup() throws IOException {
        projectRoot = new File("target/testProject");
        FileUtils.deleteDirectory(projectRoot);
        projectRoot.mkdirs();

        File buildGradleSrc = new File(this.getClass().getClassLoader().getResource("gradle/build.gradle").getFile());
        File buildGradleTrg = new File(projectRoot, "build.gradle");
        FileUtils.copyFile(buildGradleSrc, buildGradleTrg);
    }

    @Test
    public void resolvePoms() throws Exception {
        GradlePomResolver gradlePomResolver = new GradlePomResolver(projectRoot);

        List<Dependency> poms = gradlePomResolver.resolveDependencies();

        Dependency pom = new Dependency("org.codehaus.groovy:groovy-all", "2.3.1", "The Apache Software License, Version 2.0");
        Assert.assertThat(poms, Matchers.hasItem(pom));
    }
}
