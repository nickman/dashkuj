/**
 * Helios, OpenSource Monitoring
 * Brought to you by the Helios Development Group
 *
 * Copyright 2013, Helios Development Group and individual contributors
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
package org.helios.dashkuj.domain;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferIndexFinder;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.buffer.Buffer;

import com.google.gson.annotations.SerializedName;

/**
 * <p>Title: Status</p>
 * <p>Description: Represents a status message and state returned by the Dashku server in response to an API call</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.domain.Status</code></p>
 */

public class Status {
	/** The status message */
	@SerializedName("status")
	protected String message;
	/** The status reason (optional) */
	@SerializedName("reason")
	protected String reason;
	/** The dashboard id (optional) */
	@SerializedName("dashboardId")
	protected String dashboardId;
	/** The widget id (optional) */
	@SerializedName("widgetId")
	protected String widgetId;
	
	
	/** The byte pattern for a status json message with double quotes */
	public static final ChannelBuffer STATUS_PATTERN_DQ = ChannelBuffers.unmodifiableBuffer(ChannelBuffers.wrappedBuffer("{\"status\":".getBytes()));
	/** The byte pattern for a status json message with single quotes */
	public static final ChannelBuffer STATUS_PATTERN_SQ = ChannelBuffers.unmodifiableBuffer(ChannelBuffers.wrappedBuffer("{'status':".getBytes()));
	/** The minimum length of a status message */
	public static final int MIN_STATUS_SIZE = "{'status':''}".getBytes().length;
	/** The length of leading pattern */
	public static final int LEADER_STATUS_SIZE = "{'status':".getBytes().length;
	
	
	/** A ChannelBufferIndexFinder for sniffing channel buffers for the status json signature */
	public static final ChannelBufferIndexFinder STATUS_PATTERN_FINDER = new ChannelBufferIndexFinder() {
		
		/**
		 * {@inheritDoc}
		 * @see org.jboss.netty.buffer.ChannelBufferIndexFinder#find(org.jboss.netty.buffer.ChannelBuffer, int)
		 */
		public boolean find(ChannelBuffer buffer, int guessedIndex) {
			if(buffer.readableBytes()< guessedIndex + MIN_STATUS_SIZE) return false;
			ChannelBuffer slice = buffer.slice(guessedIndex, LEADER_STATUS_SIZE);
			return slice.equals(STATUS_PATTERN_DQ) || slice.equals(STATUS_PATTERN_SQ);			
		}
	};
	
	/**
	 * Determines if the passed buffer contains a signature suggesting it is the JSON for a Status entity
	 * @param buffer the buffer to test 
	 * @return true if the passed buffer contains a signature suggesting it is the JSON for a Status entity, false otherwise
	 */
	public static boolean containsStatus(Buffer buffer) {
		if(buffer==null) return false;
		return containsStatus(buffer.getChannelBuffer());
	}
	
	/**
	 * Determines if the passed buffer contains a signature suggesting it is the JSON for a Status entity
	 * @param buffer the buffer to test 
	 * @return true if the passed buffer contains a signature suggesting it is the JSON for a Status entity, false otherwise
	 */
	public static boolean containsStatus(ChannelBuffer buffer) {
		if(buffer==null) return false;
		return buffer.indexOf(0, buffer.readableBytes()-1, STATUS_PATTERN_FINDER)!=-1;
	}
	
	/**
	 * Returns the dashboard id, or null if one was not set
	 * @return the dashboardId
	 */
	public String getDashboardId() {
		return dashboardId;
	}
	
	/**
	 * Returns the widget id, or null if one was not set
	 * @return the widgetId
	 */
	public String getWidgetId() {
		return widgetId;
	}
	

	/** The (HTTP) response code */
	protected HttpResponseStatus responseCode = HttpResponseStatus.OK; 
	
	/**
	 * Sets the response code on this status
	 * @param responseCode the (HTTP) status code
	 * @return this Status
	 */
	public Status responseCode(HttpResponseStatus responseCode) {
		this.responseCode = responseCode;
		return this;
	}

	/**
	 * Returns the status message
	 * @return the status message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Returns the status reason
	 * @return the status reason
	 */
	public String getReason() {
		return reason;
	}
	

	/**
	 * Returns the (HTTP) status code
	 * @return the status code
	 */
	public HttpResponseStatus getResponseCode() {
		return responseCode;
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder("Status [");
		if(message!=null) {
			b.append("message:").append(message).append(" ");
		}
		if(reason!=null) {
			b.append("reason:").append(reason).append(" ");
		}
		if(responseCode!=null) {
			b.append("rcode:[").append(responseCode).append("] ");
		}
		if(dashboardId!=null) {
			b.append("dashboardId:").append(dashboardId).append(" ");
		}
		if(widgetId!=null) {
			b.append("widgetId:").append(widgetId).append(" ");
		}		
		
		String status = b.toString().trim();		
		return status + "]";
	}
	
	
	
}
