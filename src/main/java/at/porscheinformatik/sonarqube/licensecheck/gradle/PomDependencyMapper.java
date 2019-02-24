package at.porscheinformatik.sonarqube.licensecheck.gradle;

import at.porscheinformatik.sonarqube.licensecheck.model.Dependency;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.License;
import org.apache.maven.model.Model;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class PomDependencyMapper {


    Dependency toDependency(Model model) {
        inheritGroupOrVersionFromParent(model);

        if (StringUtils.isBlank(model.getGroupId())
            || StringUtils.isBlank(model.getArtifactId())
            || StringUtils.isBlank(model.getVersion())) {
            return null;
        }

        return new Dependency(
            model.getGroupId() + ":" + model.getArtifactId(),
            model.getVersion(),
            selectLicenses(model));


    }

    private List<String> selectLicenses(Model model) {
        List<License> licenses = model.getLicenses().stream()
            .filter(licenseNameIsNotBlank())
            .collect(Collectors.toList());


        return licenses.stream().map(License::getName).collect(Collectors.toList());
    }

    private void inheritGroupOrVersionFromParent(Model pom) {
        if (StringUtils.isBlank(pom.getGroupId()) && StringUtils.isNotBlank(pom.getParent().getGroupId())) {
            pom.setGroupId(pom.getParent().getGroupId());
        }
        if (StringUtils.isBlank(pom.getVersion()) && StringUtils.isNotBlank(pom.getParent().getVersion())) {
            pom.setVersion(pom.getParent().getVersion());
        }
    }

    private Predicate<License> licenseNameIsNotBlank() {
        return license -> StringUtils.isNotBlank(license.getName());
    }
}
