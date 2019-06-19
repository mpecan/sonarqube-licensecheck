package at.porscheinformatik.sonarqube.licensecheck.golang;

import at.porscheinformatik.sonarqube.licensecheck.interfaces.Scanner;
import at.porscheinformatik.sonarqube.licensecheck.model.Dependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WwhdParser implements Scanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(WwhdParser.class);

    private static final Pattern PACKAGE_PATTERN = Pattern.compile(".*package=\"([^\"]+).*");
    private static final Pattern LICENSE_PATTERN = Pattern.compile(".*license=(\\S+).*");

    @Override
    public List<Dependency> scan(File moduleDir) {

        try (final Stream<Path> walk = Files.walk(moduleDir.toPath())) {
            return walk.filter(it -> it.toString().matches(".*go-licenses\\.txt"))
                .flatMap(this::parseFile)
                .map(WwhdParser::lineToDepenency)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        } catch (IOException e) {
            LOGGER.error("Error waling the path", e);
        }

        return Collections.emptyList();
    }

    private Stream<String> parseFile(Path path) {
        try {
            final List<String> lines = Files.readAllLines(path);
            return lines.stream();
        } catch (IOException e) {
            LOGGER.error("Could not read all lines from file ", e);
        }
        return Stream.empty();
    }


    static Dependency lineToDepenency(String s) {
        Matcher packageMatcher = PACKAGE_PATTERN.matcher(s);
        if (packageMatcher.matches()) {
            Dependency dependency = new Dependency();
            dependency.setName(packageMatcher.group(1));
            Matcher licenseMatcher = LICENSE_PATTERN.matcher(s);
            if (licenseMatcher.matches()) {
                dependency.setLicense(licenseMatcher.group(1));
            }
            return dependency;
        }

        return null;
    }
}
