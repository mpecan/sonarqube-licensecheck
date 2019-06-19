package at.porscheinformatik.sonarqube.licensecheck;

public class LicenseCheckPropertyKeys {

    private LicenseCheckPropertyKeys() {}


    public static final String NAME_MATCHES = "nameMatches";
    public static final String LICENSE = "license";
    public static final String NAME = "name";

    public static final String LICENSE_WHITELIST_KEY = "licensecheck.licenses_whitelist";

    public static final String LICENSE_BLACKLIST_KEY = "licensecheck.licenses_blacklist";

    public static final String LICENSE_BLACKLIST_DEFAULT_KEY = "licensecheck.licenses_blacklist_default";

    public static final String LICENSE_REGEX = "licensecheck.licensesregex";

    public static final String MAVEN_REGEX = "licensecheck.mavenregex";

    public static final String INTERNAL_REGEX = "licensecheck.internalregex";

    public static final String ACTIVATION_KEY = "licensecheck.activation";
    public static final String FORBID_UNKNOWN = "licensecheck.forbid_unknown";

    public static final String GRADLE_DISABLED = "licensecheck.gradle.disabled";

    public static final String JSON_PARSER_FILE_REGEX = "licensecheck.parse.json.input_file_regex";
    public static final String JSON_PARSER_INPUT_FILE = "licensecheck.parse.json.input_file";
    public static final String JSON_PARSER_DEPENDENCY_PATH = "licensecheck.parse.json.dependency_path";
    public static final String JSON_PARSER_ARTIFACT_PATH = "licensecheck.parse.json.artifact_path";
    public static final String JSON_PARSER_LICENSE_PATH = "licensecheck.parse.json.license_path";
    public static final String JSON_PARSER_VERSION_PATH = "licensecheck.parse.json.version_path";
}
