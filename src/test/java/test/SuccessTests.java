package test;

import java.io.File;
import java.io.IOException;

import org.stringtree.util.FileReadingUtils;

public class SuccessTests extends RepoMakerTestCase {

	public void testSingle() throws IOException {
		File dir = make("single");
		assertTrue(dir.exists() && dir.isDirectory());
		assertFileExists("index.html");
		assertFileExists("org/index.html");
		assertFileExists("org/stringtree/index.html");
		assertFileExists("org/stringtree/something/index.html");
		assertFileExists("org/stringtree/something/metadata.xml");
		assertFileExists("org/stringtree/something/metadata.xml.md5");
		assertFileExists("org/stringtree/something/metadata.xml.sha1");
		assertFileExists("org/stringtree/something/1.0/index.html");
		assertFileExists("org/stringtree/something/1.0/something-1.0.jar");
		assertFileExists("org/stringtree/something/1.0/something-1.0.jar.md5");
		assertFileExists("org/stringtree/something/1.0/something-1.0.jar.sha1");
		assertFileExists("org/stringtree/something/1.0/something-1.0.pom");
		assertFileExists("org/stringtree/something/1.0/something-1.0.pom.md5");
		assertFileExists("org/stringtree/something/1.0/something-1.0.pom.sha1");
	}

	public void testSingleWithSources() throws IOException {
		File dir = make("single-sources");
		assertTrue(dir.exists() && dir.isDirectory());
		assertFileExists("index.html");
		assertFileExists("org/index.html");
		assertFileExists("org/stringtree/index.html");
		assertFileExists("org/stringtree/something/index.html");
		assertFileExists("org/stringtree/something/metadata.xml");
		assertFileExists("org/stringtree/something/metadata.xml.md5");
		assertFileExists("org/stringtree/something/metadata.xml.sha1");
		assertFileExists("org/stringtree/something/1.0/index.html");
		assertFileExists("org/stringtree/something/1.0/something-1.0.jar");
		assertFileExists("org/stringtree/something/1.0/something-1.0.jar.md5");
		assertFileExists("org/stringtree/something/1.0/something-1.0.jar.sha1");
		assertFileExists("org/stringtree/something/1.0/something-1.0-sources.jar");
		assertFileExists("org/stringtree/something/1.0/something-1.0-sources.jar.md5");
		assertFileExists("org/stringtree/something/1.0/something-1.0-sources.jar.sha1");
		assertFileExists("org/stringtree/something/1.0/something-1.0.pom");
		assertFileExists("org/stringtree/something/1.0/something-1.0.pom.md5");
		assertFileExists("org/stringtree/something/1.0/something-1.0.pom.sha1");
	}

	public void testMultipleVersions() throws IOException {
		File dir = make("multiple-versions");
		assertTrue(dir.exists() && dir.isDirectory());
		assertFileExists("index.html");
		assertFileExists("org/index.html");
		assertFileExists("org/stringtree/index.html");
		assertFileExists("org/stringtree/something/index.html");
		assertFileExists("org/stringtree/something/metadata.xml");
		assertFileExists("org/stringtree/something/metadata.xml.md5");
		assertFileExists("org/stringtree/something/metadata.xml.sha1");
		assertFileExists("org/stringtree/something/1.0/index.html");
		assertFileExists("org/stringtree/something/1.0/something-1.0.jar");
		assertFileExists("org/stringtree/something/1.0/something-1.0.jar.md5");
		assertFileExists("org/stringtree/something/1.0/something-1.0.jar.sha1");
		assertFileExists("org/stringtree/something/1.0/something-1.0.pom");
		assertFileExists("org/stringtree/something/1.0/something-1.0.pom.md5");
		assertFileExists("org/stringtree/something/1.0/something-1.0.pom.sha1");
		assertFileExists("org/stringtree/something/1.1/index.html");
		assertFileExists("org/stringtree/something/1.1/something-1.1.jar");
		assertFileExists("org/stringtree/something/1.1/something-1.1.jar.md5");
		assertFileExists("org/stringtree/something/1.1/something-1.1.jar.sha1");
		assertFileExists("org/stringtree/something/1.1/something-1.1.pom");
		assertFileExists("org/stringtree/something/1.1/something-1.1.pom.md5");
		assertFileExists("org/stringtree/something/1.1/something-1.1.pom.sha1");

		File metadataFile = new File(dir, "org/stringtree/something/metadata.xml");
		String metadata = FileReadingUtils.readFile(metadataFile);
		assertTrue(metadata.contains("<version>1.0</version>"));
		assertTrue(metadata.contains("<version>1.1</version>"));

	}

}
