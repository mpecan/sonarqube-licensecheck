package at.porscheinformatik.sonarqube.licensecheck.golang;

import at.porscheinformatik.sonarqube.licensecheck.model.Dependency;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class WwhdParserTest {


    @Test
    public void shouldConvertLineToDependency() {
        final String expectedName = "github.com/dimfeld/httptreemux";
        final String expectedLicense = "MIT";
        String input = "time=\"2019-02-21T08:39:16Z\" level=info msg=\"Found License\" license=" + expectedLicense + " package=\"" + expectedName + "\"";
        Dependency result = WwhdParser.lineToDepenency(input);
        assertThat(result.getName(), is(expectedName));
        assertThat(result.getLicense(), is(expectedLicense));
    }

    @Test
    public void shouldConvertLineWithoutLicenseToDependency() {
        final String expectedName = "github.com/xeipuuv/gojsonreference";
        final String expectedLicense = null;
        String input = "time=\"2019-02-21T08:39:16Z\" level=warning msg=\"Did not find recognized license!\" package=\"" + expectedName + "\"";
        Dependency result = WwhdParser.lineToDepenency(input);
        assertThat(result.getName(), is(expectedName));
        assertThat(result.getLicense(), is(expectedLicense));
    }

}
