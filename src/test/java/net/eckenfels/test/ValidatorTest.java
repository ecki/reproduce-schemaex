/* ASL 2.0 Bernd Eckenfels */
package net.eckenfels.test;

import static org.junit.Assert.assertNotNull;

import java.io.StringReader;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xml.sax.SAXException;

//@PowerMockIgnore({"javax.*"}) //fails 8u152,8u162
@PowerMockIgnore({"javax.*","org.xml.*"}) // works 8u152, fails 8u161
//@PowerMockIgnore({"javax.*","com.sun.org.apache.xerces.*"}) // works 8u152,8u162
//@PowerMockIgnore({}) //works 8u152,8u162
@RunWith(PowerMockRunner.class)
public class ValidatorTest
{
    static class MockMe
    {
        public void testSchema() throws SAXException {
            String anyXSD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><schema xmlns=\"http://www.w3.org/2001/XMLSchema\" targetNamespace=\"tns\"/>";
            SchemaFactory fac = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            Schema verifier = fac.newSchema(new StreamSource(new StringReader(anyXSD)));
            assertNotNull(verifier);
        }
    }

    @Test
    public void testSchema() throws SAXException {
        new MockMe().testSchema();
    }
}

