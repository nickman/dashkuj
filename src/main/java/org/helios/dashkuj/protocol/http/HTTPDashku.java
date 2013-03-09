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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.helios.dashkuj.api.AbstractDashku;
import org.helios.dashkuj.api.DashkuAPIException;
import org.helios.dashkuj.domain.Dashboard;
import org.helios.dashkuj.domain.Widget;
import org.helios.dashkuj.handlers.DashkuDecoder;
import org.helios.dashkuj.handlers.DashkuEncoder;
import org.helios.dashkuj.transport.TCPConnector;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
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
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;
import org.jboss.netty.handler.queue.BlockingReadHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

/**
 * <p>Title: HTTPDashku</p>
 * <p>Description: Synchronous Dashku API implementation over HTTP/JSON</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.protocol.http.HTTPDashku</code></p>
 */

public class HTTPDashku extends AbstractDashku implements ChannelDownstreamHandler  {
	/** The netty channel connecting to the dashku server */
	protected final Channel channel;
	/** The request timeout in ms. */
	protected long timeout = DEFAULT_REQUEST_TIMEOUT;
	
	 /** Synchronous invication handler */
	protected final BlockingReadHandler<Object> synchReader = new BlockingReadHandler<Object>();
	
	/** Shared execution handler */
	private static final ExecutionHandler executionHandler = new ExecutionHandler(new OrderedMemoryAwareThreadPoolExecutor(5, 1048576, 1048576));
	/** Encoder for collections of dashboards */
	protected static final DashkuEncoder<Collection<Dashboard>> dashboardsEncoder = new DashkuEncoder<Collection<Dashboard>>(Dashboard.DASHBOARD_COLLECTION_TYPE);
	/** Encoder for dashboards */
	protected static final DashkuEncoder<Dashboard> dashboardEncoder = new DashkuEncoder<Dashboard>(Dashboard.DASHBOARD_TYPE); 
	/** Encoder for widgets */
	protected static final DashkuEncoder<Widget> widgetEncoder = new DashkuEncoder<Widget>(Dashboard.WIDGET_TYPE); 

	/** Decoder for collections of dashboards */
	protected static final DashkuDecoder<Collection<Dashboard>> dashboardsDecoder = new DashkuDecoder<Collection<Dashboard>>(Dashboard.DASHBOARD_COLLECTION_TYPE);
	/** Decoder for dashboards */
	protected static final DashkuDecoder<Dashboard> dashboardDecoder = new DashkuDecoder<Dashboard>(Dashboard.DASHBOARD_TYPE); 
	/** Decoder for widgets */
	protected static final DashkuDecoder<Widget> widgetDecoder = new DashkuDecoder<Widget>(Dashboard.WIDGET_TYPE); 
	
	/** Domain object encoder/decoder pairs keyed by the type token of the domain object */
	protected static final Map<TypeToken<?>, ChannelHandler[]> DOMAIN_HANDLERS;
	
	static {
		Map<TypeToken<?>, ChannelHandler[]> map = new HashMap<TypeToken<?>, ChannelHandler[]>(3);
		map.put(Dashboard.DASHBOARD_COLLECTION_TYPE, new ChannelHandler[]{dashboardsEncoder, dashboardsDecoder});
		map.put(Dashboard.DASHBOARD_TYPE, new ChannelHandler[]{dashboardEncoder, dashboardDecoder});
		map.put(Dashboard.WIDGET_TYPE, new ChannelHandler[]{widgetEncoder, widgetDecoder});
		DOMAIN_HANDLERS = Collections.unmodifiableMap(map);
	}

	
	/** The default timeout in ms. */
	public static final long DEFAULT_REQUEST_TIMEOUT = 2000;
	
	/** Instance logger */
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	/** The name of the handler after which domain handlers should be inserted */
	static final String PIPELINE_INSERT = "codec";
	
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
		pipeline.addLast(PIPELINE_INSERT, new HttpClientCodec()); 			// UP/DOWN		
		//-- domain DECODER here --//										// UP ONLY
		//-- domain ENCODER here --//										// DOWN ONLY
		pipeline.addLast("httpRequestBuilder", this);						// DOWN ONLY
		pipeline.addLast("execution", executionHandler);					// UP/DOWN
		pipeline.addLast("synchreader", synchReader);						// UP ONLY		
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
		pipeline.addAfter(PIPELINE_INSERT, names[1], handlers[1]);
		pipeline.addAfter(PIPELINE_INSERT, names[0], handlers[0]);		
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
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AbstractDashku#getDashboards()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Collection<Dashboard> getDashboards() {
		return (Collection<Dashboard>) apiCall(Dashboard.DASHBOARD_COLLECTION_TYPE, channel, HttpMethod.GET, "getDashboards", null, URI_GET_DASHBOARDS, apiKey);
	}	
	
	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#getDashboard(java.lang.CharSequence)
	 */
	@Override
	public Dashboard getDashboard(CharSequence dashboardId) {
		return (Dashboard) apiCall(Dashboard.DASHBOARD_TYPE, channel, HttpMethod.GET, "getDashboard", null, URI_GET_DASHBOARD, dashboardId, apiKey);
	}	
	
	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#updateWidget(java.lang.CharSequence, org.helios.dashkuj.domain.Widget)
	 */
	@Override
	public Widget updateWidget(CharSequence dashboardId, Widget widget) {
		return (Widget) apiCall(Dashboard.WIDGET_TYPE, channel, HttpMethod.PUT, "updateWidget", widget, URI_PUT_UPDATE_WIDGET, dashboardId, widget.getId(), apiKey);
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
	protected Object apiCall(TypeToken<?> type, Channel channel, HttpMethod method, String opName, Object payload, String uri, Object...uriFillIns) {
		final String[] domainHandlerNames = installDomainHandlers(type, channel.getPipeline());
		try {			
			channel.write(new DashkuHttpRequest(payload, HttpVersion.HTTP_1_1, method, uri, uriFillIns));
			Object result = synchReader.read(timeout, TimeUnit.MILLISECONDS);
			if(result==null) throw new DashkuAPIException(opName + " call returned null", new Throwable());
			if(log.isDebugEnabled()) {
				log.debug(opName + " returned  [{}->{}]", result.getClass().getName(), result);
				if(result instanceof ChannelBuffer) {
					byte[] bytes = ((ChannelBuffer)result).copy().array();
					log.debug(opName + " returned content:>\n[{}]\n<", new String(bytes));
				}
			}
			if(result instanceof Throwable) {
				throw new DashkuAPIException(opName + " call exception", (Throwable)result);
			}
			return result;
		} catch (Exception ex) {
			throw new DashkuAPIException(opName + " call exception", ex);
		} finally {
			removeDomainHandlers(domainHandlerNames, channel.getPipeline());
		}		
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
				ctx.sendDownstream(new DownstreamMessageEvent(channel, cf, httpRequest, me.getRemoteAddress()));
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




}
