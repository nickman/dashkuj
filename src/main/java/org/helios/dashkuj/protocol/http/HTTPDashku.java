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

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.helios.dashkuj.api.AbstractDashku;
import org.helios.dashkuj.api.DashkuAPIException;
import org.helios.dashkuj.domain.Dashboard;
import org.helios.dashkuj.transport.TCPConnector;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelLocal;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;
import org.jboss.netty.handler.queue.BlockingReadHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

/**
 * <p>Title: HTTPDashku</p>
 * <p>Description: Synchronous Dashku API implementation over HTTP/JSON</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.protocol.http.HTTPDashku</code></p>
 */

public class HTTPDashku extends AbstractDashku  {
	/** The netty channel connecting to the dashku server */
	protected final Channel channel;
	
	 /** Synchronous invication handler */
	protected final BlockingReadHandler<Object> synchReader = new BlockingReadHandler<Object>();
	
	/** Shared execution handler */
	private static final ExecutionHandler executionHandler = new ExecutionHandler(new OrderedMemoryAwareThreadPoolExecutor(5, 1048576, 1048576));
	
	/** Instance logger */
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 * Creates a new HTTPDashku
	 * @param host The dashku server name or ip address
	 * @param port The dashku server listening port
	 * @param apiKey The dashku API key
	 */
	public HTTPDashku(String apiKey, String host, int port) {
		super(apiKey);
		channel = TCPConnector.getInstance().getSynchChannel(host, port);
		ChannelPipeline pipeline = channel.getPipeline();
		pipeline.addLast("execution", executionHandler);
		pipeline.addLast("codec", new HttpClientCodec());
		pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
		pipeline.addLast("synchreader", synchReader);		
	}
	
	
	/**
	 * Transmits the passed json object as a json body to the dashku server's transmission endpoint
	 * @param widgetId The id of the widget to send to if it is not already set
	 * @param json The json object to post
	 * 
	 */
	public void transmit(String widgetId, JsonObject json) {
		json.addProperty("_id", widgetId);
		DefaultHttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, String.format(URI_POST_TRANSMIT, apiKey));
		httpRequest.addHeader(HttpHeaders.Names.CONTENT_TYPE, "application/json");
		byte[] jsonContent = json.toString().getBytes();
		int contentLength = jsonContent.length;
		HttpHeaders.setContentLength(httpRequest, contentLength);
		ChannelBuffer cb = ChannelBuffers.directBuffer(contentLength);
		cb.writeBytes(jsonContent);
		httpRequest.setContent(cb);
		channel.write(httpRequest);				
	}




}
