package com.smart.apsrtcbus.vo;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StationVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String value;

	public StationVO(){}
	
	public StationVO(String id, String value){
		this.id = id;
		this.value = value;
	}
	
	public StationVO(JSONObject object){
		try {
			id = object.getString("id");
			value = object.getString("value");
			if(value.contains("-"))
			{
				value = value.split("-")[0].trim();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}
