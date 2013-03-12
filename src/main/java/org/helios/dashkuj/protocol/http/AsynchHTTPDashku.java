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

import org.helios.dashkuj.api.AsynchDashku;
import org.helios.dashkuj.api.DashkuAPIException;
import org.helios.dashkuj.domain.Dashboard;
import org.helios.dashkuj.domain.Transmission;
import org.helios.dashkuj.domain.Widget;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpVersion;

import com.google.gson.reflect.TypeToken;

/**
 * <p>Title: AsynchHTTPDashku</p>
 * <p>Description: An asynchronous {@link org.helios.dashkuj.api.Dashku} implementation using HTTP</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.protocol.http.AsynchHTTPDashku</code></p>
 */

public class AsynchHTTPDashku extends AbstractHTTPDashku implements AsynchDashku, ChannelUpstreamHandler   {

	/**
	 * Creates a new AsynchHTTPDashku
	 * @param host The dashku server name or ip address
	 * @param port The dashku server listening port
	 * @param apiKey The dashku API key
	 */
	protected AsynchHTTPDashku(String apiKey, String host, int port) {
		super(apiKey, host, port);
		pipeline.addLast("asynch-response-handler", this);
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.ChannelUpstreamHandler#handleUpstream(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelEvent)
	 */
	@Override
	public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
		if(e instanceof MessageEvent) {
			Object message = ((MessageEvent)e).getMessage();
			if(message instanceof HttpResponse) {
				HttpResponse response = (HttpResponse)message;
				try {
					
				} catch (Exception ex) {
					
				}
				return;
			}
		}
		ctx.sendUpstream(e);
		
	}
	

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#getDashboards(org.helios.dashkuj.api.AsynchDashku.DomainObjectListener)
	 */
	@Override
	public void getDashboards(DomainObjectListener<Collection<Dashboard>> responseListener) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#getDashboard(java.lang.CharSequence, org.helios.dashkuj.api.AsynchDashku.DomainObjectListener)
	 */
	@Override
	public void getDashboard(CharSequence dashboardId, DomainObjectListener<Dashboard> responseListener) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#createDashboard(org.helios.dashkuj.domain.Dashboard, org.helios.dashkuj.api.AsynchDashku.DomainObjectListener)
	 */
	@Override
	public void createDashboard(Dashboard dashboard, DomainObjectListener<Dashboard> responseListener) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#createDashboard(org.helios.dashkuj.domain.Dashboard)
	 */
	@Override
	public void createDashboard(Dashboard dashboard) {
		createDashboard(dashboard, null);
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#updateDashboard(org.helios.dashkuj.domain.Dashboard, org.helios.dashkuj.api.AsynchDashku.DomainObjectListener)
	 */
	@Override
	public void updateDashboard(Dashboard dashboard, DomainObjectListener<Dashboard> responseListener) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#updateDashboard(org.helios.dashkuj.domain.Dashboard)
	 */
	@Override
	public void updateDashboard(Dashboard dashboard) {
		updateDashboard(dashboard, null);
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#deleteDashboard(org.helios.dashkuj.domain.Dashboard, org.helios.dashkuj.api.AsynchDashku.DomainObjectListener)
	 */
	@Override
	public void deleteDashboard(Dashboard dashboard, DomainObjectListener<String> responseListener) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#deleteDashboard(org.helios.dashkuj.domain.Dashboard)
	 */
	@Override
	public void deleteDashboard(Dashboard dashboard) {
		deleteDashboard(dashboard, null);

	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#deleteDashboard(java.lang.CharSequence, org.helios.dashkuj.api.AsynchDashku.DomainObjectListener)
	 */
	@Override
	public void deleteDashboard(CharSequence dashboardId, DomainObjectListener<String> responseListener) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#deleteDashboard(java.lang.CharSequence)
	 */
	@Override
	public void deleteDashboard(CharSequence dashboardId) {
		deleteDashboard(dashboardId,  null);
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#createWidget(java.lang.CharSequence, org.helios.dashkuj.domain.Widget, org.helios.dashkuj.api.AsynchDashku.DomainObjectListener)
	 */
	@Override
	public void createWidget(CharSequence dashboardId, Widget widget, DomainObjectListener<Widget> responseListener) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#createWidget(java.lang.CharSequence, org.helios.dashkuj.domain.Widget)
	 */
	@Override
	public void createWidget(CharSequence dashboardId, Widget widget) {
		createWidget(dashboardId, widget, null);
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#updateWidget(java.lang.CharSequence, org.helios.dashkuj.domain.Widget, org.helios.dashkuj.api.AsynchDashku.DomainObjectListener)
	 */
	@Override
	public void updateWidget(CharSequence dashboardId, Widget widget, DomainObjectListener<Widget> responseListener) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#updateWidget(java.lang.CharSequence, org.helios.dashkuj.domain.Widget)
	 */
	@Override
	public void updateWidget(CharSequence dashboardId, Widget widget) {
		updateWidget(dashboardId, widget, null);

	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#deleteWidget(java.lang.CharSequence, java.lang.CharSequence, org.helios.dashkuj.api.AsynchDashku.DomainObjectListener)
	 */
	@Override
	public void deleteWidget(CharSequence dashboardId, CharSequence widgetId, DomainObjectListener<String> repsonseListener) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#deleteWidget(java.lang.CharSequence, java.lang.CharSequence)
	 */
	@Override
	public void deleteWidget(CharSequence dashboardId, CharSequence widgetId) {
		deleteWidget(dashboardId, widgetId, null);
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#transmit(org.helios.dashkuj.domain.Transmission[])
	 */
	@Override
	public void transmit(Transmission... transmissions) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#transmit(org.helios.dashkuj.api.AsynchDashku.AsynchErrorHandler, org.helios.dashkuj.domain.Transmission[])
	 */
	@Override
	public void transmit(AsynchErrorHandler errorListener, Transmission... transmissions) {
		transmit(null, transmissions);

	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#getResourceString(java.lang.CharSequence, org.helios.dashkuj.api.AsynchDashku.DomainObjectListener)
	 */
	@Override
	public void getResourceString(final CharSequence resourceUri, final DomainObjectListener<String> reponseListener) {
		if(resourceUri==null) {
			reponseListener.onAsynchError(this, "getResourceString", new Exception("The requested resourceUri was null"), resourceUri);
		}		
		channel.write(new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, resourceUri.toString()));
	}


	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.protocol.http.AbstractHTTPDashku#apiCall(com.google.gson.reflect.TypeToken, org.jboss.netty.channel.Channel, org.jboss.netty.handler.codec.http.HttpMethod, java.lang.String, java.lang.Object, java.lang.String, java.lang.Object[])
	 */
	@Override
	protected Object apiCall(TypeToken<?> type, final Channel channel, HttpMethod method, String opName, final Object payload, final String uri, final Object... uriFillIns) {
		final String[] domainHandlerNames = installDomainHandlers(type, channel.getPipeline());
		try {			
			channel.write(new DashkuHttpRequest(payload, HttpVersion.HTTP_1_1, method, uri, uriFillIns)).addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					channel.getPipeline().remove(domainHandlerNames[0]);
					
				}
			});
//			if(result==null) throw new DashkuAPIException(opName + " call returned null", new Throwable());
//			if(log.isDebugEnabled()) {
//				log.debug(opName + " returned  [{}->{}]", result.getClass().getName(), result);
//				if(result instanceof ChannelBuffer) {
//					byte[] bytes = ((ChannelBuffer)result).copy().array();
//					log.debug(opName + " returned content:>\n[{}]\n<", new String(bytes));
//				}
//			}
//			if(result instanceof Throwable) {
//				throw new DashkuAPIException(opName + " call exception", (Throwable)result);
//			}
//			return result;
		} catch (Exception ex) {
			throw new DashkuAPIException(opName + " call exception", ex);
		} 	
		return null;
	}


}
