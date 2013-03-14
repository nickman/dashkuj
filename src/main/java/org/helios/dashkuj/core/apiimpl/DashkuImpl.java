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

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.helios.dashkuj.api.Dashku;
import org.helios.dashkuj.domain.Dashboard;
import org.helios.dashkuj.domain.Transmission;
import org.helios.dashkuj.domain.Widget;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpClientResponse;

/**
 * <p>Title: DashkuImpl</p>
 * <p>Description: A synchronous Dashku API implementation</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.core.apiimpl.DashkuImpl</code></p>
 */

public class DashkuImpl extends AbstractDashku implements Dashku, Handler<HttpClientResponse> {

	/**
	 * Creates a new DashkuImpl
	 * @param client The http client for connecting to the dashku server
	 * @param apiKey The dashku api key
	 * @param host The dashku server host or ip address
	 * @param port The dashku server port
	 */
	public DashkuImpl(HttpClient client, String apiKey, String host, int port) {
		super(client, apiKey, host, port);
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#getDashboards()
	 */
	@Override
	public Collection<Dashboard> getDashboards() {
		log.debug("Calling getDashboards()");
		client.get(String.format(URI_GET_DASHBOARDS, apiKey), this).setTimeout(timeout).end();
		return null;
	}
	
	protected class SynchronousResponse<T> implements Future<T> {
		protected final CountDownLatch latch = new CountDownLatch(1);
		protected final HttpClientRequest request;
		protected final AtomicBoolean sent = new AtomicBoolean(false);
		
		/**
		 * {@inheritDoc}
		 * @see java.util.concurrent.Future#cancel(boolean)
		 */
		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			// TODO Auto-generated method stub
			return false;
		}

		/**
		 * {@inheritDoc}
		 * @see java.util.concurrent.Future#isCancelled()
		 */
		@Override
		public boolean isCancelled() {
			// TODO Auto-generated method stub
			return false;
		}

		/**
		 * {@inheritDoc}
		 * @see java.util.concurrent.Future#isDone()
		 */
		@Override
		public boolean isDone() {
			// TODO Auto-generated method stub
			return false;
		}

		/**
		 * {@inheritDoc}
		 * @see java.util.concurrent.Future#get()
		 */
		@Override
		public T get() throws InterruptedException, ExecutionException {
			// TODO Auto-generated method stub
			return null;
		}

		/**
		 * {@inheritDoc}
		 * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
		 */
		@Override
		public T get(long timeout, TimeUnit unit) throws InterruptedException,
				ExecutionException, TimeoutException {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
//	protected Buffer waitForResponse(HttpClientRequest request) {
//		
//	}
	
	/**
	 * {@inheritDoc}
	 * @see org.vertx.java.core.Handler#handle(java.lang.Object)
	 */
	@Override
	public void handle(final HttpClientResponse event) {
		log.debug("Calling handle(HttpClientResponse)");
		int bufferSize;
		String cs = event.headers().get(HttpHeaders.Names.CONTENT_LENGTH);
		if(cs==null || cs.trim().isEmpty()) {
			bufferSize = 1024;
		} else {
			try { bufferSize = Integer.parseInt(cs.trim()); } catch (Exception ex) { bufferSize = 1024; }
		}
		final Buffer content = new Buffer(bufferSize);
		event.dataHandler(new Handler<Buffer>(){
			@Override
			public void handle(Buffer dataEvent) {
				log.debug("Calling dataHandler:{} Bytes", dataEvent.length()); 
				content.appendBuffer(dataEvent);				
			}
		});
		event.endHandler(new Handler<Void>(){
			@Override
			public void handle(Void endEvent) {
				if(log.isDebugEnabled()) {
					log.debug("Calling endHandler:{} Bytes", content.length());
					log.info("Response:[{}]", render(event, content));
				}
			}
		});
	}
	

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#getDashboard(java.lang.CharSequence)
	 */
	@Override
	public Dashboard getDashboard(CharSequence dashboardId) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#createDashboard(org.helios.dashkuj.domain.Dashboard)
	 */
	@Override
	public String createDashboard(Dashboard dashboard) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#updateDashboard(org.helios.dashkuj.domain.Dashboard)
	 */
	@Override
	public void updateDashboard(Dashboard dashboard) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#deleteDashboard(org.helios.dashkuj.domain.Dashboard)
	 */
	@Override
	public String deleteDashboard(Dashboard dashboard) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#deleteDashboard(java.lang.CharSequence)
	 */
	@Override
	public String deleteDashboard(CharSequence dashboardId) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#createWidget(java.lang.CharSequence, org.helios.dashkuj.domain.Widget)
	 */
	@Override
	public String createWidget(CharSequence dashboardId, Widget widget) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#updateWidget(java.lang.CharSequence, org.helios.dashkuj.domain.Widget)
	 */
	@Override
	public Widget updateWidget(CharSequence dashboardId, Widget widget) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#deleteWidget(java.lang.CharSequence, java.lang.CharSequence)
	 */
	@Override
	public String deleteWidget(CharSequence dashboardId, CharSequence widgetId) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#transmit(org.helios.dashkuj.domain.Transmission[])
	 */
	@Override
	public void transmit(Transmission... transmissions) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#getResourceString(java.lang.CharSequence)
	 */
	@Override
	public String getResourceString(CharSequence resourceUri) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#getHost()
	 */
	@Override
	public String getHost() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#getPort()
	 */
	@Override
	public int getPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#getApiKey()
	 */
	@Override
	public String getApiKey() {
		// TODO Auto-generated method stub
		return null;
	}

	
	

}
