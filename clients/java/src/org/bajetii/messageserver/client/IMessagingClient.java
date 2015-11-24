package org.bajetii.messageserver.client;

import java.io.IOException;
import java.util.Map;

public interface IMessagingClient {

	public void getMessage(Map<String, String> headers, String body, String url) throws IOException;
	public void postMessage(Map<String, String> headers, String body, String url)throws IOException;

}
