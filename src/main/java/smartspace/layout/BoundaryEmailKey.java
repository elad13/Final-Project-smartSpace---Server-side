package smartspace.layout;

public class BoundaryEmailKey {

	private String smartspace;
	private String email;

	public BoundaryEmailKey() {

	}

	public BoundaryEmailKey(String email, String smartspace) {
		this.smartspace = smartspace;
		this.email = email;
	}

	// Getter Methods

	public String getSmartspace() {
		return this.smartspace;
	}

	public String getEmail() {
		return email;
	}

	// Setter Methods

	public void setSmartspace(String smartspace) {
		this.smartspace = smartspace;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return this.email + "#" + this.smartspace;
	}

}