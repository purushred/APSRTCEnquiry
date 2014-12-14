package com.smart.apsrtcbus.restful;

import java.util.List;

import com.smart.apsrtcbus.vo.StationVO;

import retrofit.http.GET;

public interface StationInfoService {

	@GET("/apsrtc/stations")
	List<StationVO> getStations();
	
}