package at.porscheinformatik.sonarqube.licensecheck.configuration;

import at.porscheinformatik.sonarqube.licensecheck.LicenseCheckPropertyKeys;
import org.sonar.api.config.Configuration;

public class JsonParserConfiguration {
    private String fileRegex;
    private String dependencyJsonPath;
    private String artifactJsonPath;
    private String licenseJsonPath;
    private String versionJsonPath;

    public String getFileRegex() {
        return fileRegex;
    }


    public String getDependencyJsonPath() {
        return dependencyJsonPath;
    }


    public String getArtifactJsonPath() {
        return artifactJsonPath;
    }


    public String getLicenseJsonPath() {
        return licenseJsonPath;
    }


    public String getVersionJsonPath() {
        return versionJsonPath;
    }


    public static JsonParserConfiguration burrowsLicenseCheckConfiguration() {
        JsonParserConfiguration config = new JsonParserConfiguration();
        config.fileRegex = ".*license.*Report.json";
        config.dependencyJsonPath = "$.[*]";
        config.artifactJsonPath = "$.dependency";
        config.licenseJsonPath = "$.licenses.[*].license";
        config.versionJsonPath = "$.version";
        return config;
    }

    public static JsonParserConfiguration parseConfiguration(Configuration configuration) {
        JsonParserConfiguration config = new JsonParserConfiguration();
        config.fileRegex = configuration.get(LicenseCheckPropertyKeys.JSON_PARSER_FILE_REGEX).orElse(".*" + configuration.get(LicenseCheckPropertyKeys.JSON_PARSER_INPUT_FILE).orElse("licenseReport.json"));
        config.dependencyJsonPath = configuration.get(LicenseCheckPropertyKeys.JSON_PARSER_DEPENDENCY_PATH).orElse("$.[*]");
        config.artifactJsonPath = configuration.get(LicenseCheckPropertyKeys.JSON_PARSER_ARTIFACT_PATH).orElse("$.dependency");
        config.licenseJsonPath = configuration.get(LicenseCheckPropertyKeys.JSON_PARSER_LICENSE_PATH).orElse("$.licenses.[*].license");
        config.versionJsonPath = configuration.get(LicenseCheckPropertyKeys.JSON_PARSER_VERSION_PATH).orElse("$.version");
        return config;
    }
}
