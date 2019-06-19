package at.porscheinformatik.sonarqube.licensecheck.service;

import at.porscheinformatik.sonarqube.licensecheck.configuration.JsonParserConfiguration;
import at.porscheinformatik.sonarqube.licensecheck.interfaces.Scanner;
import at.porscheinformatik.sonarqube.licensecheck.model.Dependency;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class JsonDependencyParser implements Scanner {

    public static final Pattern VERSION_MATCHING_PATTERN = Pattern.compile(".*:(\\d+(\\.\\d+)+.*)");
    private final JsonParserConfiguration configuration;

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonDependencyParser.class);

    public JsonDependencyParser(Configuration configuration) {
        this.configuration = JsonParserConfiguration.parseConfiguration(configuration);
    }

    public JsonDependencyParser(JsonParserConfiguration jsonParserConfiguration) {
        this.configuration = jsonParserConfiguration;
    }


    @Override
    public List<Dependency> scan(File moduleDir) {
        try (final Stream<Path> walk = Files.walk(moduleDir.toPath())) {
            final List<Dependency> foundDependencies = new ArrayList<>();
            walk.filter(it -> it.toString().matches(configuration.getFileRegex())).map(Path::toFile)
                .forEach(processDependencies(foundDependencies));
            return foundDependencies;
        } catch (IOException e) {
            LOGGER.error("Could not read dependencies from {}", configuration.getFileRegex());
        }
        return Collections.emptyList();
    }

    private Consumer<File> processDependencies(List<Dependency> foundDependencies) {
        return (file) -> {
            if (file.canRead()) {
                final DocumentContext licensesDocument;
                try {
                    licensesDocument = JsonPath.parse(file);
                    int count = ((JSONArray) licensesDocument.read(configuration.getDependencyJsonPath())).size();

                    LOGGER.info("Reading {} records", count);
                    final String[] dependencyPathParts = configuration.getDependencyJsonPath().split("\\*");
                    IntStream.range(0, count).boxed()
                        .map(index -> dependencyPathParts[0] + index.toString() + dependencyPathParts[1])
                        .map(dependencyPath -> extractDependency(configuration.getArtifactJsonPath(), configuration.getLicenseJsonPath(), configuration.getVersionJsonPath(), licensesDocument, dependencyPath))
                        .filter(Objects::nonNull).forEach(foundDependencies::add);
                } catch (IOException e) {
                    LOGGER.error("Could not read dependencies from {}", file);
                    LOGGER.debug("Error whilist reading dependencies: ", e);
                }
            }
        };
    }

    private Dependency extractDependency(String artifactJsonPath, String licenseJsonPath, String versionJsonPath, DocumentContext licensesDocument, String dependencyPath) {
        String artifact = licensesDocument.read(dependencyPath + artifactJsonPath.substring(1));
        String version = licensesDocument.read(dependencyPath + versionJsonPath.substring(1));
        if (artifact != null) {
            final Dependency dependency = new Dependency();
            if (version == null) {
                final Matcher matcher = VERSION_MATCHING_PATTERN.matcher(artifact);
                if (matcher.matches()) {
                    version = matcher.group(1);
                }
            }
            if (version != null && artifact.contains(version)) {
                artifact = artifact.replaceAll("[:.]?" + version, "");
            }
            dependency.setName(artifact);
            dependency.setVersion(version);
            Set<String> licenses = getLicenses(dependencyPath + licenseJsonPath.substring(1), licensesDocument);
            if (licenses != null) {
                dependency.setLicenses(licenses);
            }
            return dependency;
        }
        return null;
    }

    private Set<String> getLicenses(String path, DocumentContext licensesDocument) {
        final Object read = licensesDocument.read(path);
        if (read instanceof JSONArray) {
            return ((JSONArray) read).stream().map(Object::toString).collect(Collectors.toSet());
        } else if (read instanceof JSONObject) {
            final JSONObject jsonObject = (JSONObject) read;
            return Collections.singleton(jsonObject.toString());
        } else if (read instanceof String) {
            return Collections.singleton((String) read);
        }
        LOGGER.info("Could not retrieve licenses from JSONPath {}", path);
        return Collections.emptySet();
    }

}
