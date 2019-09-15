package smartspace.layout;

public class BoundaryIdKey {
	
	private String id;
	private String smartspace;

	public BoundaryIdKey() {

	}

	public BoundaryIdKey(String id, String smartspace) {
		this.id = id;
		this.smartspace = smartspace;

	}

	// Getter Methods

	public String getSmartspace() {
		return smartspace;
	}

	public String getId() {
		return id;
	}

	// Setter Methods

	public void setSmartspace(String smartspace) {
		this.smartspace = smartspace;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return this.id + "#" + this.smartspace;
	}

}