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

import org.helios.dashkuj.api.AsynchDashku;
import org.helios.dashkuj.api.Dashku;
import org.helios.dashkuj.api.DashkuHandler;
import org.helios.dashkuj.core.apiimpl.SynchDashkuImpl.SynchronousResponse;
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
 * <p>Title: AsynchDashkuImpl</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.core.apiimpl.AsynchDashkuImpl</code></p>
 */

public class AsynchDashkuImpl extends AbstractDashku implements AsynchDashku {

	/**
	 * Creates a new AsynchDashkuImpl
	 * @param client The http client for connecting to the dashku server
	 * @param apiKey The dashku api key
	 * @param host The dashku server host or ip address
	 * @param port The dashku server port
	 */
	public AsynchDashkuImpl(HttpClient client, String apiKey, String host, int port) {
		super(client, apiKey, host, port);
	}
	
	// ========================================
	// Actual impls and Delegate targets
	// ========================================

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#getDashboards(org.helios.dashkuj.api.DashkuHandler, org.helios.dashkuj.api.DashkuHandler)
	 */
	@Override
	public void getDashboards(DashkuHandler<Collection<Dashboard>> handler, DashkuHandler<Exception> errorHandler) {
		log.debug("Calling getDashboards()");
		AsynchronousResponse<Collection<Dashboard>> responder = new AsynchronousResponse<Collection<Dashboard>>(handler, errorHandler, Dashboard.DASHBOARD_COLLECTION_TYPE.getType(), Dashboard.DASHBOARD_COLLECTION_UNMARSHALLER);
		client.get(String.format(URI_GET_DASHBOARDS, apiKey), responder).setTimeout(timeout).end();
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#getDashboard(java.lang.CharSequence, org.helios.dashkuj.api.DashkuHandler, org.helios.dashkuj.api.DashkuHandler)
	 */
	@Override
	public void getDashboard(CharSequence dashboardId, DashkuHandler<Dashboard> handler, DashkuHandler<Exception> errorHandler) {
		log.debug("Calling getDashboard()");
		AsynchronousResponse<Dashboard> responder = new AsynchronousResponse<Dashboard>(handler, errorHandler, Dashboard.DASHBOARD_TYPE.getType(), Dashboard.DASHBOARD_UNMARSHALLER);
		client.get(String.format(URI_GET_DASHBOARD, dashboardId, apiKey), responder).setTimeout(timeout).end();
	}	
	
	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#createDashboard(org.helios.dashkuj.domain.Dashboard, org.helios.dashkuj.api.DashkuHandler, org.helios.dashkuj.api.DashkuHandler)
	 */
	@Override
	public void createDashboard(Dashboard dashboard, DashkuHandler<Dashboard> handler, DashkuHandler<Exception> errorHandler) {
		log.debug("Calling createDashboard()");
		AsynchronousResponse<Dashboard> responder = new AsynchronousResponse<Dashboard>(handler, errorHandler, Dashboard.DASHBOARD_TYPE.getType(), Dashboard.DASHBOARD_UNMARSHALLER);
		completeRequest(buildDirtyUpdatePostJSON(dashboard), client.post(String.format(URI_POST_CREATE_DASHBOARD, apiKey), responder));					
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#updateDashboard(org.helios.dashkuj.domain.Dashboard, org.helios.dashkuj.api.DashkuHandler, org.helios.dashkuj.api.DashkuHandler)
	 */
	@Override
	public void updateDashboard(Dashboard dashboard, DashkuHandler<Dashboard> handler, DashkuHandler<Exception> errorHandler) {
		log.debug("Calling updateDashboard()");
		AsynchronousResponse<Dashboard> responder = new AsynchronousResponse<Dashboard>(handler, errorHandler, Dashboard.DASHBOARD_TYPE.getType(), Dashboard.DASHBOARD_UNMARSHALLER);
		completeRequest(
				buildDirtyUpdatePostJSON(dashboard), 
				client.put(String.format(URI_PUT_UPDATE_DASHBOARD, dashboard.getId(), apiKey), responder)
					.putHeader(HttpHeaders.Names.ACCEPT, JSON_CONTENT_TYPE)
		);					
	}
	
	
	
	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#deleteDashboard(java.lang.CharSequence, org.helios.dashkuj.api.DashkuHandler, org.helios.dashkuj.api.DashkuHandler)
	 */
	@Override
	public void deleteDashboard(CharSequence dashboardId, final DashkuHandler<Void> handler, DashkuHandler<Exception> errorHandler) {
		log.debug("Calling deleteDashboard()");
		DashkuHandler<Status> statusHandler = new DashkuHandler<Status>() {
			@Override
			public void handle(Status event) {
				if(handler!=null) handler.handle(null);
				
			}
		};
		AsynchronousResponse<Status> responder = new AsynchronousResponse<Status>(statusHandler, errorHandler, Status.STATUS_TYPE.getType(), Status.STATUS_UNMARSHALLER);
		completeRequest(client.delete(String.format(URI_DELETE_DELETE_DASHBOARD, dashboardId, apiKey), responder));			
		
	}
	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#createWidget(java.lang.CharSequence, org.helios.dashkuj.domain.Widget, org.helios.dashkuj.api.DashkuHandler, org.helios.dashkuj.api.DashkuHandler)
	 */
	@Override
	public void createWidget(final CharSequence dashboardId, final Widget widget, final DashkuHandler<Widget> handler, DashkuHandler<Exception> errorHandler) {
		log.debug("Calling createWidget()");
		final Dashku dashku = this;
		DashkuHandler<Widget> statusHandler = new DashkuHandler<Widget>() {
			@Override
			public void handle(Widget createdWidget) {
				if(handler!=null) handler.handle(null);
				widget.updateFrom(createdWidget, dashboardId.toString()).getId();
				widget.updateTransmissionScript(TransmissionScriptType.DEFAULT_TYPE, dashku);
			}
		};
		
		AsynchronousResponse<Widget> responder = new AsynchronousResponse<Widget>(statusHandler, errorHandler, Widget.WIDGET_ID_TYPE.getType(), Widget.WIDGET_UNMARSHALLER);
		completeRequest(buildDirtyUpdatePostJSON(widget), JSON_CONTENT_TYPE,  client.post(String.format(URI_POST_CREATE_WIDGET, dashboardId, apiKey), responder));
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#updateWidget(java.lang.CharSequence, org.helios.dashkuj.domain.Widget, org.helios.dashkuj.api.DashkuHandler, org.helios.dashkuj.api.DashkuHandler)
	 */
	@Override
	public void updateWidget(final CharSequence dashboardId, final Widget widget, final DashkuHandler<Widget> handler, DashkuHandler<Exception> errorHandler) {
		log.debug("Calling updateWidget()");
		final Dashku dashku = this;
		DashkuHandler<Widget> statusHandler = new DashkuHandler<Widget>() {
			@Override
			public void handle(Widget createdWidget) {
				if(handler!=null) handler.handle(null);
				widget.updateFrom(createdWidget, dashboardId.toString()).getId();
				widget.updateTransmissionScript(TransmissionScriptType.DEFAULT_TYPE, dashku);
			}
		};
		
		AsynchronousResponse<Widget> responder = new AsynchronousResponse<Widget>(statusHandler, errorHandler, Widget.WIDGET_ID_TYPE.getType(), Widget.WIDGET_UNMARSHALLER);		
		completeRequest(buildDirtyUpdatePostJSON(widget), JSON_CONTENT_TYPE,  client.put(String.format(URI_PUT_UPDATE_WIDGET, dashboardId, widget.getId(), apiKey), responder));
	}
	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#deleteWidget(java.lang.CharSequence, java.lang.CharSequence, org.helios.dashkuj.api.DashkuHandler, org.helios.dashkuj.api.DashkuHandler)
	 */
	@Override
	public void deleteWidget(CharSequence dashboardId, CharSequence widgetId, final DashkuHandler<Void> handler, DashkuHandler<Exception> errorHandler) {
		log.debug("Calling deleteWidget({}, {})", dashboardId, widgetId);
		DashkuHandler<Status> statusHandler = new DashkuHandler<Status>() {
			@Override
			public void handle(Status event) {
				if(handler!=null) handler.handle(null);
				
			}
		};
		AsynchronousResponse<Status> responder = new AsynchronousResponse<Status>(statusHandler, errorHandler, Status.STATUS_TYPE.getType(), Status.STATUS_UNMARSHALLER);
		completeRequest(client.delete(String.format(URI_DELETE_DELETE_WIDGET, dashboardId, widgetId, apiKey), responder));
	}
	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#transmit(org.helios.dashkuj.api.DashkuHandler, org.helios.dashkuj.api.DashkuHandler, org.helios.dashkuj.domain.Transmission[])
	 */
	@Override
	public void transmit(DashkuHandler<Void> handler, DashkuHandler<Exception> errorHandler, Transmission... transmissions) {
		// TODO Auto-generated method stub		
	}
	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#getResource(java.lang.CharSequence, org.helios.dashkuj.api.DashkuHandler, org.helios.dashkuj.api.DashkuHandler)
	 */
	@Override
	public void getResource(CharSequence resourceUri, DashkuHandler<Resource> handler, DashkuHandler<Exception> errorHandler) {
		log.debug("Calling getResourceString({})", resourceUri);
		AsynchronousResponse<Resource> responder = new AsynchronousResponse<Resource>(handler, errorHandler, Resource.RESOURCE_TYPE.getType());
		completeRequest(client.get(resourceUri.toString(), responder));		
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#post(java.lang.String, java.lang.String, org.helios.dashkuj.api.DashkuHandler, org.helios.dashkuj.api.DashkuHandler)
	 */
	@Override
	public void post(String uri, String data, DashkuHandler<Status> handler, DashkuHandler<Exception> errorHandler) {
		log.debug("Calling post({},{})", uri, data);
		AsynchronousResponse<Status> responder = new AsynchronousResponse<Status>(handler, errorHandler, Status.STATUS_TYPE.getType(), Status.STATUS_UNMARSHALLER);
		completeRequest(new Buffer(data), client.post(uri, responder));					
	}
	

	
	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#deleteDashboard(org.helios.dashkuj.domain.Dashboard, org.helios.dashkuj.api.DashkuHandler, org.helios.dashkuj.api.DashkuHandler)
	 */
	@Override
	public void deleteDashboard(Dashboard dashboard, DashkuHandler<Void> handler, DashkuHandler<Exception> errorHandler) {
		deleteDashboard(dashboard.getId(), handler, errorHandler);
		
	}
	
	// ================================================================
	//   UTILS
	// ================================================================
	
	
	/** The default response handler */
	protected final Handler<?> DEFAULT_RESPONSE_HANDLER = new Handler<Object>(){
		@Override
		public void handle(Object event) {
			log.debug("Default handled event [{}]", event);
		}
	};
	/** The default exception handler */
	protected final Handler<Exception> DEFAULT_EXCEPTION_HANDLER = new Handler<Exception>(){
		@Override
		public void handle(Exception event) {
			log.error("Default handled exception", event);
		}
	};
	


	/**
	 * <p>Title: AsynchronousResponse</p>
	 * <p>Description: A {@link DashkuHandler} wrapper to map to vertx handlers</p> 
	 * <p>Company: Helios Development Group LLC</p>
	 * @author Whitehead (nwhitehead AT heliosdev DOT org)
	 * <p><code>org.helios.dashkuj.core.apiimpl.AsynchDashkuImpl.AsynchronousResponse</code></p>
	 * @param <T> The expected type of the response
	 */
	protected class AsynchronousResponse<T> implements Handler<HttpClientResponse> {
		/** The response handler */
		protected final Handler<T> handler;
		/** The response exception handler */
		protected final Handler<Exception> errorHandler;
		
		/** The domain unmarshaller */
		protected final DomainUnmarshaller<T> unmarshaller;
		/** The actual type we expect the response to be */
		protected final Type responseType;
		/** The content type */
		protected String contentType = null;
		
		/**
		 * Creates a new AsynchronousResponse with no unmarshaller
		 * @param handler the api caller provided response handler
		 * @param errorHandler the api caller provided error handler 
		 * @param responseType The actual type we expect the response to be
		 */
		@SuppressWarnings("unchecked")
		protected AsynchronousResponse(Handler<T> handler, Handler<Exception> errorHandler, Type responseType) {
			this.handler = handler==null ? (Handler<T>)DEFAULT_RESPONSE_HANDLER : handler;
			this.errorHandler = errorHandler==null ? DEFAULT_EXCEPTION_HANDLER : errorHandler;
			this.responseType = responseType;
			this.unmarshaller = null;					
		}		
		
		
		/**
		 * Creates a new AsynchronousResponse
		 * @param handler the api caller provided response handler
		 * @param errorHandler the api caller provided error handler 
		 * @param responseType The actual type we expect the response to be
		 * @param unmarshaller The domain unmarshaller
		 */
		@SuppressWarnings("unchecked")
		protected AsynchronousResponse(Handler<T> handler, Handler<Exception> errorHandler, Type responseType, DomainUnmarshaller<T> unmarshaller) {
			this.handler = handler==null ? (Handler<T>)DEFAULT_RESPONSE_HANDLER : handler;
			this.errorHandler = errorHandler==null ? DEFAULT_EXCEPTION_HANDLER : errorHandler;
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
			event.exceptionHandler(errorHandler);
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
								handler.handle((T) status);
							} else {
								errorHandler.handle(status.getException());
							}
							return;
						}
						if(unmarshaller!=null) {
							T response = unmarshaller.unmarshall(content);
							if(unmarshaller==Dashboard.DASHBOARD_COLLECTION_UNMARSHALLER) {
								repository.synch((Collection<Dashboard>)response);
							} else if (unmarshaller==Dashboard.DASHBOARD_UNMARSHALLER) {
								repository.synch((Dashboard)response);
							} else if (unmarshaller==Widget.WIDGET_UNMARSHALLER) {
								repository.synch((Widget)response);
							}
							handler.handle(response);
						} else {
							if(Resource.class.equals(responseType)) {
								handler.handle((T)new Resource(content.getBytes(), contentType));
							} else {
								handler.handle((T)content);
							}
						}
					} catch (Exception ex) {
						errorHandler.handle(ex);
					}
				}
			});			
		}
	}
	
	
/*	protected <T> Handler<T> wrapResponseHandler(DashkuHandler<T> handler) {
		
	}
*/
	// ================================================================
	//   BELOW ARE ALL DELEGATING OVERLOADS
	// ================================================================

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#getDashboards(org.helios.dashkuj.api.DashkuHandler)
	 */
	@Override
	public void getDashboards(DashkuHandler<Collection<Dashboard>> handler) {
		getDashboards(handler, null);
		
	}



	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#getDashboard(java.lang.CharSequence, org.helios.dashkuj.api.DashkuHandler)
	 */
	@Override
	public void getDashboard(CharSequence dashboardId, DashkuHandler<Dashboard> handler) {
		getDashboard(dashboardId, handler, null);
		
	}
	

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#createDashboard(org.helios.dashkuj.domain.Dashboard, org.helios.dashkuj.api.DashkuHandler)
	 */
	@Override
	public void createDashboard(Dashboard dashboard, DashkuHandler<Dashboard> handler) {
		createDashboard(dashboard, handler, null);
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#createDashboard(org.helios.dashkuj.domain.Dashboard)
	 */
	@Override
	public void createDashboard(Dashboard dashboard) {
		createDashboard(dashboard, null, null);
	}


	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#updateDashboard(org.helios.dashkuj.domain.Dashboard, org.helios.dashkuj.api.DashkuHandler)
	 */
	@Override
	public void updateDashboard(Dashboard dashboard, DashkuHandler<Dashboard> handler) {
		updateDashboard(dashboard, handler, null);		
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#updateDashboard(org.helios.dashkuj.domain.Dashboard)
	 */
	@Override
	public void updateDashboard(Dashboard dashboard) {
		updateDashboard(dashboard, null, null);		
	}


	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#deleteDashboard(org.helios.dashkuj.domain.Dashboard, org.helios.dashkuj.api.DashkuHandler)
	 */
	@Override
	public void deleteDashboard(Dashboard dashboard, DashkuHandler<Void> handler) {
		deleteDashboard(dashboard, handler, null);
		
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#deleteDashboard(org.helios.dashkuj.domain.Dashboard)
	 */
	@Override
	public void deleteDashboard(Dashboard dashboard) {
		deleteDashboard(dashboard, null, null);
		
	}


	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#deleteDashboard(java.lang.CharSequence, org.helios.dashkuj.api.DashkuHandler)
	 */
	@Override
	public void deleteDashboard(CharSequence dashboardId, DashkuHandler<Void> handler) {
		deleteDashboard(dashboardId, handler, null);		
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#deleteDashboard(java.lang.CharSequence)
	 */
	@Override
	public void deleteDashboard(CharSequence dashboardId) {
		deleteDashboard(dashboardId, null, null);		
	}


	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#createWidget(java.lang.CharSequence, org.helios.dashkuj.domain.Widget, org.helios.dashkuj.api.DashkuHandler)
	 */
	@Override
	public void createWidget(CharSequence dashboardId, Widget widget, DashkuHandler<Widget> handler) {
		createWidget(dashboardId, widget, handler, null);		
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#createWidget(java.lang.CharSequence, org.helios.dashkuj.domain.Widget)
	 */
	@Override
	public void createWidget(CharSequence dashboardId, Widget widget) {
		createWidget(dashboardId, widget, null, null);		
	}


	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#updateWidget(java.lang.CharSequence, org.helios.dashkuj.domain.Widget, org.helios.dashkuj.api.DashkuHandler)
	 */
	@Override
	public void updateWidget(CharSequence dashboardId, Widget widget, DashkuHandler<Widget> handler) {		
		updateWidget(dashboardId, widget, handler, null);		
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#updateWidget(java.lang.CharSequence, org.helios.dashkuj.domain.Widget)
	 */
	@Override
	public void updateWidget(CharSequence dashboardId, Widget widget) {
		updateWidget(dashboardId, widget, null, null);
		
	}


	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#deleteWidget(java.lang.CharSequence, java.lang.CharSequence, org.helios.dashkuj.api.DashkuHandler)
	 */
	@Override
	public void deleteWidget(CharSequence dashboardId, CharSequence widgetId, DashkuHandler<Void> handler) {
		deleteWidget(dashboardId, widgetId, handler, null);		
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#deleteWidget(java.lang.CharSequence, java.lang.CharSequence)
	 */
	@Override
	public void deleteWidget(CharSequence dashboardId, CharSequence widgetId) {
		deleteWidget(dashboardId, widgetId, null, null);		
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#transmit(org.helios.dashkuj.api.DashkuHandler, org.helios.dashkuj.domain.Transmission[])
	 */
	@Override
	public void transmit(DashkuHandler<Void> handler, Transmission... transmissions) {
		transmit(handler, null, transmissions);
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#transmit(org.helios.dashkuj.domain.Transmission[])
	 */
	@Override
	public void transmit(Transmission... transmissions) {
		transmit(null, null, transmissions);
	}


	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#getResource(java.lang.CharSequence, org.helios.dashkuj.api.DashkuHandler)
	 */
	@Override
	public void getResource(CharSequence resourceUri, DashkuHandler<Resource> handler) {
		getResource(resourceUri, handler, null);		
	}


	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#post(java.lang.String, java.lang.String, org.helios.dashkuj.api.DashkuHandler)
	 */
	@Override
	public void post(String uri, String data, DashkuHandler<Status> handler) {
		post(uri, data, handler, null);		
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku#post(java.lang.String, java.lang.String)
	 */
	@Override
	public void post(String uri, String data) {
		post(uri, data, null, null);		
	}


}
