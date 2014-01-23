package com.smart.apsrtcbus.utilities;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.util.Log;

import com.smart.apsrtcbus.vo.SearchResultVO;
import com.smart.apsrtcbus.vo.ServiceInfo;

public class AppUtils {

	public static final String SITE_URL = "http://apsrtconline.in/oprs-web/";
	
	public static final String FROM_URL =  SITE_URL + "ajax/booking/start/places.do?startPlaceName=";
	
	public static final String TO_URL = SITE_URL+"ajax/booking/end/places.do?endPlaceName=";
	
	// searchType=0 for onward and 1 for return journey
	public static final String SEARCH_URL = SITE_URL+"forward/booking/avail/services.do?adultMale=1&childMale=0&";
	
	/**
	 * This method will parse the HTML response returned from the FROM_URL
	 * and prepare an XML format to parse the station information.	
	 * @param response
	 * @return 
	 */
	
	public static List<ServiceInfo> parseBusStations(String response) {

		String strArr[] = response.split("PlaceDivIdTbl");
		String data = "";
		if(strArr.length>1)
		{
			data = "<table class=\"dummy"+strArr[1].replaceAll("&nbsp;", "");
			data=data.split("</table>")[0];
			data += "</table>";
			return extractData(data);
		}
		return null;
	}

	public static ArrayList<SearchResultVO> formatData(String response)
	{
		String strArr[] = response.split("BoxBorder");
		String data = "";
		if(strArr.length>1)
		{
			data = "<table class=\"dummy"+strArr[1].replaceAll("&nbsp;", "");
			data = data.substring(0, data.lastIndexOf("</table>"));
			data += "</table>";
			data = data.replaceAll("\\n", " ");
			data = data.replaceAll("\\t", " ");
			Pattern p = Pattern.compile(Pattern.quote("'0');\">"));
			Matcher m = p.matcher(data);
			data = m.replaceAll("'0');\"/>");
			return extractSearchData(data);
		}
		return null;
	}
	
	/**
	 * This method will parse the XML and form a ServiceInfo list which will be
	 * returned to populate in the popup view.
	 * @param data
	 */
	
	public static ArrayList<SearchResultVO> extractSearchData(String data)
	{
		ArrayList<SearchResultVO> serviceInfoList = new ArrayList<SearchResultVO>();
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();

		InputSource source = new InputSource(new StringReader(data));
		try 
		{
			String expression = "/table/tr[(@class='srvcLstCss_1') or (@class='srvcLstCss_0')]/td";
			NodeList nodeList = (NodeList) xpath.compile(expression).evaluate(source, XPathConstants.NODESET);
//			Log.e("LENGTH:", nodeList.getLength()+"");
			for (int i = 0; i < nodeList.getLength(); i=i+13) {
				SearchResultVO resultVO = new SearchResultVO();
				resultVO.setServiceName(nodeList.item(i+2).getFirstChild().getNodeValue().trim());
				resultVO.setDepotName(nodeList.item(i+3).getFirstChild().getNodeValue().trim());
				resultVO.setViaPlace(nodeList.item(i+4).getFirstChild().getNodeValue().trim());
				resultVO.setDistance(nodeList.item(i+5).getFirstChild().getNodeValue().trim());
				resultVO.setDeparture(nodeList.item(i+6).getFirstChild().getNodeValue().trim());
				resultVO.setArrival(nodeList.item(i+7).getFirstChild().getNodeValue().trim());
				resultVO.setAdultFare(nodeList.item(i+8).getFirstChild().getNodeValue().trim());
				resultVO.setChildFare(nodeList.item(i+9).getFirstChild().getNodeValue().trim());
				resultVO.setType(nodeList.item(i+10).getFirstChild().getNodeValue().trim());
				resultVO.setAvailableSeats(nodeList.item(i+11).getFirstChild().getNodeValue().trim());
				serviceInfoList.add(resultVO);
//				Log.e("RESULT",resultVO.toString());
			}

		} catch (XPathExpressionException e) {
			Log.e("APSRTC",e.getMessage());
		}
		return serviceInfoList;
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
//				Log.e("APSRTC",info.toString());
			}

		} catch (XPathExpressionException e) {
			Log.e("APSRTC",e.getMessage());
		}
		return serviceInfoList;
	}
}
