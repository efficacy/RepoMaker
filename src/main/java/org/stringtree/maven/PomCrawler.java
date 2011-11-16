package org.stringtree.maven;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.stringtree.util.FileWritingUtils;
import org.stringtree.util.StringUtils;
import org.stringtree.util.URLReadingUtils;
import org.stringtree.xml.PartialMapXMLEventHandler;
import org.stringtree.xml.StanzaMatcher;
import org.stringtree.xml.XMLEventHandler;
import org.stringtree.xml.XMLEventParser;

import test.MavenArtefact;

public class PomCrawler {
	private Collection<String> repos;
	private Collection<MavenArtefact> dependencies;
	
	XMLEventParser parser;
	XMLEventHandler handler;

	public PomCrawler(String... repos) {
		this.repos = new HashSet<String>();
		this.repos.addAll(Arrays.asList(repos));
		this.dependencies = new HashSet<MavenArtefact>();

		this.parser = new XMLEventParser(true, true);
		Map<String, StanzaMatcher>matchers = new HashMap<String, StanzaMatcher>();
		matchers.put("/project/dependencies/dependency", new StanzaMatcher() {
			public Object match(String path, Map<?,?> values, Object context) {
				dependencies.add(new MavenArtefact(
						StringUtils.nullToEmpty(values.get("/project/dependencies/dependency/groupId")), 
						StringUtils.nullToEmpty(values.get("/project/dependencies/dependency/artifactId")), 
						StringUtils.nullToEmpty(values.get("/project/dependencies/dependency/version"))
					));
				return context;
			}});
		handler = new PartialMapXMLEventHandler(matchers, false);
	}

	public void crawl(String pom) throws IOException {
		if (null == pom) throw new IOException("cannot process null POM");
		parser.process(new StringReader(pom), handler);
	}
	
	public void addRepo(String repo) {
		repos.add(repo);
	}

	public Collection<MavenArtefact> getDependencies() {
		return dependencies;
	}
	
	public void fetchDependency(MavenArtefact artefact, File destination) {
		byte[] value = null;
		for (String repo : repos) {
			try {
				URL base = URLReadingUtils.findURL(repo);
				String path = artefact.getGroupId().replace(".", "/") + "/" +
					artefact.getArtefactId() + "/" +
					artefact.getVersion() + "/" + 
					artefact.getArtefactId() + "-" + artefact.getVersion() + ".jar";
				URL dep = new URL(base, path);
				value = URLReadingUtils.readRawURLBytes(dep);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// ok to ignore this, just move on and try the next repo
			}
			if (null != value) {
				try {
					FileWritingUtils.writeFile(new File(destination, artefact.getArtefactId() + "-" + artefact.getVersion() + ".jar"), value);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void fetchAllDependencies(File destination) {
		for (MavenArtefact artefact : dependencies) {
			fetchDependency(artefact, destination);
		}
	}

}
