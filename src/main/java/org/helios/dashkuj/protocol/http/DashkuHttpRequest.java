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
package org.helios.dashkuj.protocol.http;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;

/**
 * <p>Title: DashkuHttpRequest</p>
 * <p>Description: A builder for http requests for Dashku API calls </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.protocol.http.DashkuHttpRequest</code></p>
 */

public class DashkuHttpRequest {
	/** The HTTP version */
	protected final HttpVersion version;
	/** The HTTP method */
	protected final HttpMethod method;
	/** The build HTTP request URI */
	protected final String uri;
	/** The optional request payload */
	protected Object payload;
	/** The bufferized payload */
	protected ChannelBuffer channelBuffer = ChannelBuffers.EMPTY_BUFFER;
	
	
	
	/**
	 * Creates a new DashkuHttpRequest
	 * @param payload The optional request payload
	 * @param version The HTTP version
	 * @param method The HTTP method
	 * @param uriTemplate The request URI template
	 * @param uriArgs The fillin values for the URI template 
	 */
	public DashkuHttpRequest(Object payload, HttpVersion version, HttpMethod method, String uriTemplate, Object...uriArgs) {
		this.payload = payload;
		this.version = (version==null ? HttpVersion.HTTP_1_1 : version);
		this.method = method;
		uri = String.format(uriTemplate, uriArgs);
		// String.format(URI_POST_TRANSMIT, apiKey)
	}
	
	/**
	 * Creates a new HTTP 1.1 null payload DashkuHttpRequest 
	 * @param method The HTTP method
	 * @param uriTemplate The request URI template
	 * @param uriArgs The fillin values for the URI template 
	 */
	public DashkuHttpRequest(HttpMethod method, String uriTemplate, Object...uriArgs) {
		this(null, HttpVersion.HTTP_1_1, HttpMethod.GET, uriTemplate, uriArgs);
	}
	
	/**
	 * Builds and returns the HttpRequest
	 * @return the HttpRequest
	 */
	public HttpRequest getHttpRequest() {
		HttpRequest req = new DefaultHttpRequest(version, method, uri);
		req.addHeader(HttpHeaders.Names.ACCEPT, "application/json");	
		int contentLength = channelBuffer.readableBytes();
		if(contentLength>0) {					
			req.setContent(channelBuffer);
			req.addHeader(HttpHeaders.Names.CONTENT_TYPE, "application/json");			
			HttpHeaders.setContentLength(req, contentLength);
		} 
		
		return req;
	}
	
	/**
	 * Sets the bufferized version of the payload
	 * @param buffer the bufferized version of the payload
	 */
	public void setChannelBuffer(ChannelBuffer buffer) {
		this.channelBuffer = buffer;
		this.payload = null;
	}
	
	/**
	 * Returns the bufferized version of the payload
	 * @return the bufferized version of the payload
	 */
	public ChannelBuffer getChannelBuffer() {
		return channelBuffer;
	}

	/**
	 * Returns the HTTP version
	 * @return the version
	 */
	public HttpVersion getVersion() {
		return version;
	}

	/**
	 * Returns the HTTP method
	 * @return the method
	 */
	public HttpMethod getMethod() {
		return method;
	}

	/**
	 * Returns the request URI
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * Returns the request payload
	 * @return the payload
	 */
	public Object getPayload() {
		return payload;
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DashkuHttpRequest [\n\tversion:");
		builder.append(version);
		builder.append("\n\tmethod:");
		builder.append(method);
		builder.append("\n\turi:");
		builder.append(uri);
		builder.append("\n\tpayload:");
		builder.append(payload);
		builder.append("]");
		return builder.toString();
	}
	
	

}
