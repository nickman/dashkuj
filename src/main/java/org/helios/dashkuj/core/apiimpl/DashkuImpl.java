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

import java.io.InputStreamReader;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import org.helios.dashkuj.api.Dashku;
import org.helios.dashkuj.domain.Dashboard;
import org.helios.dashkuj.domain.DashboardId;
import org.helios.dashkuj.domain.DomainUnmarshaller;
import org.helios.dashkuj.domain.Transmission;
import org.helios.dashkuj.domain.Widget;
import org.helios.dashkuj.json.GsonFactory;
import org.jboss.netty.buffer.ChannelBufferInputStream;
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

public class DashkuImpl extends AbstractDashku implements Dashku {

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
		SynchronousResponse<Collection<Dashboard>> responder = newSynchronousResponse(Dashboard.DASHBOARD_COLLECTION_UNMARSHALLER);
		client.get(String.format(URI_GET_DASHBOARDS, apiKey), responder).setTimeout(timeout).end();
		return responder.getResponse();
	}
	
	
	
	/**
	 * <p>Title: SynchronousResponse</p>
	 * <p>Description: A future for making the http request handling synchronous</p> 
	 * <p>Company: Helios Development Group LLC</p>
	 * @author Whitehead (nwhitehead AT heliosdev DOT org)
	 * <p><code>org.helios.dashkuj.core.apiimpl.DashkuImpl.SynchronousResponse</code></p>
	 * @param <T> The expected type of the response
	 */
	protected class SynchronousResponse<T> implements Future<T>, Handler<HttpClientResponse> {
		/** The completion latch */
		protected final CountDownLatch latch = new CountDownLatch(1);
		/** The domain unmarshaller */
		protected final DomainUnmarshaller<T> unmarshaller;
		/** The final return value */
		protected final AtomicReference<T> result = new AtomicReference<T>(null); 
		/** The exception result */
		protected final AtomicReference<Exception> ex = new AtomicReference<Exception>(null); 
		
		/**
		 * Creates a new SynchronousResponse with no unmarshaller
		 */
		protected SynchronousResponse() {			
			this.unmarshaller = null;			
		}		
		
		
		/**
		 * Creates a new SynchronousResponse
		 * @param unmarshaller The domain unmarshaller
		 */
		protected SynchronousResponse(DomainUnmarshaller<T> unmarshaller) {			
			this.unmarshaller = unmarshaller;			
		}
		
		
		
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
			final Handler<Exception> exceptionHandler = new Handler<Exception>() {
				@Override
				public void handle(Exception exEvent) {
					ex.set(exEvent);
					latch.countDown();					
				}
			};
			event.exceptionHandler(exceptionHandler);
			event.dataHandler(new Handler<Buffer>(){
				@Override
				public void handle(Buffer dataEvent) {
					log.debug("Calling dataHandler:{} Bytes", dataEvent.length()); 
					content.appendBuffer(dataEvent);				
				}
			});
			event.endHandler(new Handler<Void>(){
				@SuppressWarnings("unchecked")
				@Override
				public void handle(Void endEvent) {
					if(log.isDebugEnabled()) {
						log.debug("Calling endHandler:{} Bytes", content.length());
						log.info("Response:[{}]", render(event, content));
					}
					try {
						if(unmarshaller!=null) {
							result.set(unmarshaller.unmarshall(content));
						} else {
							result.set((T)content);
						}
						latch.countDown();
					} catch (Exception ex) {
						exceptionHandler.handle(ex);
					}
				}
			});
			
		}
		
		
		/**
		 * <p>No Op</p>
		 * {@inheritDoc}
		 * @see java.util.concurrent.Future#cancel(boolean)
		 */
		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			// TODO Auto-generated method stub
			return false;
		}

		/**
		 * <p>Always returns false</p>
		 * {@inheritDoc}
		 * @see java.util.concurrent.Future#isCancelled()
		 */
		@Override
		public boolean isCancelled() {
			return false;
		}

		/**
		 * {@inheritDoc}
		 * @see java.util.concurrent.Future#isDone()
		 */
		@Override
		public boolean isDone() {
			return latch.getCount()<1;
		}
		
		/**
		 * Returns the [unmarshalled] http request response
		 * @return the http request response
		 */
		public T getResponse() {
			try {
				return get();
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}

		/**
		 * {@inheritDoc}
		 * @see java.util.concurrent.Future#get()
		 */
		@Override
		public T get() throws InterruptedException, ExecutionException {
			latch.await();
			Exception exx = ex.get();
			if(exx!=null) {
				throw new RuntimeException("Request execution failed", exx);
			}
			return result.get();
		}

		/**
		 * {@inheritDoc}
		 * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
		 */
		@Override
		public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
			boolean ok = latch.await(timeout, unit);
			if(!ok) throw new TimeoutException("Request timed out");
			Exception exx = ex.get();
			if(exx!=null) {
				throw new RuntimeException("Request execution failed", exx);
			}
			return result.get();
		}
		
	}
	
	
	/**
	 * Creates a new SynchronousResponse
	 * @param unmarshaller The domain unmarshaller
	 * @param <T> The expected type of the response
	 * @return The new SynchronousResponse
	 */
	protected <T> SynchronousResponse<T> newSynchronousResponse(DomainUnmarshaller<T> unmarshaller) {
		return new SynchronousResponse<T>(unmarshaller);
	}
	
	/**
	 * Creates a new SynchronousResponse with no unmarshaller (meaning the type we're expecting is a {@link Buffer}.
	 * @param <T> The expected type of the response
	 * @return The new SynchronousResponse
	 */
	protected <T> SynchronousResponse<T> newSynchronousResponse() {
		return new SynchronousResponse<T>();
	}
	
	
//	protected Buffer waitForResponse(HttpClientRequest request) {
//		
//	}
	

	

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#getDashboard(java.lang.CharSequence)
	 */
	@Override
	public Dashboard getDashboard(CharSequence dashboardId) {
		log.debug("Calling getDashboard()");
		SynchronousResponse<Dashboard> responder = newSynchronousResponse(Dashboard.DASHBOARD_UNMARSHALLER);
		client.get(String.format(URI_GET_DASHBOARD, dashboardId, apiKey), responder).setTimeout(timeout).end();
		return responder.getResponse();

	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#createDashboard(org.helios.dashkuj.domain.Dashboard)
	 */
	@Override
	public String createDashboard(Dashboard dashboard) {
		log.debug("Calling createDashboard()");
		SynchronousResponse<Dashboard> responder = newSynchronousResponse(Dashboard.DASHBOARD_UNMARSHALLER);
		completeRequest(buildDirtyUpdatePostJSON(dashboard), client.post(String.format(URI_POST_CREATE_DASHBOARD, apiKey), responder));			
		Dashboard newd = responder.getResponse();
		dashboard.updateFrom(newd);
		return newd.getId();
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#updateDashboard(org.helios.dashkuj.domain.Dashboard)
	 */
	@Override
	public void updateDashboard(Dashboard dashboard) {
		log.debug("Calling updateDashboard()");
		SynchronousResponse<Dashboard> responder = newSynchronousResponse(Dashboard.DASHBOARD_UNMARSHALLER);
		completeRequest(buildDirtyUpdatePostJSON(dashboard), client.post(String.format(URI_PUT_UPDATE_DASHBOARD, dashboard.getId(), apiKey), responder));			
		Dashboard newd = responder.getResponse();
		dashboard.updateFrom(newd);
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#deleteDashboard(org.helios.dashkuj.domain.Dashboard)
	 */
	@Override
	public String deleteDashboard(Dashboard dashboard) {
		log.debug("Calling deleteDashboard()");
		SynchronousResponse<DashboardId> responder = newSynchronousResponse(Dashboard.DASHBOARD_ID_UNMARSHALLER);
		completeRequest(buildDirtyUpdatePostJSON(dashboard), client.delete(String.format(URI_DELETE_DELETE_DASHBOARD, dashboard.getId(), apiKey), responder));			
		return responder.getResponse().getDashboardId();
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
