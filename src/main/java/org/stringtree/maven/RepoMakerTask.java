package org.stringtree.maven;

import java.io.IOException;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;

public class RepoMakerTask extends Task {

    String srcdir;
    String destdir;
    
    public void setSrcdir(String from) {
        this.srcdir = from;
    }
    
    public void setDestdir(String to) {
        this.destdir = to;
    }

    public void execute() {
        if (null == srcdir || null == destdir) {
            throw new BuildException("RepoMaker task requires both 'srcdir' and 'destdir' atributes");
        }
        
        try {
			RepoMaker.make(srcdir, destdir);
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException(e);
		}
    }

}