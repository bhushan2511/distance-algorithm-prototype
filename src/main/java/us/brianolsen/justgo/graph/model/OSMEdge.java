package us.brianolsen.justgo.graph.model;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.apache.commons.graph.WeightedEdge;

public class OSMEdge implements WeightedEdge{
	
	//private Integer id;
	private OSMVertex v1;
	private OSMVertex v2;
	private Double weight;

	public OSMEdge(Double weight) {
		//this.id = id;
		this.weight = weight;
	}

	public OSMEdge(OSMVertex v1, OSMVertex v2) {
		//this.id = id;
		this.v1 = v1;
		this.v2 = v2;
		this.weight = distance(v1.getLat(), v2.getLat(), v1.getLon(), v2.getLon());;
	}

//	public Integer getId() {
//		return id;
//	}
//
//	public void setId(Integer id) {
//		this.id = id;
//	}

	public OSMVertex getV1() {
		return v1;
	}

	public void setV1(OSMVertex v1) {
		this.v1 = v1;
	}

	public OSMVertex getV2() {
		return v2;
	}

	public void setV2(OSMVertex v2) {
		this.v2 = v2;
	}
	
	@Override
	public double getWeight() {
		return this.weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}
	
	/**
	 * https://stackoverflow.com/a/16794680/2023810
	 * 
	 * Calculate distance between two points in latitude and longitude taking
	 * into account height difference. If you are not interested in height
	 * difference pass 0.0. Uses Haversine method as its base.
	 * 
	 * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
	 * el2 End altitude in meters
	 * @returns Distance in Meters
	 */
	private double distance(double lat1, double lat2, double lon1,
	        double lon2) {

	    final int R = 6371; // Radius of the earth

	    double latDistance = Math.toRadians(lat2 - lat1);
	    double lonDistance = Math.toRadians(lon2 - lon1);
	    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
	            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
	            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    double distance = R * c * 1000; // convert to meters

	    distance = Math.pow(distance, 2);

	    return Math.sqrt(distance);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		DecimalFormat format = new DecimalFormat("##0.00000m", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		builder.append("OSMEdge [");
		builder.append(v1.getId());
		builder.append("<- ");
		builder.append(format.format(weight));
		builder.append(" ->");
		builder.append(v2.getId());
		builder.append("]");
		return builder.toString();
	}

}
