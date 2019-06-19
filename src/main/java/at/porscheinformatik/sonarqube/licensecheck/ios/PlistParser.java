package at.porscheinformatik.sonarqube.licensecheck.ios;

import at.porscheinformatik.sonarqube.licensecheck.interfaces.Scanner;
import at.porscheinformatik.sonarqube.licensecheck.model.Dependency;
import com.dd.plist.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlistParser implements Scanner {


    private static final Logger LOGGER = LoggerFactory.getLogger(PlistParser.class);
    public static final String PREFERENCE_SPECIFIERS = "PreferenceSpecifiers";
    public static final String LICENSE = "License";
    public static final String TITLE = "Title";
    public static final String ACKNOWLEDGEMENTS = "Acknowledgements";
    public static final String PODS_ACKNOWLEDGEMENTS_PLIST = ".*/Pods-.*-acknowledgements.plist";

    @Override
    public List<Dependency> scan(File moduleDir) {
        try (final Stream<Path> walk = Files.walk(moduleDir.toPath())) {
            return walk
                .filter(path -> path.toString().matches(PODS_ACKNOWLEDGEMENTS_PLIST))
                .map(Path::toFile)
                .filter(File::canRead)
                .flatMap(PlistParser::scanFile).collect(Collectors.toList());
        } catch (IOException e) {
            LOGGER.error("Error walking path " + moduleDir, e);
        }
        return Collections.emptyList();
    }

    static Stream<Dependency> scanFile(File file) {
        try {
            final NSObject document = PropertyListParser.parse(file);
            if (document instanceof NSDictionary) {
                final NSDictionary parentDict = (NSDictionary) document;
                final NSObject preferenceSpecifiers = parentDict.get(PREFERENCE_SPECIFIERS);
                if (preferenceSpecifiers instanceof NSArray) {
                    final NSArray specifiers = (NSArray) preferenceSpecifiers;
                    return Arrays.stream(specifiers.getArray()).map(it -> {
                        if (it instanceof NSDictionary) {
                            return (NSDictionary) it;
                        }
                        return null;
                    }).filter(Objects::nonNull).map(it ->
                        {
                            final Optional<String> license = Optional.ofNullable(it.get(LICENSE)).map(item -> item.toJavaObject(String.class));
                            final Optional<String> title = Optional.ofNullable(it.get(TITLE)).map(item -> item.toJavaObject(String.class));
                            if (title.isPresent() && StringUtils.isNotBlank(title.get()) && !ACKNOWLEDGEMENTS.equals(title.get())) {
                                return new Dependency(title.get(), null, license.orElse(null));
                            } else return null;
                        }
                    ).filter(Objects::nonNull);
                }
            }
        } catch (IOException | PropertyListFormatException | ParseException | ParserConfigurationException | SAXException e) {
            LOGGER.error("Could not parse file " + file, e);
        }

        return Stream.empty();
    }
}
