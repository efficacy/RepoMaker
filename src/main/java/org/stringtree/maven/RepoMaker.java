package org.stringtree.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

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
			if (file.isDirectory()) {
				crawl(new File(indir, name), new File(outdir, name));
			} else {
				copy(file, outdir);
				createIndex(outdir);
			}
		}
	}

	private void createIndex(File outdir) throws IOException {
		new File(outdir, "index.html").createNewFile();
	}

	public static void copy(File file, File outdir) throws IOException {
		File destFile = new File(outdir, file.getName());
		if(!destFile.exists()) {
			destFile.createNewFile();
		}
		
		FileChannel source = null;
		FileChannel destination = null;
		try {
			source = new FileInputStream(file).getChannel();
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
