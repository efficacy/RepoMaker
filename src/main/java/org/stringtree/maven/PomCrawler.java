package org.stringtree.maven;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.stringtree.util.StringUtils;
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

}
