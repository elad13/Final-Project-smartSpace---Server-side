package smartspace.layout;


public class LocationBoundary{
	
	/** Class Attributes */
	private double lat;
	private double lng;
	
	/** Class Constructors */
	public LocationBoundary() {
	}

	public LocationBoundary(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}
	
	/** Class Getters & Setters */
	public double getLat() {
		return lat;
	}
	
	public void setLay(double lat) {
		this.lat = lat;
	}
	
	public double getLng() {
		return lng;
	}
	
	public void setLng(double lng) {
		this.lng = lng;
	}
	
	/** Class Methods */
	@Override
	public String toString() {
		return "Location [lat=" + lat + ", lng=" + lng + "]";
	}
	
}
