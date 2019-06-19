package at.porscheinformatik.sonarqube.licensecheck.gradle;

import org.apache.maven.model.License;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;

import java.util.Arrays;
import java.util.List;

public class TestDataBuilder {

    public static Model pom(String artifactId, String groupId, String version, List<License> licenses) {
        return pom(artifactId, groupId, version, null, licenses);
    }

    public static Model pom(String artifactId, String groupId, String version, Parent parent, List<License> licenses) {
        Model model = new Model();
        model.setArtifactId(artifactId);
        model.setGroupId(groupId);
        model.setVersion(version);
        model.setLicenses(licenses);
        model.setParent(parent);
        return model;
    }

    public static Parent parent(String groupId, String version) {
        Parent parent = new Parent();
        parent.setGroupId(groupId);
        parent.setVersion(version);
        return parent;
    }

    public static List<License> licenses(License... licenses) {
        return Arrays.asList(licenses);
    }

    public static License license(String name) {
        License license = new License();
        license.setName(name);
        return license;
    }
}
