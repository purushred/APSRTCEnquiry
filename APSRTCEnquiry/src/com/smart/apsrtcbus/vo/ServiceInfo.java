package com.smart.apsrtcbus.vo;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ServiceInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String serviceId;
	private String serviceCode;
	private String serviceName;

	public ServiceInfo(JSONObject object){
		try {
			serviceId = object.getString("id");
			serviceName = object.getString("value");
			if(serviceName.contains("-"))
			{
				serviceName = serviceName.split("-")[0].trim();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<ServiceInfo> fromJson(JSONArray jsonObjects) {
		ArrayList<ServiceInfo> serviceList = new ArrayList<ServiceInfo>();
		for (int i = 0; i < jsonObjects.length(); i++) {
			try {
				serviceList.add(new ServiceInfo(jsonObjects.getJSONObject(i)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return serviceList;
	}

	public ServiceInfo(String id,String code, String name)
	{
		this.serviceId = id;
		this.serviceCode = code;
		this.serviceName = name;
	}

	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public String getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	@Override
	public String toString() {
		return serviceName;
	}
}
