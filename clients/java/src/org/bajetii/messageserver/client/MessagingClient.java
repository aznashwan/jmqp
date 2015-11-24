package org.bajetii.messageserver.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class MessagingClient implements IMessagingClient {

	@Override
	public void getMessage(Map<String, String> headers, String body, String url) throws IOException {
		URL urlObj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

		con.setRequestMethod("GET");
		for(String key: headers.keySet()) {
			con.setRequestProperty(key, headers.get(key));
		}

		int responseCode = con.getResponseCode();
		System.out.println("\n" + "Sending 'GET' request to URL : " + url);
		System.out.println("Response code received: " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		StringBuffer response = new StringBuffer();
		response.append(body);
		in.close();

		System.out.println(response.toString());
	}

	public void postMessage(Map<String, String> headers, String body, String url) throws IOException {
		URL urlObj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

		con.setRequestMethod("POST");
		for(String key: headers.keySet()) {
			con.setRequestProperty(key, headers.get(key));
		}

		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(body);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post body: " + body);
		System.out.println("Response Code: " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		System.out.println(response.toString());
	}
}
