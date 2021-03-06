package de.ohmesoftware.javadoctoopenapischema;

import de.ohmesoftware.javadoctoopenapischema.model.subdir.Foo;
import de.ohmesoftware.javadoctoopenapischema.model.subdir.User;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.util.Collections;

import static junit.framework.TestCase.*;

/**
 * Test.
 *
 * @author <a href="mailto:k_o_@users.sourceforge.net">Karsten Ohme
 * (k_o_@users.sourceforge.net)</a>
 */
public class TestEnricher {

    private static String buildPath(String classOrPackageName) {
        return "src/test/java/"+ classOrPackageName.replace(".", "/");
    }

    @After
    public void after() throws Exception {
        FileUtils.copyFile(new File(buildPath(User.class.getName())+".bak"),
                new File(buildPath(User.class.getName())+".java"));
        FileUtils.copyFile(new File(buildPath(Foo.class.getName())+".bak"),
                new File(buildPath(Foo.class.getName())+".java"));
    }

    @Test
    public void enrich() throws Exception {
        Enricher enricher = new Enricher(buildPath(User.class.getPackage().getName().substring(0,
                User.class.getPackage().getName().lastIndexOf("."))),
                Collections.singleton("**User.java"), Collections.singleton("**.bak"), false);
        enricher.enrich();
        String newContent = IOUtils.toString(new FileReader(new File(buildPath(User.class.getName())+".java")));
        assertTrue(newContent.contains("The maximum value is 100000.\\n * The minimum value is 0."));
        assertTrue(newContent.contains("package de.ohmesoftware.javadoctoopenapischema.model.subdir;"));
        assertTrue(newContent.contains("@io.swagger.v3.oas.annotations.media.Schema(title = \"No summary.\", minLength = 36, maxLength = 64, description = \"No summary. No description.\\n * The minimum length is 36.\\n * The maximum length is 64.\""));
        assertTrue(newContent.contains("@io.swagger.v3.oas.annotations.media.Schema(title = \"The username.\", required = true, minLength = 1, description = \"The username.\\n * This value is required.\\n * The minimum length is 1.\")"));
        assertTrue(newContent.contains(" @io.swagger.v3.oas.annotations.media.Schema(title = \"No summary.\", minLength = 2, maxLength = 64, description = \"No summary. No description.\\n * The minimum length is 2.\\n * The maximum length is 64.\")"));
        assertTrue(newContent.contains("@io.swagger.v3.oas.annotations.media.Schema(title = \"No summary.\", required = true, description = \"No summary. No description.\\n * This value is required.\")"));
        assertTrue(newContent.contains("@io.swagger.v3.oas.annotations.media.Schema(description = \"The email address. Escape \\\"test\\\"\", title = \"The email address.\")"));
        assertTrue(newContent.contains("@io.swagger.v3.oas.annotations.media.Schema(title = \"No summary.\", required = true, minLength = 1, maxLength = 2048, description = \"No summary. No description.\\n * This value is required.\\n * The minimum length is 1.\\n * The maximum length is 2048.\")"));
        assertTrue(newContent.contains("@io.swagger.v3.oas.annotations.media.Schema(title = \"No summary.\", maximum = \"10\", minimum = \"0\", description = \"No summary. No description.\\n * The maximum value is 10.\\n * The minimum value is 0.\")"));
        assertTrue(newContent.contains("@io.swagger.v3.oas.annotations.media.Schema(title = \"All bar.\", description = \"All bar. More detailed description of the list of bars.\")"));
        assertTrue(newContent.contains(" @io.swagger.v3.oas.annotations.media.Schema(title = \"A nice foobar.\", description = \"A nice foobar. More detailed description of the foobar.\")"));
        assertFalse(newContent.contains("@io.swagger.v3.oas.annotations.media.Schema()"));
    }

    @Test
    public void enrichHateaos() throws Exception {
        Enricher enricher = new Enricher(buildPath(Foo.class.getPackage().getName().substring(0,
                Foo.class.getPackage().getName().lastIndexOf("."))),
                Collections.singleton("**Foo.java"), Collections.singleton("**.bak"), true);
        enricher.enrich();
        String newContent = IOUtils.toString(new FileReader(new File(buildPath(Foo.class.getName())+".java")));
        assertTrue(newContent.contains("package de.ohmesoftware.javadoctoopenapischema.model.subdir;"));
        assertTrue(newContent.contains("description = \"For the resource creation with `POST` this attribute is an URI to the associated resource. For a `GET` operation on the item or collection resource this attribute of the same name is included in the `_links` section as `\\\"_links\\\": { \\\"bar\\\": { \\\"href\\\": \\\"Resource URI\\\"} } `. The associated resource can be updated with a `PUT` call with `Content-Type: text/uri-list` and the single URI to the updated associated resource.\""));
        assertTrue(newContent.contains("description = \"For the resource creation with `POST` this attribute is an array of URIs to the associated resources. For a `GET` operation on the item or collection resource this attribute of the same name is included in the `_links` section as `\\\"_links\\\": { \\\"bars\\\": { \\\"href\\\": \\\"Resource URI\\\"} } ` section containing the URI to the associated collection of resources. The associated resources can be updated with a `PUT` call with `Content-Type: text/uri-list` and a list with URIs to the updated associated resources.\""));
        assertTrue(newContent.contains("title = \"URI to the resource association: A Bar object.\""));
        assertTrue(newContent.contains("title = \"URIs to the resource associations: Multiple bars.\""));
        assertFalse(newContent.contains("title = \"URI to the resource: A lot of data."));
    }

}
