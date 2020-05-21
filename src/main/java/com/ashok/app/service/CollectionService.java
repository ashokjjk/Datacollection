package com.ashok.app.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.stereotype.Component;

import com.ashok.app.Main;
import com.ashok.app.service.DataService;

@Component
public class CollectionService {

	final String PORT_NO = "1883";
	URLConnection connectReq;
	MqttClient client;
	URL urlObj;
	List<String> url = new ArrayList<String>();
	List<DataService> payload = new LinkedList<DataService>();

	public void startProcess() throws MqttException {
		url.add("http://uoweb3.ncl.ac.uk/api/v1.1/sensors/PER_AIRMON_MESH1911150/data/json/?");
		url.add("http://uoweb3.ncl.ac.uk/api/v1.1/sensors/PER_AIRMON_MESH301245/data/json/?");
		url.add("http://uoweb3.ncl.ac.uk/api/v1.1/sensors/PER_EMOTE_1309/data/json/?");
		try {

			for (String data : url) {
				getRawData(data);
			}
			boolean repeat = true;
			int retry = 0;
			while (repeat) {
				try {
					client = new MqttClient("tcp://" + Main.getIpAddress() + ":" + PORT_NO, "datacoll");
					MqttConnectOptions connOpts = new MqttConnectOptions();
					connOpts.setCleanSession(false); // If clean session not needed set to false
					connOpts.setKeepAliveInterval(60);
					client.connect();
					repeat = false;

				} catch (Exception e) {
					retry += 1;
					if (retry <= 4) {
						System.out.println("MQTT Connection lost. Retry no: " + retry);
						repeat = true;
					} else {
						break;
					}
				}
			}

			for (DataService data : payload) {
				String payloadFmt = data.getName() + "," + data.getVariable() + "," + data.getUnits() + ","
						+ data.getDate() + "," + data.getFlag() + "," + data.getValue();
				MqttMessage message = new MqttMessage(payloadFmt.getBytes());
				message.setQos(0);
				client.publish("datacoll", message);

			}
		}

		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		System.out.println("Process Complete..");
	}

	public void getRawData(String reqUrl) throws IOException {
		int repeat = 0, retry = 0;
		while (repeat == 0) {
			try {
				urlObj = new URL(reqUrl + getDate());
				connectReq = urlObj.openConnection();
				connectReq.connect();
				repeat = 1;
			} catch (Exception e) {
				retry += 1;
				System.out.println("API connection lost.. Retry no: " + retry);
				System.out.println(e.getMessage());
				if (retry <= 4) {
					repeat = 0;
				} else {
					break;
				}
			}
		}

		BufferedReader stream = new BufferedReader(new InputStreamReader(connectReq.getInputStream()));
		String inputLine;
		StringBuffer bufferData = new StringBuffer();
		while ((inputLine = stream.readLine()) != null) {
			bufferData.append(inputLine);
		}

		JSONObject rawData = new JSONObject(bufferData.toString());
		JSONArray sensors = rawData.getJSONArray("sensors");

		for (int d = 0; d < sensors.length(); d++) {
			JSONObject arrData = sensors.getJSONObject(d);
			JSONObject data = new JSONObject(arrData.getJSONObject("data").toString());
			JSONArray sensorTy = data.names();
			try {
				for (Object eachTy : sensorTy) {
					JSONArray getData = data.getJSONArray((String) eachTy);
					for (int i = 0; i < getData.length(); i++) {
						JSONObject dataObj = getData.getJSONObject(i);

						String sensorNm = dataObj.getString("Sensor Name");
						String variable = dataObj.getString("Variable");
						String units = dataObj.getString("Units");
						long unixSeconds = dataObj.getLong("Timestamp");

						// Epochs returns too many long data which causes wrong calculation of unix time
						String sec = Long.toString(unixSeconds);
						Long Csec = Long.parseLong(sec.substring(0, 10));
						Date date = new java.util.Date(Csec * 1000L);
						SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						// Timezone reference for formatting
						sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+1"));
						String formattedDate = sdf.format(date);

						Double value = dataObj.getDouble("Value");
						Boolean flag = dataObj.getBoolean("Flagged as Suspect Reading");

						DataService dataService = new DataService();
						dataService.setName(sensorNm);
						dataService.setVariable(variable);
						dataService.setUnits(units);
						dataService.setFlag(flag);
						dataService.setValue(value);
						dataService.setDate(formattedDate);
						payload.add(dataService);

					}
				}
			} catch (NullPointerException e) {
				System.out.println("No data in API : " + reqUrl + getDate());
				e.getMessage();
			}
		}
	}

	private String getDate() {

		DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

//		For last 1 day time period
		String from = dtFormat.format(LocalDateTime.now().minusHours(24));
		String to = dtFormat.format(LocalDateTime.now().minusHours(1));

		return "starttime=" + from + "&endtime=" + to;
	}
}
