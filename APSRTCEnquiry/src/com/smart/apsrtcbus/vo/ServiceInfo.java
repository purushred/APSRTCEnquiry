package com.smart.apsrtcbus.vo;

public class ServiceInfo {

	private String serviceId;
	private String serviceCode;
	private String serviceName;

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
