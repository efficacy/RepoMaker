package test;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.stringtree.maven.PomCrawler;
import org.stringtree.util.FileReadingUtils;
import org.stringtree.util.testing.Checklist;

public class PomCrawlerTest extends TestCase {
	PomCrawler crawler;
	
	public void setUp() {
		crawler = new PomCrawler();
	}
	
	public void testEmpty() throws IOException {
		crawl("empty.xml");
		assertTrue(crawler.getDependencies().isEmpty());
	}
	
	public void testNoDependencies() throws IOException {
		crawl("nodeps.xml");
		assertTrue(crawler.getDependencies().isEmpty());
	}
	
	public void testEmptyDependencies() throws IOException {
		crawl("emptydeps.xml");
		assertTrue(crawler.getDependencies().isEmpty());
	}
	
	public void testOneDep() throws IOException {
		crawl("onedep.xml");
		assertTrue(new Checklist<MavenArtefact>(
				new MavenArtefact("org.test", "single", "1.0")
			).check(crawler.getDependencies()));
	}
	
	public void testTwoDeps() throws IOException {
		crawl("twodeps.xml");
		assertTrue(new Checklist<MavenArtefact>(
				new MavenArtefact("org.test", "single", "1.0"),
				new MavenArtefact("com.lala", "huh", "9.9-SNAPSHOT")
			).check(crawler.getDependencies()));
	}

	public void crawl(String filename) throws IOException {
		File file = new File("src/test/poms/" + filename);
		crawler.crawl(FileReadingUtils.readRawFile(file));
	}
}
