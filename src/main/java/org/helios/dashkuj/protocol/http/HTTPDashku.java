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

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.helios.dashkuj.api.Dashku;
import org.helios.dashkuj.api.DashkuAPIException;
import org.helios.dashkuj.domain.Dashboard;
import org.helios.dashkuj.domain.Resource;
import org.helios.dashkuj.domain.Status;
import org.helios.dashkuj.domain.Transmission;
import org.helios.dashkuj.domain.Widget;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.queue.BlockingReadHandler;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

/**
 * <p>Title: HTTPDashku</p>
 * <p>Description: Synchronous Dashku API implementation over HTTP/JSON</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.protocol.http.HTTPDashku</code></p>
 */

public class HTTPDashku extends AbstractHTTPDashku implements Dashku {
	/** Synchronous invocation handler queue */
	protected final BlockingQueue<ChannelEvent> synchReaderQueue = new ArrayBlockingQueue<ChannelEvent>(100, false); 
	 /** Synchronous invocation handler */
	protected final BlockingReadHandler<Object> synchReader = new BlockingReadHandler<Object>(synchReaderQueue);
	
	/**
	 * Creates a new HTTPDashku
	 * @param host The dashku server name or ip address
	 * @param port The dashku server listening port
	 * @param apiKey The dashku API key
	 */
	public HTTPDashku(String apiKey, String host, int port) {
		super(apiKey, host, port);
		pipeline.addLast("synchreader", synchReader);						// UP ONLY		
	}
	

	
	
	
	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AbstractDashku#getDashboards()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Collection<Dashboard> getDashboards() {		
		synchReaderQueue.clear();
		Object response = apiCall(Dashboard.DASHBOARD_COLLECTION_TYPE, channel, HttpMethod.GET, "getDashboards", null, URI_GET_DASHBOARDS, apiKey);		
		return (Collection<Dashboard>)response;
	}	
	
	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#getDashboard(java.lang.CharSequence)
	 */
	@Override
	public Dashboard getDashboard(CharSequence dashboardId) {
		return (Dashboard) apiCall(Dashboard.DASHBOARD_TYPE, channel, HttpMethod.GET, "getDashboard", null, URI_GET_DASHBOARD, dashboardId, apiKey);
	}	
	
	@Override
	public Resource getResource(CharSequence resourceUri) {
		return null;
	}

	
	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#deleteDashboard(java.lang.CharSequence)
	 */
	@Override
	public String deleteDashboard(CharSequence dashboardId) {
		Status status = (Status)apiCall(Dashboard.STATUS_TYPE, channel, HttpMethod.DELETE, "deleteDashboard", null, URI_DELETE_DELETE_DASHBOARD, dashboardId, apiKey);
		return status.getDashboardId();
	}	
	
	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#deleteDashboard(org.helios.dashkuj.domain.Dashboard)
	 */
	@Override
	public String deleteDashboard(Dashboard dashboard) {
		return deleteDashboard(dashboard.getId());
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#deleteWidget(java.lang.CharSequence, java.lang.CharSequence)
	 */
	@Override
	public String deleteWidget(CharSequence dashboardId, CharSequence widgetId) {
		Status status = (Status)apiCall(Dashboard.STATUS_TYPE, channel, HttpMethod.DELETE, "deleteWidget", null, URI_DELETE_DELETE_WIDGET, dashboardId, widgetId, apiKey);
		log.info("Delete Widget Status {}", status);
		return status.getWidgetId();
	}
	
	
	/** 
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#createDashboard(org.helios.dashkuj.domain.Dashboard)
	 */
	@Override
	public String createDashboard(Dashboard dashboard) {
		String diffPost = buildDirtyUpdatePostJSON(dashboard);
		log.debug("Sending Dashboard Init Attrs:[{}]", diffPost);		
		Dashboard newd = (Dashboard) apiCall(Dashboard.DASHBOARD_TYPE, channel, HttpMethod.POST, "createDashboard", diffPost, URI_POST_CREATE_DASHBOARD, apiKey);
		dashboard.updateFrom(newd);
		dashboard.clearDirtyFields();
		return newd.getId();
	}	
	
	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#createWidget(java.lang.CharSequence, org.helios.dashkuj.domain.Widget)
	 */
	@Override
	public String createWidget(CharSequence dashboardId, Widget widget) {
		String diffPost = buildDirtyUpdatePost(widget);
		log.debug("Sending Widget Init Attrs:[{}]", diffPost);		
		Widget newWidget = (Widget) apiCall(Widget.WIDGET_TYPE, channel, HttpMethod.POST, "createWidget", diffPost, URI_POST_CREATE_WIDGET, dashboardId, apiKey);
		widget.updateFrom(newWidget, dashboardId.toString());
		return newWidget.getId();
	}
	
	
	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#updateWidget(java.lang.CharSequence, org.helios.dashkuj.domain.Widget)
	 */
	@Override
	public Widget updateWidget(CharSequence dashboardId, Widget widget) {
		if(!widget.isDirty()) return widget;
		String diffPost = buildDirtyUpdatePostJSON(widget);
		log.debug("Sending Widget Diffs:[{}]", diffPost);
		apiCall(Widget.WIDGET_TYPE, channel, HttpMethod.PUT, "updateWidget", diffPost, URI_PUT_UPDATE_WIDGET, dashboardId, widget.getId(), apiKey);
		log.debug("Widget updated:[{}/{}]", widget.getName(), widget.getId());
		widget.clearDirtyFields();
		return widget;
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#updateDashboard(org.helios.dashkuj.domain.Dashboard)
	 */
	@Override
	public void updateDashboard(Dashboard dashboard) {
		if(!dashboard.isDirty()) return;
		String diffPost = buildDirtyUpdatePostJSON(dashboard);
		log.debug("Sending Dashboard Diffs:[{}]", diffPost);
		Dashboard db = (Dashboard) apiCall(Dashboard.DASHBOARD_TYPE, channel, HttpMethod.PUT, "updateDashboard", diffPost, URI_PUT_UPDATE_DASHBOARD, dashboard.getId(), apiKey);
		log.debug("Dashboard updated:[{}/{}]", dashboard.getName(), dashboard.getId());
		dashboard.updateFrom(db);
		dashboard.clearDirtyFields();
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
	@Override
	protected Object apiCall(TypeToken<?> type, Channel channel, HttpMethod method, String opName, Object payload, String uri, Object...uriFillIns) {		
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
		}		
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#transmit(org.helios.dashkuj.domain.Transmission[])
	 */
	@Override
	public void transmit(Transmission... transmissions) {
		
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
		Object result;
		try {
			result = synchReader.read(timeout, TimeUnit.MILLISECONDS);
			if(result==null) throw new DashkuAPIException("transmit call returned null", new Throwable());
			log.info("Transmit Result [{}]", result);
		} catch (Exception e) {
			throw new DashkuAPIException("transmit call returned null", e);
		}
	}


}
