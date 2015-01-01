package com.smart.apsrtcbus.utilities;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smart.apsrtcbus.vo.SearchResultVO;
import com.smart.apsrtcbus.vo.StationVO;

public class AppUtils {

	public static final String SITE_URL = "http://apsrtconline.in/oprs-web/";

	public static final String STATION_INFO_URL = "http://apsrtc-reddy.rhcloud.com/";

	// searchType=0 for onward and 1 for return journey
	public static final String SEARCH_URL = SITE_URL+"forward/booking/avail/services.do?adultMale=1&childMale=0&";

	public static final String MAIN_SEARCH_URL = SITE_URL+"avail/services.do?";

	/**
	 * To check whether internet is enabled.
	 * @param cm - Connectivity Manager class
	 * @return true: network connected. false: network not connected.
	 */
	public static boolean isNetworkOnline(ConnectivityManager cm) {
		boolean status=false;
		try{
			NetworkInfo netInfo = cm.getNetworkInfo(0);
			if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
				status= true;
			}else {
				netInfo = cm.getNetworkInfo(1);
				if(netInfo!=null && netInfo.getState()==NetworkInfo.State.CONNECTED)
					status= true;
			}
		}catch(Exception e){
			Log.e("Network Error", e.getMessage());
			return false;
		}
		return status;
	}

	/**
	 * This method will parse the HTML response returned from the FROM_URL
	 * and prepare an XML format to parse the station information.	
	 * @param response
	 * @return 
	 */

	public static String parseBusStations(String response) {
		int startIndex = response.indexOf("jsondata =");
		int endIndex = response.indexOf("</script>",startIndex);

		String str = response.substring(startIndex, endIndex);
		String data = str.split("=")[1];
		return data;
	}

	public static List<StationVO> getBusStationList(String data) {
		Gson gson = new Gson();
		Type collectionType = new TypeToken<List<StationVO>>() {}.getType();
		List<StationVO> list = gson.fromJson(data, collectionType);
		return list;
	}

	/** 
	 * This method will make it proper xml format so that it will be easy to
	 * process.
	 * @param response : the raw html content
	 * @return will parse and return object.
	 */
	public static ArrayList<SearchResultVO> formatData(String response)
	{
		String strArr[] = response.split("BoxBorder");
		if(strArr.length>1)
		{
			String data = "<table class=\"dummy"+strArr[1].replaceAll("&nbsp;", "");
			data = data.substring(0, data.lastIndexOf("</table>"));
			data += "</table>";
			Pattern p = Pattern.compile(Pattern.quote("'0');\">"));
			Matcher m = p.matcher(data);
			data = m.replaceAll("'0');\"/>");
			ArrayList<SearchResultVO> searchData = extractSearchData(data);
			//saxParser(data);
			return searchData;
		}
		return new ArrayList<SearchResultVO>();
	}

	public static ArrayList<SearchResultVO> saxParser(String data){

		ArrayList<SearchResultVO> serviceInfoList = new ArrayList<SearchResultVO>();
		data = data.replaceAll("&", "");
		try {
			XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser()
					.getXMLReader();

			DefaultHandler handler = new DefaultHandler() {

				private List<SearchResultVO> results;
				private String tempVal;
				private SearchResultVO resultsVO;

				public List<SearchResultVO> getEmployees() {
					return results;
				}

				// Event Handlers
				public void startElement(String uri, String localName, String qName,
						Attributes attributes) throws SAXException {

					Log.e("ATTRVA:",attributes.getLength()+"");
					// reset
					tempVal = "";
					Log.e("qName Start:",qName);
					if (qName.equalsIgnoreCase("tr")) {
						resultsVO = new SearchResultVO();
					}
				}
				
				public void characters(char[] ch, int start, int length)
						throws SAXException {
					tempVal = new String(ch, start, length);
					Log.e("Element Value:",tempVal);
				}

				public void endElement(String uri, String localName, String qName)
						throws SAXException {
					Log.e("qName End:",qName);
					if (qName.equalsIgnoreCase("tr")) {
						// add it to the list
						results.add(resultsVO);
					}
				}
			};

			xmlReader.setContentHandler(handler);
			// the process starts
			xmlReader.parse(new InputSource(new StringReader(data)));
			//serviceInfoList = handler.getEmployees();

		} catch (Exception ex) {
			Log.d("XML", "SAXXMLParser: parse() failed");
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
		data = data.replaceAll("&", "");
		InputSource source = new InputSource(new StringReader(data));
		try 
		{
			//Log.e("5:", new Date().getTime()+"");
			String expression = "/table/tr[(@class='oddRow') or (@class='evenRow')]/td";
			NodeList nodeList = (NodeList) xpath.compile(expression).evaluate(source, XPathConstants.NODESET);
			//Log.e("6:", new Date().getTime()+"");
			int length = nodeList.getLength();
			if(length>1) {
				SearchResultVO resultVO;
				for (int i = 0; i < length; i=i+12) {
					resultVO = new SearchResultVO();
					resultVO.setServiceName(nodeList.item(i+1).getFirstChild().getNodeValue().trim());
					resultVO.setDepotName(nodeList.item(i+2).getFirstChild().getNodeValue().trim());
					resultVO.setViaPlace(nodeList.item(i+3).getFirstChild().getNodeValue().trim());
					resultVO.setDistance(nodeList.item(i+4).getFirstChild().getNodeValue().trim());
					resultVO.setDeparture(nodeList.item(i+5).getFirstChild().getNodeValue().trim());
					resultVO.setArrival(nodeList.item(i+6).getFirstChild().getNodeValue().trim());
					resultVO.setAdultFare(nodeList.item(i+7).getFirstChild().getNodeValue().trim());
					resultVO.setChildFare(nodeList.item(i+8).getFirstChild().getNodeValue().trim());
					resultVO.setType(nodeList.item(i+9).getFirstChild().getNodeValue().trim());
					String seats = nodeList.item(i+10).getFirstChild().getNodeValue().trim();
					if(seats.contains("("))
					{
						seats = seats.split(Pattern.quote("("))[0].trim()+" WL";
					}
					resultVO.setAvailableSeats(seats);
					serviceInfoList.add(resultVO);
				}
			}
		} catch (XPathExpressionException e) {
			Log.e("APSRTC",e.getMessage());
		}
		return serviceInfoList;
	}

	/**
	 * This method is to increment/decrement a date by 1 day.
	 * @param operation - -1 for decrement and 1 for increment.
	 * @param dateStr : Input date string which is to be incremented/decremented.
	 * @return New date string will be returned.
	 */
	public static String getNewDate(int operation,String dateStr)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
		try {
			Date date = formatter.parse(dateStr);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			switch(operation)
			{
			case -1:
				cal.add(Calendar.DAY_OF_MONTH, -1);
				break;
			case 1:
				cal.add(Calendar.DAY_OF_MONTH, 1);
				break;
			}

			Date newDate = cal.getTime();
			return formatter.format(newDate);

		} catch (ParseException e) {
			Log.e("Error", e.getMessage());
		}
		return null;
	}

	public static String getFormattedDate(String dateStr){

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
		Date date = null;
		try {
			date = format.parse(dateStr);
		} catch (ParseException e) {
			Log.e("Error", e.getLocalizedMessage());
		}

		format.applyPattern("EEE dd MMM, yyyy");
		return format.format(date);
	}
}
