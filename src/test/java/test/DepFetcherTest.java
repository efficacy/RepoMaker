package test;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.stringtree.maven.PomCrawler;

public class DepFetcherTest extends TestCase {
	private static final File DEST = new File("src/test/dest");
	PomCrawler crawler;
	
	public void setUp() {
		crawler = new PomCrawler();
		DEST.mkdirs();
		clean(DEST);
	}
	
	private void clean(File file) {
		if (null == file) return;
		if (file.isFile()) {
			file.delete();
		} else {
			for (File child : file.listFiles()) {
				clean(child);
			}
		}
	}

	public void testNoRepo() throws IOException {
		fetch(new MavenArtefact("org.test", "single", "1.0"));
		assertEquals(0, DEST.list().length);
	}
	
	public void testRepoWithoutArtefact() throws IOException {
		crawler.addRepo("file:src/test/repo1/");
		fetch(new MavenArtefact("org.test", "single", "1.0"));
		assertEquals(0, DEST.list().length);
	}
	
	public void testRepoWithArtefact() throws IOException {
		crawler.addRepo("file:src/test/repo2/");
		fetch(new MavenArtefact("org.test", "single", "1.0"));
		assertEquals(1, DEST.list().length);
	}
	
	public void testRepoFallback() throws IOException {
		crawler.addRepo("file:src/test/repo1/");
		crawler.addRepo("file:src/test/repo2/");
		fetch(new MavenArtefact("org.test", "single", "1.0"));
		assertEquals(1, DEST.list().length);
	}
	
	private void fetch(MavenArtefact mavenArtefact) {
		crawler.fetchDependency(mavenArtefact, DEST);
	}
}
