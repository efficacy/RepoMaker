package org.stringtree.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
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
import org.stringtree.converter.TemplateResourceConverter;
import org.stringtree.solomon.EasySolomon;
import org.stringtree.solomon.Session;
import org.stringtree.solomon.Template;
import org.stringtree.util.FileReadingUtils;
import org.stringtree.util.FileWritingUtils;

public class RepoMaker {

	private File in;
	private File out;
	private Context<Template> templates;

	private DateFormat mavenTimestampFormat = new SimpleDateFormat("yyyyMMddHHmmssSS");
	private DateFormat modifiedFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	public RepoMaker(File in, File out) throws IOException {
		this.in = in;
		this.out = out;
		if (null == in || !this.in.exists()) {
			throw new IOException("cannot make a repo from missing dir [" + in + "]");
		}
		if (null == out || !out.exists()) {
			throw new IOException("cannot make a repo to missing dir [" + out + "]");
		}
		TemplateResourceConverter converter = new TemplateResourceConverter("templates/");
		templates = new ConvertingContext<Template>(converter);
	}

	public RepoMaker(String in, String out) throws IOException {
		this(new File(in), new File(out));
	}

	public void make() throws IOException {
		Stack<String> path = new Stack<String>();
		crawl(in, out, path, null, null);
		createIndex(out);
	}

	private void crawl(File indir, File outdir, Stack<String> path, String id, String version) throws IOException {
		if (!outdir.exists()) {
			outdir.mkdirs();
		}
		if (!indir.isDirectory()) {
			return;
		}
		
		for (File file : indir.listFiles(new FilenameFilter() {
			@Override public boolean accept(File dir, String name) {
				return !name.startsWith(".");
			}
		})) {
			String name = file.getName();
			File destFile = new File(outdir, name);
			if (isNameDir(file)) {
				crawl(new File(indir, name), destFile, path, name, version);
				File metadata = createMetadata(destFile, path);
				createMD5Hash(metadata);
				createSHAHash(metadata);
				createIndex(destFile);
			} else if (isVersionDir(file)) {
				crawl(new File(indir, name), destFile, path, id, file.getName());
				createIndex(destFile);
			} else if (isGroupDir(file)) {
				path.push(name);
				crawl(new File(indir, name), destFile, path, id, version);
				path.pop();
				createIndex(destFile);
			} else if (isFlatArtefact(file)){
				destFile = buildStructure(outdir, file);
				copy(file, destFile);
				derive(path, id, version, destFile);
			} else if (isArtefact(file)){
				copy(file, destFile);
				derive(path, id, version, destFile);
			} else {
				System.err.println("WARNING: ignored unknown file type [" + name + "]");
			}
		}
	}

	private File buildStructure(File dir, File file) throws IOException {
		Stack<String> path = new Stack<String>();
		String[] parts = file.getName().split("\\.");
		StringBuilder tail = new StringBuilder();
		for (String part : parts) {
			boolean isComposite = part.contains("-");
			boolean hasTail = tail.length() > 0;
			if (hasTail || isComposite) {
				if (hasTail) tail.append('.');
				tail.append(part);
				continue;
			}
			dir = new File(dir, part);
			dir.mkdirs();
			createIndex(dir);
			path.push(part);
		}

		String filename = tail.toString();
		
		int dash = filename.indexOf('-');
		String name = filename.substring(0, dash);
		dir = new File(dir, name);
		dir.mkdirs();
		File metadata = createMetadata(dir, path);
		createMD5Hash(metadata);
		createSHAHash(metadata);
		createIndex(dir);

		String version = filename.substring(dash+1);
		version = version.substring(0, version.lastIndexOf('.'));
		if (version.contains("-")) {
			if (version.contains("-SNAPSHOT")) {
				filename = filename.replace("-SNAPSHOT", "");
			}
			version = version.substring(0,version.lastIndexOf("-"));
		}
		dir = new File(dir, version);
		dir.mkdirs();
		createIndex(dir);
		
		return new File(dir, filename);
	}

	private boolean isFlatArtefact(File file) {
		return file.getName().matches("([^\\.-]+\\.)+([^\\.-]*-)+.*");
	}

	public void derive(Stack<String> path, String id, String version, File destFile) throws IOException {
		File outdir = destFile.getParentFile();
		createMD5Hash(destFile);
		createSHAHash(destFile);
		if (isPrimaryArtefact(destFile)) {
			File pom = new File(outdir, destFile.getName().replace(".jar", ".pom"));
			createPOM(destFile, pom, path, id, version);
			createMD5Hash(pom);
			createSHAHash(pom);
		}
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
		String release = maxReleaseVersion(versions);
		String snapshot = maxSnapshotVersion(versions);
		solomon.put("group", path);
		solomon.put("name", name);
		solomon.put("latest", latest);
		solomon.put("release", release);
		solomon.put("snapshot", snapshot);
		solomon.put("versions", versions);
		solomon.put("stamp", mavenTimestampFormat.format(new Date()));
		File ret = new File(dir,"metadata.xml");
		String contents = solomon.toString("metadata", session);
//System.err.println("createMetadata writing:\n" + contents);
		FileWritingUtils.writeFile(ret, contents);
		return ret;
	}

	private String maxVersion(Collection<String> versions) {
		String sofar = null;
		for (String version : versions) {
			if (null == sofar || (version.compareTo(sofar) > 0)) {
				sofar = version;
			}
		}
		
		return sofar;
	}

	private String maxReleaseVersion(Collection<String> versions) {
		String sofar = null;
		for (String version : versions) {
			if (!version.toLowerCase().contains("snapshot")) {
				if (null == sofar || (version.compareTo(sofar) > 0)) {
					sofar = version;
				}
			}
		}
		
		return sofar;
	}

	private String maxSnapshotVersion(Collection<String> versions) {
		String sofar = null;
		for (String version : versions) {
			if (version.toLowerCase().contains("snapshot")) {
				if (null == sofar || (version.compareTo(sofar) > 0)) {
					sofar = version;
				}
			}
		}
		
		return sofar;
	}

	private Collection<String> findChildVersions(File file) {
		Collection<String> ret = new ArrayList<String>();
		for (String name : file.list()) {
			if (name.matches("[\\d.]+(-SNAPSHOT)?")) {
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
			child.put("isdir", file.isDirectory());
			child.put("name", name);
			child.put("url", file.isDirectory() ? name + "/index.html" : name);
			child.put("date", modifiedFormat.format(file.lastModified()));
			if (!file.isDirectory()) {
				child.put("filesize", file.length() + " bytes");
			}
			children.add(child);
		}
		EasySolomon solomon = new EasySolomon(templates);
		Session session = new Session();
		
		solomon.put("children", children);
		solomon.put("site", "Maven Repository");
		solomon.put("page", outdir.getName());
		File ret = new File(outdir,"index.html");
		FileWritingUtils.writeFile(ret, solomon.toString("index", session));
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
	
	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.out.println("usage java -jar repomaker.jar <input-dir> <output-dir>");
			System.exit(1);
		}
		make(args[0], args[1]);
	}

	public static void make(String srcdir, String destdir) throws IOException {
		RepoMaker repoMaker = new RepoMaker(new File(srcdir), new File(destdir));
		repoMaker.make();
	}
}
