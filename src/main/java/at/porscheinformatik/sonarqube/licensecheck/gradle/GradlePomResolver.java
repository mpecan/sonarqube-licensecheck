package at.porscheinformatik.sonarqube.licensecheck.gradle;

import at.porscheinformatik.sonarqube.licensecheck.configuration.JsonParserConfiguration;
import at.porscheinformatik.sonarqube.licensecheck.model.Dependency;
import at.porscheinformatik.sonarqube.licensecheck.service.JsonDependencyParser;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

class GradlePomResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(GradlePomResolver.class);

    private final File projectRoot;

    GradlePomResolver(File projectRoot) {
        this.projectRoot = projectRoot;
    }

    List<Dependency> resolveDependencies() throws IOException, GradleInvoker.GradleInvokerException {
        File targetDir = generateLicenseInformation();
        return new JsonDependencyParser(JsonParserConfiguration.burrowsLicenseCheckConfiguration()).scan(targetDir);
    }

    private File generateLicenseInformation() throws IOException, GradleInvoker.GradleInvokerException {
        GradleInvoker gradleInvoker = new GradleInvoker(projectRoot.getAbsolutePath());
        gradleInvoker.invoke("licenseReport", "-I", createInitScript());

        return projectRoot;
    }

    private String createInitScript() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("gradle/pom.gradle");
        File buildDir = new File(projectRoot, "build");
        File file = new File(buildDir, "pom.gradle");
        try {
            FileUtils.copyInputStreamToFile(inputStream, file);
            return file.getAbsolutePath();
        } catch (IOException e) {
            LOGGER.error("IOExceptions attempting to copy pom.gradle file.", e);
            return null;
        }
    }
}
