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

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import org.helios.dashkuj.api.AsynchDashku;
import org.helios.dashkuj.api.SynchDashku;
import org.helios.dashkuj.core.Dashkuj;
import org.helios.dashkuj.domain.Dashboard;
import org.helios.dashkuj.domain.DomainUnmarshaller;
import org.helios.dashkuj.domain.Resource;
import org.helios.dashkuj.domain.Status;
import org.helios.dashkuj.domain.Transmission;
import org.helios.dashkuj.domain.TransmissionScriptType;
import org.helios.dashkuj.domain.Widget;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientResponse;

/**
 * <p>Title: SynchDashkuImpl</p>
 * <p>Description: A synchronous Dashku API implementation</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.core.apiimpl.SynchDashkuImpl</code></p>
 */

public class SynchDashkuImpl extends AbstractDashku implements SynchDashku {

	/**
	 * Creates a new SynchDashkuImpl
	 * @param client The http client for connecting to the dashku server
	 * @param apiKey The dashku api key
	 * @param host The dashku server host or ip address
	 * @param port The dashku server port
	 */
	public SynchDashkuImpl(HttpClient client, String apiKey, String host, int port) {
		super(client, apiKey, host, port);
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.SynchDashku#getAsynchDashku()
	 */
	@Override
	public AsynchDashku getAsynchDashku() {
		return Dashkuj.getInstance().getAsynchDashku(apiKey, host, port);
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.SynchDashku#getDashboards()
	 */
	@Override
	public Collection<Dashboard> getDashboards() {
		log.debug("Calling getDashboards()");
		SynchronousResponse<Collection<Dashboard>> responder = newSynchronousResponse(Dashboard.DASHBOARD_COLLECTION_TYPE.getType(), Dashboard.DASHBOARD_COLLECTION_UNMARSHALLER);
		client.get(String.format(URI_GET_DASHBOARDS, apiKey), responder).setTimeout(timeout).end();
		return repository.synch(responder.getResponse());
	}
	
	
	
	/**
	 * <p>Title: SynchronousResponse</p>
	 * <p>Description: A future for making the http request handling synchronous</p> 
	 * <p>Company: Helios Development Group LLC</p>
	 * @author Whitehead (nwhitehead AT heliosdev DOT org)
	 * <p><code>org.helios.dashkuj.core.apiimpl.SynchDashkuImpl.SynchronousResponse</code></p>
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
		/** The actual type we expect the response to be */
		protected final Type responseType;
		
		/** The content type */
		protected String contentType = null;
		
		/**
		 * Creates a new SynchronousResponse with no unmarshaller
		 * @param responseType The actual type we expect the response to be
		 */
		protected SynchronousResponse(Type responseType) {			
			this.responseType = responseType;
			this.unmarshaller = null;			
		}		
		
		
		/**
		 * Creates a new SynchronousResponse
		 * @param responseType The actual type we expect the response to be
		 * @param unmarshaller The domain unmarshaller
		 */
		protected SynchronousResponse(Type responseType, DomainUnmarshaller<T> unmarshaller) {
			this.responseType = responseType;
			this.unmarshaller = unmarshaller;					
		}
		
		
		
		/**
		 * {@inheritDoc}
		 * @see org.vertx.java.core.Handler#handle(java.lang.Object)
		 */
		@Override
		public void handle(final HttpClientResponse event) {
			log.debug("Calling handle(HttpClientResponse)");
			int bufferSize;
			String cs = event.headers().get(HttpHeaders.Names.CONTENT_LENGTH);
			contentType = event.headers().get(HttpHeaders.Names.CONTENT_TYPE);
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
						log.debug("Response:[{}]", render(event, content));
					}
					try {
						if(Status.containsStatus(content)) {
							log.info("Detected Status Pattern in Response Buffer [{}]", render(event, content));
							Status status = Status.STATUS_UNMARSHALLER.unmarshall(content);
							if(responseType.equals(Status.class)) {
								result.set((T) status);
							} else {
								exceptionHandler.handle(status.getException());
							}
							latch.countDown();
							return;
						}
						if(unmarshaller!=null) {
							result.set(unmarshaller.unmarshall(content));
						} else {
							if(Resource.class.equals(responseType)) {
								result.set((T)new Resource(content.getBytes(), contentType));
							} else {
								result.set((T)content);
							}
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
	 * @param responseType The actual type we expect the response to be
	 * @param unmarshaller The domain unmarshaller
	 * @param <T> The expected type of the response
	 * @return The new SynchronousResponse
	 */
	protected <T> SynchronousResponse<T> newSynchronousResponse(Type responseType, DomainUnmarshaller<T> unmarshaller) {
		return new SynchronousResponse<T>(responseType, unmarshaller);
	}
	
	/**
	 * Creates a new SynchronousResponse with no unmarshaller (meaning the type we're expecting is a {@link Buffer}.
	 * @param responseType The actual type we expect the response to be
	 * @param <T> The expected type of the response
	 * @return The new SynchronousResponse
	 */
	protected <T> SynchronousResponse<T> newSynchronousResponse(Type responseType) {
		return new SynchronousResponse<T>(responseType);
	}


	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.SynchDashku#getDashboard(java.lang.CharSequence)
	 */
	@Override
	public Dashboard getDashboard(CharSequence dashboardId) {
		log.debug("Calling getDashboard({})", dashboardId);
		SynchronousResponse<Dashboard> responder = newSynchronousResponse(Dashboard.DASHBOARD_TYPE.getType(), Dashboard.DASHBOARD_UNMARSHALLER);
		log.debug("getDashboard URI:[{}]", String.format(URI_GET_DASHBOARD, dashboardId, apiKey));
		client.get(String.format(URI_GET_DASHBOARD, dashboardId, apiKey), responder).setTimeout(timeout).end();
		return repository.synch(responder.getResponse());

	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.SynchDashku#createDashboard(org.helios.dashkuj.domain.Dashboard)
	 */
	@Override
	public String createDashboard(Dashboard dashboard) {
		log.debug("Calling createDashboard()");
		SynchronousResponse<Dashboard> responder = newSynchronousResponse(Dashboard.DASHBOARD_TYPE.getType(), Dashboard.DASHBOARD_UNMARSHALLER);
		if(log.isDebugEnabled()) {
			log.debug("createDashboard POST [\n{}\n]", String.format(URI_POST_CREATE_DASHBOARD, apiKey));
		}
		completeRequest(buildDirtyUpdatePostJSON(dashboard), client.post(String.format(URI_POST_CREATE_DASHBOARD, apiKey), responder));					
		return repository.synch(dashboard.updateFrom(responder.getResponse())).getId();
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.SynchDashku#updateDashboard(org.helios.dashkuj.domain.Dashboard)
	 */
	@Override
	public void updateDashboard(Dashboard dashboard) {
		log.debug("Calling updateDashboard()");
		SynchronousResponse<Dashboard> responder = newSynchronousResponse(Dashboard.DASHBOARD_TYPE.getType(), Dashboard.DASHBOARD_UNMARSHALLER);
		completeRequest(
				buildDirtyUpdatePostJSON(dashboard), 
				client.put(String.format(URI_PUT_UPDATE_DASHBOARD, dashboard.getId(), apiKey), responder)
					.putHeader(HttpHeaders.Names.ACCEPT, JSON_CONTENT_TYPE)
		);					
		repository.synch(dashboard.updateFrom(responder.getResponse()));
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.SynchDashku#deleteDashboard(org.helios.dashkuj.domain.Dashboard)
	 */
	@Override
	public String deleteDashboard(Dashboard dashboard) {
		return repository.deleteDashboard(deleteDashboard(dashboard.getId()));
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.SynchDashku#deleteDashboard(java.lang.CharSequence)
	 */
	@Override
	public String deleteDashboard(CharSequence dashboardId) {
		log.debug("Calling deleteDashboard()");
		SynchronousResponse<Status> responder = newSynchronousResponse(Status.STATUS_TYPE.getType(), Status.STATUS_UNMARSHALLER);
		completeRequest(client.delete(String.format(URI_DELETE_DELETE_DASHBOARD, dashboardId, apiKey), responder));			
		return repository.deleteDashboard(responder.getResponse().getDashboardId());
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.SynchDashku#createWidget(java.lang.CharSequence, org.helios.dashkuj.domain.Widget)
	 */
	@Override
	public String createWidget(CharSequence dashboardId, Widget widget) {
		log.debug("Calling createWidget()");
		SynchronousResponse<Widget> responder = newSynchronousResponse(Widget.WIDGET_ID_TYPE.getType(), Widget.WIDGET_UNMARSHALLER);
		completeRequest(buildDirtyUpdatePostJSON(widget), JSON_CONTENT_TYPE,  client.post(String.format(URI_POST_CREATE_WIDGET, dashboardId, apiKey), responder));
		widget.updateFrom(responder.getResponse(), dashboardId.toString()).getId();
		widget.updateTransmissionScript(TransmissionScriptType.DEFAULT_TYPE, this);
		return repository.synch(widget).getId();
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.SynchDashku#updateWidget(java.lang.CharSequence, org.helios.dashkuj.domain.Widget)
	 */
	@Override
	public Widget updateWidget(CharSequence dashboardId, Widget widget) {
		log.debug("Calling updateWidget()");
		SynchronousResponse<Widget> responder = newSynchronousResponse(Widget.WIDGET_ID_TYPE.getType(), Widget.WIDGET_UNMARSHALLER);
		completeRequest(buildDirtyUpdatePostJSON(widget), JSON_CONTENT_TYPE,  client.put(String.format(URI_PUT_UPDATE_WIDGET, dashboardId, widget.getId(), apiKey), responder));
		widget.updateFrom(responder.getResponse(), dashboardId.toString());
		widget.updateTransmissionScript(TransmissionScriptType.DEFAULT_TYPE, this);
		return repository.synch(widget);
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.SynchDashku#deleteWidget(java.lang.CharSequence, java.lang.CharSequence)
	 */
	@Override
	public String deleteWidget(CharSequence dashboardId, CharSequence widgetId) {
		log.debug("Calling deleteWidget({}, {})", dashboardId, widgetId);
		SynchronousResponse<Status> responder = newSynchronousResponse(Status.STATUS_TYPE.getType(), Status.STATUS_UNMARSHALLER);
		completeRequest(client.delete(String.format(URI_DELETE_DELETE_WIDGET, dashboardId, widgetId, apiKey), responder));
		return repository.deleteWidget(responder.getResponse().getWidgetId());
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.SynchDashku#transmit(org.helios.dashkuj.domain.Transmission[])
	 */
	@Override
	public void transmit(Transmission... transmissions) {
		// TODO Auto-generated method stub

	}
	
	
	/**
	 * NodeJS fiendly signature data post
	 * @param uri The URI to post to
	 * @param data The data to post in JSON format
	 * @return The returned status
	 */
	@Override
	public Status post(String uri, String data) {
		log.debug("Calling post({},{})", uri, data);
		SynchronousResponse<Status> responder = newSynchronousResponse(Status.STATUS_TYPE.getType(), Status.STATUS_UNMARSHALLER);
		completeRequest(new Buffer(data), client.post(uri, responder));					
		return responder.getResponse();
	}
	

	

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.SynchDashku#getResource(java.lang.CharSequence)
	 */
	@Override
	public Resource getResource(CharSequence resourceUri) {
		log.debug("Calling getResourceString({})", resourceUri);
		SynchronousResponse<Resource> responder = newSynchronousResponse(Resource.RESOURCE_TYPE.getType());
		completeRequest(client.get(resourceUri.toString(), responder));
		return responder.getResponse();		
	}


	
	

}
