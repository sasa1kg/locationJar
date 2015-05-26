package com.travel;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.opencsv.CSVWriter;



public class CSVGenerator {

	/**
	 * Constants
	 */
	public static final String CSV_EXTENSION = ".csv";
	public static final String API_URL = "http://api.goeuro.com/api/v2/position/suggest/en/";
	public static final String FIELD_ID = "_id";
	public static final String FIELD_NAME = "name";
	public static final String FIELD_TYPE = "type";
	public static final String FIELD_LONG = "longitude";
	public static final String FIELD_LAT = "latitude";
	public static final String OBJ_GEO = "geo_position";


	public static void main(String [] args)
	{
		if (args.length != 1) {
			System.err.println("ERROR Invalid number of entry arguments.");
		} else {
			JSONArray jsonFromURL = null;
			String commandArgument = args[0].trim().toLowerCase();
			try {
				jsonFromURL = readJsonFromUrl(API_URL + commandArgument);
			} catch (IOException e) {
				System.err.println("IO Error - " + e.toString());
			} catch (JSONException e) {
				System.err.println("JSON Error - " + e.toString());
			}

			if (jsonFromURL.length() != 0) {
				generateCsvFile(commandArgument, jsonFromURL);
			} else {
				System.out.println("INFO - No data for the entered location.");
			}
		}
	}


	/**
	 * @param location - name of file to be generated
	 * @param json - JSONArray extracted
	 */
	private static void generateCsvFile(String location, JSONArray json) {
		try {			
			CSVWriter writer = new CSVWriter(new FileWriter(location + CSV_EXTENSION), ';', CSVWriter.NO_QUOTE_CHARACTER);
			String[] entries = new String[5];
			for (int i=0; i<json.length(); i++) {
				JSONObject jsonObj = json.optJSONObject(i);
				if (jsonObj != null) {
					JSONObject locationObj = jsonObj.getJSONObject(OBJ_GEO);
					entries[0] = Integer.toString(jsonObj.optInt(FIELD_ID));
					entries[1] = jsonObj.optString(FIELD_NAME);
					entries[2] = jsonObj.optString(FIELD_TYPE);
					entries[3] = Double.toString(locationObj.optDouble(FIELD_LONG));
					entries[4] = Double.toString(locationObj.optDouble(FIELD_LAT));
				}
				writer.writeNext(entries);
			}
			writer.close();
			System.out.println("File " + location + CSV_EXTENSION + " generated");
		} catch (IOException e) {
			System.err.println("Error generating CSV file " + e.toString());
		} catch (JSONException e) {
			System.err.println("JSON Error - " + e.toString());
		}

	}

	/**
	 * @param rd
	 * @return
	 * @throws IOException
	 */
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	/**
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	public static JSONArray readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONArray json = new JSONArray(jsonText);
			return json;
		} finally {
			is.close();
		}
	}


}
