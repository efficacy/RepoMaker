package org.stringtree.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.stringtree.Context;
import org.stringtree.context.ConvertingContext;
import org.stringtree.context.MapContext;
import org.stringtree.converter.TemplateFileConverter;
import org.stringtree.solomon.EasySolomon;
import org.stringtree.solomon.Session;
import org.stringtree.solomon.Template;
import org.stringtree.util.FileWritingUtils;
import org.stringtree.util.LiteralMap;

public class RepoMaker {

	private File in;
	private File out;

	public RepoMaker(String in, String out) throws IOException {
		this.in = new File(in);
		this.out = new File(out);
		if (null == in || !this.in.exists()) {
			throw new IOException("cannot make a repo from missing dir [" + in + "]");
		}
	}

	public void make() throws IOException {
		crawl(in, out);
	}

	private void crawl(File indir, File outdir) throws IOException {
		if (!outdir.exists()) {
			outdir.mkdirs();
		}
		for (File file : indir.listFiles()) {
			String name = file.getName();
			if (isPackage(file)) {
				crawl(new File(indir, name), new File(outdir, name));
			} else if (isArtefact(file)){
				File destFile = new File(outdir, name);
				copy(file, destFile);
				createMD5Hash(destFile);
				createSHAHash(destFile);
				File pom = new File(outdir, name.replace(".jar", ".pom"));
				createPOM(file, pom);
				createMD5Hash(pom);
				createSHAHash(pom);
			} else {
				System.err.println("WARNING: ignored unknown file type [" + name + "]");
			}
		}
		createIndex(outdir);
	}

	private boolean isArtefact(File file) {
		return file.getName().endsWith(".jar");
	}

	private boolean isPackage(File file) {
		return file.isDirectory();
	}

	private void createPOM(File file, File pom) throws IOException {
		Context<Template> templates = new ConvertingContext<Template>(
				new TemplateFileConverter(new File("src/main/resources/templates"),
					new LiteralMap<String, Boolean>(".tpl", false)));
		Context<String> context = new MapContext<String>();
		EasySolomon solomon = new EasySolomon(templates, context);
		Session session = new Session();
		
		String path = extractPath(file);
		String name = extractName(file);
		String version = extractVersion(file);
		context.put("group", path);
		context.put("name", name);
		context.put("version", version);
		FileWritingUtils.writeFile(pom, solomon.toString("pom", session));
				pom.createNewFile();
	}

	private String extractVersion(File file) {
		return "1.0";
	}

	private String extractName(File file) {
		return "something";
	}

	private String extractPath(File file) {
		return "";
	}

	private void createMD5Hash(File destFile) throws IOException {
		new File(destFile.getParentFile(), destFile.getName() + ".md5").createNewFile();
	}

	private void createSHAHash(File destFile) throws IOException {
		new File(destFile.getParentFile(), destFile.getName() + ".sha1").createNewFile();
	}

	private void createIndex(File outdir) throws IOException {
		new File(outdir, "index.html").createNewFile();
	}

	public static void copy(File from, File destFile) throws IOException {
		if(!destFile.exists()) {
			destFile.createNewFile();
		}
		
		FileChannel source = null;
		FileChannel destination = null;
		try {
			source = new FileInputStream(from).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		}
		finally {
			if(source != null) {
			source.close();
		}
		if(destination != null) {
			destination.close();
			}
		}
	}
}
