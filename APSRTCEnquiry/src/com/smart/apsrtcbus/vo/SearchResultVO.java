package com.smart.apsrtcbus.vo;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchResultVO implements Parcelable  {

	private String serviceNo;
	private String serviceName; 
	private String depotName;
	private String viaPlace;
	private String distance;
	private String departure;
	private String arrival;
	private String adultFare;
	private String childFare;
	private String type;
	private String availableSeats;

	// The following methods that are required for using Parcelable
	private SearchResultVO(Parcel in) {
		serviceNo = in.readString();
		serviceName = in.readString();
		depotName = in.readString();
		viaPlace = in.readString();
		distance = in.readString();
		departure = in.readString();
		arrival = in.readString();
		adultFare = in.readString();
		childFare = in.readString();
		type = in.readString();
		availableSeats = in.readString();
	}

	public SearchResultVO()
	{
		
	}
	
	public String getServiceNo() {
		return serviceNo;
	}
	public void setServiceNo(String serviceNo) {
		this.serviceNo = serviceNo;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getDepotName() {
		return depotName;
	}
	public void setDepotName(String depotName) {
		this.depotName = depotName;
	}
	public String getViaPlace() {
		return viaPlace;
	}
	public void setViaPlace(String viaPlace) {
		this.viaPlace = viaPlace;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public String getDeparture() {
		return departure;
	}
	public void setDeparture(String departure) {
		this.departure = departure;
	}
	public String getArrival() {
		return arrival;
	}
	public void setArrival(String arrival) {
		this.arrival = arrival;
	}
	public String getAdultFare() {
		return adultFare;
	}
	public void setAdultFare(String adultFare) {
		this.adultFare = adultFare;
	}
	public String getChildFare() {
		return childFare;
	}
	public void setChildFare(String childFare) {
		this.childFare = childFare;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAvailableSeats() {
		return availableSeats;
	}
	public void setAvailableSeats(String availableSeats) {
		this.availableSeats = availableSeats;
	}

	@Override
	public String toString() {

		StringBuffer buffer = new StringBuffer();

		buffer.append("Service No:");
		buffer.append(serviceNo);

		buffer.append("Service Name:");
		buffer.append(serviceName);

		buffer.append("Depot");
		buffer.append(depotName);

		buffer.append("Via");
		buffer.append(viaPlace);

		buffer.append("Distance");
		buffer.append(distance);

		buffer.append("Departure");
		buffer.append(departure);

		buffer.append("Arrival");
		buffer.append(arrival);

		buffer.append("Adult Fare");
		buffer.append(adultFare);

		buffer.append("Child Fare");
		buffer.append(childFare);

		buffer.append("Type");
		buffer.append(type);

		buffer.append("Available Seats");
		buffer.append(availableSeats);

		return buffer.toString();
	}



	public void writeToParcel(Parcel out, int flags) {
		out.writeString(serviceNo);
		out.writeString(serviceName);
		out.writeString(depotName);
		out.writeString(viaPlace);
		out.writeString(distance);
		out.writeString(departure);
		out.writeString(arrival);
		out.writeString(adultFare);
		out.writeString(childFare);
		out.writeString(type);
		out.writeString(availableSeats);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	// Just cut and paste this for now
	public static final Parcelable.Creator<SearchResultVO> CREATOR = new Parcelable.Creator<SearchResultVO>() {
		public SearchResultVO createFromParcel(Parcel in) {
			return new SearchResultVO(in);
		}

		public SearchResultVO[] newArray(int size) {
			return new SearchResultVO[size];
		}
	};

}
