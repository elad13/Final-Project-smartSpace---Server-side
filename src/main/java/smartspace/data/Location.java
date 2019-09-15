package smartspace.data;

public class Location implements Comparable<Location>{
	
	/** Class Attributes */
	private double x;
	private double y;
	
	/** Class Constructors */
	public Location() {
	}

	public Location(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/** Class Getters & Setters */
	public double getX() {
		return x;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	/** Class Methods */
	@Override
	public String toString() {
		return "Location [x=" + x + ", y=" + y + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Location) {
			Location other = (Location)obj;
			if (new Double(x).equals(other.getX())) {
				if (new Double(y).equals(other.getY())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int compareTo(Location o) {
		int rv = new Double(x).compareTo(new Double(o.getX()));
		if (rv == 0) {
			rv = new Double(y).compareTo(new Double(o.getY()));
		}
		return rv;
	}
}
