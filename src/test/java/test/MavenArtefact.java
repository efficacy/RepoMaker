package test;

import org.stringtree.util.Utils;

public class MavenArtefact {
	private final String groupId;
	private final String artefactId;
	private final String version;
	private final int hash;

	public MavenArtefact(String groupId, String artefactId, String version) {
		this.groupId = groupId;
		this.artefactId = artefactId;
		this.version = version;
		this.hash = (groupId + "/" + artefactId + "/" + version).hashCode(); 
	}

	public String getGroupId() {
		return groupId;
	}

	public String getArtefactId() {
		return artefactId;
	}

	public String getVersion() {
		return version;
	}
	
	@Override public int hashCode() {
		return hash;
	}
	
	@Override public boolean equals(Object obj) {
		if (this == obj) return true;
		if (null == obj || !(obj instanceof MavenArtefact)) return false;
		MavenArtefact other = (MavenArtefact)obj;
		
		return 
			Utils.same(this.groupId, other.groupId) && 
			Utils.same(this.artefactId, other.artefactId) &&
			Utils.same(this.version, other.version);
	}
	
	@Override public String toString() {
		return "MavenArtefact[" + groupId + "][" + artefactId + "][" + version + "]";
	}
}
