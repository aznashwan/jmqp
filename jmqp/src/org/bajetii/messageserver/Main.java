package org.bajetii.messageserver;


import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import org.bajetii.messageserver.server.MessagingServer;
import org.bajetii.messageserver.server.handlers.MainHandler;

public class Main {

    /**
     * main here starts a server running on port 8989 and listens and handles
     * incoming requests.
     */
    public static void main(String args[]) {
        // NOTE: hardcoded timeout !!!
        MessagingServer ms = new MessagingServer(30);
     
        HttpServer server;
		try {
	        // NOTE: hardcoded PORT !!!
			server = HttpServer.create(new InetSocketAddress(8989), 100);
			server.createContext("/bajetii/jmqp", new MainHandler(ms));
	        server.setExecutor(null);
	        server.start();

	        System.out.println("Messaging server started.");

		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
