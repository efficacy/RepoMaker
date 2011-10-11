package org.stringtree.maven;

import java.io.File;
import java.io.IOException;

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
			}
		}
	}

}
