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
package org.helios.dashkuj.api;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Title: AsynchHandler</p>
 * <p>Description: A generic response handler, registered by a dashku request issuer, that also serves as the last channel handler.</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.api.AsynchHandler</code></p>
 * @param <T> The expected type of the response
 */

public class AsynchHandler<T> extends SimpleChannelUpstreamHandler {
	/** The response handler when the asynch request is successful */
	protected final AsynchResponseHandler<T> responseHandler;
	/** The error handler when the asynch request fails */
	protected final AsynchErrorHandler errorHandler;
	/** The pending request queue which the handler <i>must</i> consume from on success or failure */
	protected final BlockingQueue<AsynchHandler<T>> pendingQueue;
	/** The pipeline the asynch handler has been added to */
	protected final ChannelPipeline pipeline;
	
	/** Flag indicating if this handler has been consumed from the pending queue */
	protected final AtomicBoolean dequeued = new AtomicBoolean(false);
	/** Static class logger */
	protected static final Logger LOG = LoggerFactory.getLogger(AsynchHandler.class);
	
	/** The default response handler so we don't gotta do no steenkin' null checking  */
	protected static final AsynchResponseHandler<?> DEFAULT_RESPONSE_HANDLER = new AsynchResponseHandler<Object>() {
		@Override
		public void onResponse(Object response) {
			LOG.debug("Default asynch response handler received [{}]",  response);
			
		}
	};
	/** The default error handler so we don't gotta do no steenkin' null checking  */
	protected static final AsynchErrorHandler DEFAULT_ERROR_HANDLER = new AsynchErrorHandler() {
		@Override
		public void onAsynchError(Throwable cause) {
			LOG.warn("Default asynch error handler invoked",  cause);
		}
	};
	
	/**
	 * Creates a new AsynchHandler
	 * @param pendingQueue The queue the waiting asynch handlers will be consumed from
	 * @param pipeline The pipeline the asynch handler has been added to
	 * @param responseHandler The response handler when the asynch request is successful
	 * @param errorHandler The error handler when the asynch request fails
	 */
	private AsynchHandler(BlockingQueue<AsynchHandler<T>> pendingQueue, ChannelPipeline pipeline, AsynchResponseHandler<T> responseHandler, AsynchErrorHandler errorHandler) {
		this.responseHandler = responseHandler==null ? (AsynchResponseHandler<T>)DEFAULT_RESPONSE_HANDLER : responseHandler;
		this.errorHandler = errorHandler==null ? DEFAULT_ERROR_HANDLER : errorHandler;
		this.pendingQueue = pendingQueue;
		this.pipeline = pipeline;
	}
	
	/**
	 * <p>Title: AsyncHandlerFactory</p>
	 * <p>Description: A factory to create AsynchHandler instances</p> 
	 * <p>Company: Helios Development Group LLC</p>
	 * @author Whitehead (nwhitehead AT heliosdev DOT org)
	 * <p><code>org.helios.dashkuj.api.AsynchHandler.AsyncHandlerFactory</code></p>
	 */
	public static class AsyncHandlerFactory<T> {
		/** The pending request queue which the handler <i>must</i> consume from on success or failure */
		protected final BlockingQueue<AsynchHandler<T>> pendingQueue;
		/** The pipeline the asynch handler has been added to */
		protected final ChannelPipeline pipeline;

		/**
		 * Creates a new AsyncHandlerFactory
		 * @param pendingQueue The queue the waiting asynch handlers will be consumed from
		 * @param pipeline The pipeline the asynch handler has been added to
		 */
		public AsyncHandlerFactory(BlockingQueue<AsynchHandler<T>> pendingQueue, ChannelPipeline pipeline) {
			if(pendingQueue==null) throw new IllegalArgumentException("The passed queue was null", new Throwable());
			if(pipeline==null) throw new IllegalArgumentException("The passed pipeline was null", new Throwable());
			this.pendingQueue = pendingQueue;
			this.pipeline = pipeline;
		}
		
		/**
		 * Creates and returns a new AsynchHandler
		 * @param responseHandler The successful response handler
		 * @param errorHandler The failed request error handler
		 * @return a new AsynchHandler
		 */
		public AsynchHandler<T> handler(AsynchResponseHandler<T> responseHandler, AsynchErrorHandler errorHandler) {
			return new AsynchHandler<T>(pendingQueue, pipeline, responseHandler, errorHandler);
		}
		
		/**
		 * Creates and returns a new AsynchHandler that internally implements a default error handler
		 * @param responseHandler The successful response handler
		 * @return a new AsynchHandler
		 */
		public AsynchHandler<T> handler(AsynchResponseHandler<T> responseHandler) {
			return new AsynchHandler<T>(pendingQueue, pipeline, responseHandler, null);
		}
		
		/**
		 * Creates and returns a new AsynchHandler that internally implements a default response and error handler
		 * @return a new AsynchHandler
		 */
		public AsynchHandler<T> handler() {
			return new AsynchHandler<T>(pendingQueue, pipeline, null, null);
		}
		
		
	}

	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#exceptionCaught(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ExceptionEvent)
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		try { pipeline.remove(this); } catch (Exception ex) {}
		AsynchHandler<T> handler = null;
		final boolean isDequeued = dequeued.compareAndSet(false, true); 
		if(isDequeued) {
			handler = pendingQueue.poll();
		}
		if(handler!=null) {			
			errorHandler.onAsynchError(e.getCause());
		}

	}
	
	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#messageReceived(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.MessageEvent)
	 */
	@Override 
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		try { pipeline.remove(this); } catch (Exception ex) {}
		AsynchHandler<T> handler = null;
		final boolean isDequeued = dequeued.compareAndSet(false, true); 
		if(isDequeued) {
			handler = pendingQueue.poll();
		}
		if(handler==null) {
			if(!isDequeued) {
				// this means the pending request was already dequeued by the exception handler
				// so we have nothing to do
			} else {
				// this means the pending request disappeared.
				LOG.error("Unexpected missing pending handler (not dequeued, but queue was empty)", new Throwable());
			}
		} else {
			// got the handler. Make sure the message is not an exceptiom.
			Object message = e.getMessage();
			if(message instanceof Throwable) {
				errorHandler.onAsynchError((Throwable)message);
			} else {
				try {
					responseHandler.onResponse((T)message);
				} catch (Exception ex) {
					errorHandler.onAsynchError(new Exception("Failed to invoke onResponse(T) against pending asynch handler", ex));
				}
			}
		}
	}
	
	/**
	 * <p>Title: AsynchResponseHandler</p>
	 * <p>Description: Callback definition for an asynch request response</p> 
	 * <p><code>org.helios.dashkuj.api.AsynchHandler.AsynchResponseHandler</code></p>
	 * @param <T> The expected type of the response
	 */
	public interface AsynchResponseHandler<T> {
		/**
		 * Callback with the unmarshalled response
		 * @param response the unmarshalled response
		 */
		public void onResponse(T response);
	}
	
	/**
	 * <p>Title: AsynchErrorHandler</p>
	 * <p>Description: Callback definition for an asynch request failure</p> 
	 * <p><code>org.helios.dashkuj.api.AsynchHandler.AsynchErrorHandler</code></p>
	 */
	public interface AsynchErrorHandler {
		/**
		 * Callback when an asynchronous dashku request fails
		 * @param cause The root cause of the failure
		 */
		public void onAsynchError(Throwable cause);
	}
	

}
