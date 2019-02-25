package at.porscheinformatik.sonarqube.licensecheck.gradle;

import at.porscheinformatik.sonarqube.licensecheck.LicenseCheckPropertyKeys;
import at.porscheinformatik.sonarqube.licensecheck.interfaces.Scanner;
import at.porscheinformatik.sonarqube.licensecheck.model.Dependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Configuration;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class GradleDependencyScanner implements Scanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(GradleDependencyScanner.class);
    private final boolean disabled;

    private File projectRoot;

    public GradleDependencyScanner(Configuration configuration) {
        this.disabled = configuration.getBoolean(LicenseCheckPropertyKeys.GRADLE_DISABLED).orElse(false);
    }

    @Override
    public List<Dependency> scan(File moduleDir) {
        this.projectRoot = moduleDir;

        if (disabled) {
            LOGGER.info("Gradle scanning is disabled. Unset \"licensecheck.gradle.disabled\" to enable.");
            return Collections.emptyList();
        }
        try {
            return resolveDependenciesWithLicenses();
        } catch (Exception e) {
            LOGGER.error("Could not retrieve dependencies for module " + moduleDir, e);
            return Collections.emptyList();
        }
    }

    private List<Dependency> resolveDependenciesWithLicenses() throws Exception {
        GradlePomResolver gradlePomResolver = new GradlePomResolver(projectRoot);

        return gradlePomResolver.resolveDependencies();
    }


}
