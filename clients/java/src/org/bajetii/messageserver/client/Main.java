package org.bajetii.messageserver.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.cli.*;

public class Main {

	public static void main(String[] args) {
		CommandLineParser parser = new DefaultParser();

		Options options = new Options();
		options.addOption("u", "url", true, "Server URL");
		options.addOption("m", "method", true, "HTTP method to be used; either 'GET' or 'POST'/'PUT'");
		options.addOption("t", "type", true, "The type of the message; either 'Topic' or 'Personal'.");
		options.addOption("d", "destination", true,
				"The destination of the message be it topic or person. Used only for POST/PUT requests.");
		options.addOption("T", "Timeout", true, "Topic timeout. Used only when type parameter is 'Topic'");
		options.addOption("b", "body", true,
				"Body request. Anything for the POST/PUT request and user_name/topic_name for GET request");
		options.addOption("h", "help", false, "Display help menu");

		MessagingClient client = new MessagingClient();
		Map<String, String> headers = new HashMap<String, String>();

		 String[] args1 = new String[] {
				 "--url=http://192.168.137.1:8989/bajetii/jmqp",
				 "--method=POST",
				 "--type=Personal",
				 "--destination=iony",
		 		 "--body='Iony are mere'" };

		String[] args2 = new String[] {
				"--url=http://192.168.137.1:8989/bajetii/jmqp",
				"--method=GET",
				"--type=Personal",
				"--body=iony" };
		
		String[] args3 = new String[] {
				"--help" };


		try {
			CommandLine line = parser.parse(options, args2);

			if (line.hasOption('h')) {
				HelpFormatter f = new HelpFormatter();
				f.printHelp("Options", options);
				System.exit(0);
			}

			if (!line.hasOption("body")) {
				throw new ParseException("Missing parameter 'body'");
			}

			if (!line.hasOption("url")) {
				throw new ParseException("Missing parameter 'url'");
			}

			if (!line.hasOption("type") || (!line.getOptionValue("type").equalsIgnoreCase("Topic")
					&& !line.getOptionValue("type").equalsIgnoreCase("Personal"))) {
				throw new ParseException("Invalid parameter 'type'");
			}
			headers.put("Type", line.getOptionValue("type"));

			if (!line.hasOption("method") || (!line.getOptionValue("method").equals("GET")
					&& !line.getOptionValue("method").equals("POST") && !line.getOptionValue("method").equals("PUT"))) {
				throw new ParseException("Invalid parameter 'method'");
			}

			if (line.getOptionValue("method").equalsIgnoreCase("POST")
					|| line.getOptionValue("method").equalsIgnoreCase("PUT")) {
				if (!line.hasOption("destination")) {
					throw new ParseException("Missing parameter 'destination'");
				}
				headers.put("To", line.getOptionValue("destination"));

				if (line.getOptionValue("type").equalsIgnoreCase("Topic")) {
					if (!line.hasOption("Timeout")) {
						throw new ParseException("Missing parameter 'timeout'");
					}
					headers.put("Timeout", line.getOptionValue("Timeout"));
				}
			}

			try {
				if (line.getOptionValue("method").equalsIgnoreCase("GET")) {
					client.getMessage(headers, line.getOptionValue("body"), line.getOptionValue("url"));
				} else {
					client.postMessage(headers, line.getOptionValue("body"), line.getOptionValue("url"));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (ParseException e) {
			System.out.println("ERROR: " + e.getMessage());
			System.exit(1);
		}

		System.exit(0);
	}
}
