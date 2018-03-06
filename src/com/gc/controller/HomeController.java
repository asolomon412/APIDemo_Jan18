package com.gc.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/*
 * author: Antonella Solomon
 *
 */

@Controller
public class HomeController {

	@RequestMapping("/")
	public ModelAndView index(Model model) {

		String prodCenter = "";

		try {
			// the HttpClient Interface represents the contract for the HTTP request
			// execution
			HttpClient http = HttpClientBuilder.create().build();

			// HttpHost holds the variables needed for the connections
			// default port for http is 80
			// default port for https is 443
			HttpHost host = new HttpHost("forecast.weather.gov", 80, "http");

			// HttpGet retrieves the info identified by the request url (returns as an
			// entity)
			HttpGet getPage = new HttpGet("/MapClick.php?lat=42.331427&lon=-83.045754&FcstType=json");

			HttpResponse resp = http.execute(host, getPage);

			// casting the entity returned to a string
			String jsonString = EntityUtils.toString(resp.getEntity());

			// assign the returned result to a json object
			JSONObject json = new JSONObject(jsonString);
			prodCenter = json.get("productionCenter").toString();

			// this is a test print to our console to make sure we are communicating with
			// the API (response code should be 200)
			System.out.println("Response code: " + resp.getStatusLine().getStatusCode());

			String text = "";
			JSONArray arr = json.getJSONObject("data").getJSONArray("text");

			for (int i = 0; i < arr.length(); i++) {
				text += ("<h6>" + arr.getString(i) + "</h6>");
			}

			model.addAttribute("jsonData", text);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ModelAndView("index", "centerData", prodCenter);
	}

	// this is an alternative way to pull in json data
	@RequestMapping("/nasadata")
	public ModelAndView nasaData() {

		String center = "";
		String city = "";
		String contact = "";
		String forPrint = "";

		try {
			// this is how we create the url code in order to call the JSON response with
			// info we request
			URL url = new URL("https://data.nasa.gov/resource/9g7e-7hzz.json");

			// the openstream() allows us to open and read the url that was given -- we will
			// need to loop through this
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

			String line = reader.readLine();
			String jsonString = "";

			while (line != null) {
				jsonString += line;
				line = reader.readLine();
			}

			JSONArray json = new JSONArray(jsonString);

			for (int i = 0; i < json.length(); i++) {
				center = json.getJSONObject(i).getString("center");
				city = json.getJSONObject(i).getString("city");
				contact = json.getJSONObject(i).getString("contact");
				forPrint += ("<h6>" + center + ", " + city + ", " + contact + "</h6>");
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ModelAndView("welcome", "message", forPrint);
	}

	@RequestMapping("xmldata")
	public ModelAndView xmlData() {

		String result = "";
		try {
			// the HttpClient Interface represents the contract for the HTTP request
			// execution
			HttpClient http = HttpClientBuilder.create().build();

			// HttpHost holds the variables needed for the connections
			// default port for http is 80
			// default port for https is 443
			HttpHost host = new HttpHost("forecast.weather.gov", 80, "http");

			// HttpGet retrieves the info identified by the request url (returns as an
			// entity)
			HttpGet getPage = new HttpGet("/MapClick.php?lat=42.331427&lon=-83.045754&FcstType=xml");

			HttpResponse resp = http.execute(host, getPage);

			// casting the entity returned to a string
			String xmlString = EntityUtils.toString(resp.getEntity());

			// factory is going to enable our app to obtain a parser for the XML DOM
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			DocumentBuilder db = dbf.newDocumentBuilder();

			InputSource inStream = new InputSource();
			inStream.setCharacterStream(new StringReader(xmlString));

			Document doc = db.parse(inStream);

			String weatherForecast = "";

			NodeList nl = doc.getElementsByTagName("text");

			for (int i = 0; i < nl.getLength(); i++) {
				Element nameElement = (Element) nl.item(i);
				weatherForecast = nameElement.getFirstChild().getNodeValue().trim();
				result += ("<h6>" + weatherForecast + "</h6>");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ModelAndView("xmlstuff", "xmlData", result);
	}

}
