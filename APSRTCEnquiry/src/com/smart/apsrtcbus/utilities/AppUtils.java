package com.smart.apsrtcbus.utilities;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.util.Log;

import com.smart.apsrtcbus.vo.ServiceInfo;

public class AppUtils {

	public static final String SITE_URL = "http://apsrtconline.in/oprs-web/";
	
	public static final String FROM_URL =  SITE_URL + "ajax/booking/start/places.do?startPlaceName=";
	
	public static final String TO_URL = SITE_URL+"ajax/booking/end/places.do?endPlaceName=";
	
	// searchType=0 for onward and 1 for return journey
	public static final String SEARCH_URL = SITE_URL+"avail/services.do" +
			"?ajaxAction=&contextPath=%2Foprs-web&currentIndex=&searchType=0&";
	
	/**
	 * This method will parse the HTML response returned from the FROM_URL
	 * and prepare an XML format to parse the station information.	
	 * @param response
	 * @return 
	 */
	
	public static List<ServiceInfo> parseBusStations(String response) {

		String strArr[] = response.split("PlaceDivIdTbl");
		String data = "";
		Log.e("APSRTC", "ARRAY LENGTH:"+strArr.length);
		if(strArr.length>1)
		{
			data = "<table class=\"dummy"+strArr[1].replaceAll("&nbsp;", "");
			data=data.split("</table>")[0];
			data += "</table>";
			return extractData(data);
		}
		return null;
	}

	/**
	 * This method will parse the XML and form a ServiceInfo list which will be
	 * returned to populate in the popup view.
	 * @param data
	 */
	
	public static List<ServiceInfo> extractData(String data)
	{
		List<ServiceInfo> serviceInfoList = new ArrayList<ServiceInfo>();
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();

		InputSource source = new InputSource(new StringReader(data));
		try 
		{
			String expression = "//input/@value";
			NodeList nodeList = (NodeList) xpath.compile(expression).evaluate(source, XPathConstants.NODESET);
			for (int i = 0; i < nodeList.getLength(); i=i+3) {
				ServiceInfo info = new ServiceInfo(nodeList.item(i).getNodeValue(),
						nodeList.item(i+1).getNodeValue(),nodeList.item(i+2).getNodeValue());
				serviceInfoList.add(info);
				Log.e("APSRTC",info.toString());
			}

		} catch (XPathExpressionException e) {
			Log.e("APSRTC",e.getMessage());
		}
		return serviceInfoList;
	}
}
