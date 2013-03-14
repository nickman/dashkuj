/**
 * Helios, OpenSource Monitoring
 * Brought to you by the Helios Development Group
 *
 * Copyright 2007, Helios Development Group and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org. 
 *
 */
package org.helios.dashkuj.core.apiimpl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientResponse;

/**
 * <p>Title: AbstractDashku</p>
 * <p>Description: Base class for Dashku and asyncDashku implementations</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.core.apiimpl.AbstractDashku</code></p>
 */

public class AbstractDashku {
	/** The API key */
	protected final String apiKey;
	/** The dashku server host */
	protected final String host;
	/** The dashku server port */
	protected final int port;
	/** An http client */
	protected final HttpClient client; 	
	/** The instance logger */
	protected final Logger log;
	/** The client request timeout in ms. */
	protected long timeout = DEFAULT_TIMEOUT;
	
	
	/** The default request timeout in ms. */
	public static final long DEFAULT_TIMEOUT = 2000;

	/**
	 * Creates a new AbstractDashku
	 * @param client The http client for connecting to the dashku server
	 * @param apiKey The dashku api key
	 * @param host The dashku server host or ip address
	 * @param port The dashku server port
	 */
	protected AbstractDashku(HttpClient client, String apiKey, String host, int port) {
		if(apiKey==null || apiKey.trim().isEmpty()) throw new IllegalArgumentException("The passed APIKey was null", new Throwable());
		if(host==null || host.trim().isEmpty()) throw new IllegalArgumentException("The passed host was null", new Throwable());
		this.apiKey = apiKey;
		this.host = host;
		this.port = port;
		this.client = client.setHost(host).setPort(port).setConnectTimeout(timeout).setKeepAlive(true);
		log = LoggerFactory.getLogger(String.format("%s.%s:%s", getClass().getName(), this.host, this.port));
		
	}
	
	/**
	 * Returns the dashku server host name or ip address
	 * @return the dashku server host name or ip address
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Returns the dashku server port
	 * @return the dashku server port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Returns the configured Dashku api key
	 * @return the configured Dashku api key
	 */
	public String getApiKey() {
		return apiKey;
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("% [\\n\\thost:%s, port:%s]", getClass().getSimpleName(), host, port);
	}
	
	/**
	 * Renders the passed HttpClientResponse to a formatted string
	 * @param event The client response
	 * @param data The response data buffer
	 * @return a formatted string
	 */
	public static String render(HttpClientResponse event, Buffer data) {
		StringBuilder b = new StringBuilder("HttpClientResponse [");
		b.append("\n\tStatus:").append(event.statusMessage).append(event.statusCode);
		b.append("\n\tData Size:").append(data.length());		
		if(!event.headers().isEmpty()) {
			b.append("\n\tHeaders:");
			for(Map.Entry<String, String> h: event.headers().entrySet()) {
				b.append("\n\t\t").append(h.getKey()).append(":").append(h.getValue());
			}
		}
		if(!event.trailers().isEmpty()) {
			b.append("\n\tTrailers:");
			for(Map.Entry<String, String> h: event.trailers().entrySet()) {
				b.append("\n\t\t").append(h.getKey()).append(":").append(h.getValue());
			}
		}
		if(data.length()>0) {
			b.append("\n\tData:").append(data.toString("UTF-8"));
		}
		b.append("\n]");
		return b.toString();
	}

	

}
