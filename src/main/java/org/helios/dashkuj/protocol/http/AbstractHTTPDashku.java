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

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.helios.dashkuj.api.AbstractDashku;
import org.helios.dashkuj.domain.AbstractDashkuDomainObject;
import org.helios.dashkuj.domain.Dashboard;
import org.helios.dashkuj.domain.Status;
import org.helios.dashkuj.domain.Widget;
import org.helios.dashkuj.handlers.DashkuDecoder;
import org.helios.dashkuj.handlers.DashkuEncoder;
import org.helios.dashkuj.json.GsonFactory;
import org.helios.dashkuj.transport.TCPConnector;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.DownstreamMessageEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

/**
 * <p>Title: AbstractHTTPDashku</p>
 * <p>Description: Abstract base class for HTTP {@link org.helios.dashkuj.api.Dashku} and {@link org.helios.dashkuj.api.AsynchDashku}</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.protocol.http.AbstractHTTPDashku</code></p>
 */

public abstract class AbstractHTTPDashku implements ChannelDownstreamHandler  {
	/** The netty channel connecting to the dashku server */
	protected final Channel channel;
	/** The request timeout in ms. */
	protected long timeout = DEFAULT_REQUEST_TIMEOUT;
	/** The dashku server host */
	protected final String host;
	/** The dashku server port */
	protected final int port;
	/** The channel pipeline */
	protected final ChannelPipeline pipeline;
	/** The API key */
	protected final String apiKey;

	

	/** Shared execution handler */
	protected static final ExecutionHandler executionHandler = new ExecutionHandler(new OrderedMemoryAwareThreadPoolExecutor(5, 1048576, 1048576));
	/** Encoder for collections of dashboards */
	protected static final DashkuEncoder<Collection<Dashboard>> dashboardsEncoder = new DashkuEncoder<Collection<Dashboard>>(Dashboard.DASHBOARD_COLLECTION_TYPE);
	/** Encoder for dashboards */
	protected static final DashkuEncoder<Dashboard> dashboardEncoder = new DashkuEncoder<Dashboard>(Dashboard.DASHBOARD_TYPE);
	/** Encoder for dashboard Ids */
	protected static final DashkuEncoder<Status> statusEncoder = new DashkuEncoder<Status>(Dashboard.STATUS_TYPE); 
	
	/** Encoder for widgets */
	protected static final DashkuEncoder<Widget> widgetEncoder = new DashkuEncoder<Widget>(Dashboard.WIDGET_TYPE); 

	/** Decoder for collections of dashboards */
	protected static final DashkuDecoder<Collection<Dashboard>> dashboardsDecoder = new DashkuDecoder<Collection<Dashboard>>(Dashboard.DASHBOARD_COLLECTION_TYPE);
	/** Decoder for dashboards */
	protected static final DashkuDecoder<Dashboard> dashboardDecoder = new DashkuDecoder<Dashboard>(Dashboard.DASHBOARD_TYPE);
	/** Decoder for dashboard IDs */ 
	protected static final DashkuDecoder<Status> statusDecoder = new DashkuDecoder<Status>(Dashboard.STATUS_TYPE); 
	
	/** Decoder for widgets */
	protected static final DashkuDecoder<Widget> widgetDecoder = new DashkuDecoder<Widget>(Dashboard.WIDGET_TYPE); 
	
	/** Domain object encoder/decoder pairs keyed by the type token of the domain object */
	protected static final Map<TypeToken<?>, ChannelHandler[]> DOMAIN_HANDLERS;
	
	static {
		Map<TypeToken<?>, ChannelHandler[]> map = new HashMap<TypeToken<?>, ChannelHandler[]>(4);
		map.put(Dashboard.DASHBOARD_COLLECTION_TYPE, new ChannelHandler[]{dashboardsEncoder, dashboardsDecoder});
		map.put(Dashboard.DASHBOARD_TYPE, new ChannelHandler[]{dashboardEncoder, dashboardDecoder});
		map.put(Dashboard.WIDGET_TYPE, new ChannelHandler[]{widgetEncoder, widgetDecoder});
		map.put(Dashboard.STATUS_TYPE, new ChannelHandler[]{statusEncoder, statusDecoder});
		DOMAIN_HANDLERS = Collections.unmodifiableMap(map);
	}
	
	/** The default timeout in ms. */
	public static final long DEFAULT_REQUEST_TIMEOUT = 2000;
	
	/** Instance logger */
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	/** The name of the handler after which domain handlers should be inserted */
	static final String PIPELINE_INSERT = "execution";
	
	/** An HTTP codec */
	protected static final HttpClientCodec httpClientCodec = new HttpClientCodec(16384, 16384, 16384);
	/** An HTTP chunk aggregator */
	protected static final HttpChunkAggregator httpChunkAggregator = new HttpChunkAggregator(1048576);
	
	/** A UTF-8 charset for URL encoding */
	public static final Charset UTF8CS = Charset.forName("UTF-8");
	
	/**
	 * Creates a new HTTPDashku
	 * @param host The dashku server name or ip address
	 * @param port The dashku server listening port
	 * @param apiKey The dashku API key
	 */
	protected AbstractHTTPDashku(String apiKey, String host, int port) {
		if(apiKey==null || apiKey.trim().isEmpty()) throw new IllegalArgumentException("The passed APIKey was null", new Throwable());
		if(host==null || host.trim().isEmpty()) throw new IllegalArgumentException("The passed host was null", new Throwable());
		this.apiKey = apiKey;
		this.host = host;
		this.port = port;
		channel = TCPConnector.getInstance().getSynchChannel(host, port);
		pipeline = channel.getPipeline();
		pipeline.addLast("http-codec", httpClientCodec); 					// UP/DOWN
		pipeline.addLast("aggregator", httpChunkAggregator);				// UP ONLY
		pipeline.addLast("httpRequestBuilder", this);						// DOWN ONLY
		//-- domain DECODER here --//										// UP ONLY
		//-- domain ENCODER here --//										// DOWN ONLY
		pipeline.addLast("execution", executionHandler);					// UP/DOWN
		// SYNCH ONLY pipeline.addLast("synchreader", synchReader);						// UP ONLY		
	}
		
	/**
	 * Installs the domain encoder/decoder pair into the pipeline for the passed type
	 * @param type The type of the domain object to install the encoder/decoder pair for 
	 * @param pipeline The pipeline to install into
	 * @return The names of the installed channel handlers
	 */
	protected String[] installDomainHandlers(TypeToken<?> type, ChannelPipeline pipeline) {
		ChannelHandler[] handlers = DOMAIN_HANDLERS.get(type);
		String[] names = new String[]{type.toString() + "-decoder", type.toString() + "-encoder"}; 
		
		pipeline.addBefore(PIPELINE_INSERT, names[1], handlers[1]);
		pipeline.addBefore(PIPELINE_INSERT, names[0], handlers[0]);		
		log.debug("Installed Domain Handlers for {}", Arrays.toString(names));
		return names;
	}
	
	/**
	 * Removes the named channel handlers from the passed pipeline
	 * @param names The names of the channel handlers to remove
	 * @param pipeline The pipeline to remove from
	 */
	protected void removeDomainHandlers(String[] names, ChannelPipeline pipeline) {
		pipeline.remove(names[0]);
		pipeline.remove(names[1]);
	}
	
	
	/**
	 * Generic API invoker
	 * @param type The type token for the expected response type
	 * @param channel The channel connected to the dashku server
	 * @param method The HTTP method to use 
	 * @param opName The API method name for logging
	 * @param payload The optional payload to send as the http request content
	 * @param uri The URI template of the http request
	 * @param uriFillIns The values to fill in the uri template
	 * @return the returned value
	 */
	protected abstract Object apiCall(TypeToken<?> type, Channel channel, HttpMethod method, String opName, Object payload, String uri, Object...uriFillIns);
	
	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.ChannelDownstreamHandler#handleDownstream(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelEvent)
	 */
	@Override
	public void handleDownstream(ChannelHandlerContext ctx, final ChannelEvent e) throws Exception {
		if(e instanceof MessageEvent) {
			final MessageEvent me = (MessageEvent)e;			
			Object message = me.getMessage();
			if(message instanceof DashkuHttpRequest) {
				final DashkuHttpRequest request = (DashkuHttpRequest)message;
				HttpRequest httpRequest = request.getHttpRequest();
				
				//httpRequest.addHeader(HttpHeaders.Names.CONTENT_TYPE, "application/json");
				httpRequest.addHeader(HttpHeaders.Names.ACCEPT, "application/json");
				ChannelBuffer buff = request.getChannelBuffer();
				int contentLength = buff.readableBytes();
				if(contentLength>0) {					
					httpRequest.setContent(buff);
				}
				HttpHeaders.setContentLength(httpRequest, contentLength);
				Channel channel = e.getChannel();
				ChannelFuture cf = Channels.future(channel);
				cf.addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						if(future.isSuccess()) {
							log.debug("Successfully sent API request [{}/{}/{}] to [{}]", request.getVersion(), request.getMethod(), request.getUri(), me.getRemoteAddress());
						} else {
							log.error("Failed to send API request [{}/{}/{}] to [{}]", request.getVersion(), request.getMethod(), request.getUri(), me.getRemoteAddress(), future.getCause());
						}
					}
				});
				ctx.sendDownstream(new DownstreamMessageEvent(channel, cf, request, me.getRemoteAddress()));
				return;
			}
		}
		ctx.sendDownstream(e);	
	}
	
	/**
	 * Returns the current request timeout in ms.
	 * @return the current request timeout in ms.
	 */
	public long getTimeout() {
		return timeout;
	}

	/**
	 * Sets the request timeout in ms.
	 * @param timeout the request timeout in ms.
	 */
	public void setTimeout(long timeout) {
		this.timeout = timeout;
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
	 * Builds a post body in JSON format to send the dirty fields for the passed domain object
	 * @param domainObject the domain object to generate the diff post for
	 * @return the diff post body
	 */
	protected String buildDirtyUpdatePostJSON(AbstractDashkuDomainObject domainObject) {
		JsonObject jsonDomainObject = GsonFactory.getInstance().newGson().toJsonTree(domainObject).getAsJsonObject();
		JsonObject diffs = new JsonObject();
		for(String dirtyFieldName: domainObject.getDirtyFieldNames()) {
			diffs.add(dirtyFieldName, jsonDomainObject.get(dirtyFieldName));
		}
		return diffs.toString();
	}
	
	/**
	 * Builds a post body in post body format to send the dirty fields for the passed domain object.
	 * The field values are URL encoded. 
	 * @param domainObject the domain object to generate the diff post for
	 * @return the diff post body
	 */
	protected String buildDirtyUpdatePost(AbstractDashkuDomainObject domainObject) {
		StringBuilder b = new StringBuilder();
		JsonObject jsonDomainObject = GsonFactory.getInstance().newGson().toJsonTree(domainObject).getAsJsonObject();		
		for(String dirtyFieldName: domainObject.getDirtyFieldNames()) {
			try {
				String value = URLEncoder.encode(jsonDomainObject.get(dirtyFieldName).toString(), UTF8CS.name());
				b.append(dirtyFieldName).append("=").append(value).append("&");
			} catch (Exception ex) {
				throw new RuntimeException("Failed to encode dirty field [" + dirtyFieldName + "]", ex);
			}
		}
		return b.deleteCharAt(b.length()-1).toString();
	}
		
	
}
