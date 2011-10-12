package org.stringtree.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.stringtree.Context;
import org.stringtree.context.ConvertingContext;
import org.stringtree.converter.TemplateFileConverter;
import org.stringtree.solomon.EasySolomon;
import org.stringtree.solomon.Session;
import org.stringtree.solomon.Template;
import org.stringtree.util.FileReadingUtils;
import org.stringtree.util.FileWritingUtils;
import org.stringtree.util.LiteralMap;

public class RepoMaker {

	private File in;
	private File out;
	private Context<Template> templates;
	private DateFormat df;

	public RepoMaker(File in, File out) throws IOException {
		this.in = in;
		this.out = out;
		if (null == in || !this.in.exists()) {
			throw new IOException("cannot make a repo from missing dir [" + in + "]");
		}
		templates = new ConvertingContext<Template>(
			new TemplateFileConverter(new File("src/main/resources/templates"),
				new LiteralMap<String, Boolean>(".tpl", false)));
		df = new SimpleDateFormat("yyyyMMddHHmmssSS");
	}

	public RepoMaker(String in, String out) throws IOException {
		this(new File(in), new File(out));
	}

	public void make() throws IOException {
		Stack<String> path = new Stack<String>();
		crawl(in, out, path, null, null);
	}

	private void crawl(File indir, File outdir, 
			Stack<String> path, String id, String version) throws IOException {
		if (!outdir.exists()) {
			outdir.mkdirs();
		}
		for (File file : indir.listFiles()) {
			String name = file.getName();
			File destFile = new File(outdir, name);
			if (isNameDir(file)) {
				crawl(new File(indir, name), destFile, path, name, version);
				File metadata = createMetadata(destFile, path);
				createMD5Hash(metadata);
				createSHAHash(metadata);
			} else if (isVersionDir(file)) {
				crawl(new File(indir, name), destFile, path, id, file.getName());
			} else if (isGroupDir(file)) {
				path.push(name);
				crawl(new File(indir, name), destFile, path, id, version);
				path.pop();
			} else if (isArtefact(file)){
				copy(file, destFile);
				createMD5Hash(destFile);
				createSHAHash(destFile);
				if (isPrimaryArtefact(file)) {
					File pom = new File(outdir, name.replace(".jar", ".pom"));
					createPOM(file, pom, path, id, version);
					createMD5Hash(pom);
					createSHAHash(pom);
				}
			} else {
				System.err.println("WARNING: ignored unknown file type [" + name + "]");
			}
		}
		createIndex(outdir);
	}

	private boolean isVersionDir(File file) {
		return file.getName().matches("[\\d.]+");
	}

	private boolean isNameDir(File file) {
		if (!file.isDirectory()) return false;
		for (String name : file.list()) {
			if (name.matches("[\\d.]+")) return true;
		}
		return false;
	}

	private boolean isPrimaryArtefact(File file) {
		String name = file.getName();
		return name.matches(".*-[\\d.]+\\.jar");
	}

	private boolean isArtefact(File file) {
		return file.getName().endsWith(".jar");
	}

	private boolean isGroupDir(File file) {
		return file.isDirectory();
	}

	private File createMetadata(File dir, Stack<String> path) throws IOException {
		EasySolomon solomon = new EasySolomon(templates);
		Session session = new Session();
		
		String name = dir.getName();
		Collection<String> versions = findChildVersions(dir);
		String latest = maxVersion(versions);
		solomon.put("group", path);
		solomon.put("name", name);
		solomon.put("latest", latest);
		solomon.put("versions", versions);
		solomon.put("stamp", df.format(new Date()));
		File ret = new File(dir,"metadata.xml");
		FileWritingUtils.writeFile(ret, solomon.toString("metadata", session));
		return ret;
	}

	private String maxVersion(Collection<String> versions) {
		String sofar = "0";
		for (String version : versions) {
			if (version.compareTo(sofar) > 0) {
				sofar = version;
			}
		}
		
		return sofar;
	}

	private Collection<String> findChildVersions(File file) {
		Collection<String> ret = new ArrayList<String>();
		for (String name : file.list()) {
			if (name.matches("[\\d.]+")) {
				ret.add(name);
			}
		}

		return ret;
	}

	private void createPOM(File file, File pom, Stack<String> path, String id, String version) throws IOException {
		EasySolomon solomon = new EasySolomon(templates);
		Session session = new Session();
		
		solomon.put("group", path);
		solomon.put("name", id);
		solomon.put("version", version);
		FileWritingUtils.writeFile(pom, solomon.toString("pom", session));
	}

	private void createHash(File destFile, String alogorithm, String extension) throws IOException {
		byte[] bytesOfMessage = FileReadingUtils.readFileBytes(destFile);

		MessageDigest md;
		try {
			md = MessageDigest.getInstance(alogorithm);
			byte[] digest = md.digest(bytesOfMessage);
			FileWritingUtils.writeFile(new File(destFile.getParentFile(), destFile.getName() + "." + extension), digest);
		} catch (NoSuchAlgorithmException e) {
			throw new IOException("could not generate Hash using algorithm [" + alogorithm + "]", e);
		}
	}

	private void createMD5Hash(File destFile) throws IOException {
		createHash(destFile, "MD5", "md5");
	}

	private void createSHAHash(File destFile) throws IOException {
		createHash(destFile, "SHA1", "sha1");
	}

	private void createIndex(File outdir) throws IOException {
		Collection<Map<String, Object>> children = new ArrayList<Map<String,Object>>();
		for (File file : outdir.listFiles()) {
			Map<String, Object> child = new HashMap<String, Object>();
			String name = file.getName();
			child.put("name", name);
			child.put("url", name);
			child.put("date", file.lastModified());
			child.put("size", file.length());
		}
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
