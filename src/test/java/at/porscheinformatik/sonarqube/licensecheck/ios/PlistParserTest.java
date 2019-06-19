package at.porscheinformatik.sonarqube.licensecheck.ios;

import at.porscheinformatik.sonarqube.licensecheck.model.Dependency;
import com.dd.plist.PropertyListFormatException;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.empty;

public class PlistParserTest {

    @Test
    public void shouldParsePlist() throws PropertyListFormatException, ParserConfigurationException, SAXException, ParseException, IOException {
        File file = new File(this.getClass().getClassLoader().getResource("ios/Pods-some-item-acknowledgements.plist").getFile());
        final List<Dependency> dependencies = PlistParser.scanFile(file).collect(Collectors.toList());
        Assert.assertThat(dependencies, not(empty()));
    }
}
