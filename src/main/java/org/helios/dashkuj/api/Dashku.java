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

import org.helios.dashkuj.core.Dashkuj;
import org.helios.dashkuj.domain.Widget;


/**
 * <p>Title: Dashku</p>
 * <p>Description: Defines the dashku api</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.api.Dashku</code></p>
 */

public interface Dashku {
	
	/** The URI template for retrieving all dashboards */
	public static final String URI_GET_DASHBOARDS = "/api/dashboards?apiKey=%s";
	/** The URI (GET) template for retrieving a dashboard */
	public static final String URI_GET_DASHBOARD = "/api/dashboards/%s?apiKey=%s";
	/** The URI (POST) template for creating a dashboard */
	public static final String URI_POST_CREATE_DASHBOARD = "/api/dashboards?apiKey=%s";
	/** The URI (PUT) template for updating a dashboard */
	public static final String URI_PUT_UPDATE_DASHBOARD = "/api/dashboards/%s?apiKey=%s";
	/** The URI (DELETE) template for deleting a dashboard */
	public static final String URI_DELETE_DELETE_DASHBOARD = "/api/dashboards/%s?apiKey=%s";
	/** The URI (POST) template for creating a widget */
	public static final String URI_POST_CREATE_WIDGET = "/api/dashboards/%s/widgets?apiKey=%s";
	/** The URI (PUT) template for updating a widget */
	public static final String URI_PUT_UPDATE_WIDGET = "/api/dashboards/%s/widgets/%s?apiKey=%s";
	/** The URI (DELETE) template for deleting a widget */
	public static final String URI_DELETE_DELETE_WIDGET = "/api/dashboards/%s/widgets/%s?apiKey=%s";
	/** The URI (POST) template for transmitting to a widget */
	public static final String URI_POST_TRANSMIT = "/api/transmission?apiKey=%s";
	
	/** The content type for JSON */
	public static final String JSON_CONTENT_TYPE = "application/json";
	

	
	
	/**
	 * Returns the dashku host or ip address 
	 * @return the dashku host or ip address
	 */
	public String getHost();
	
	/**
	 * Returns the dashku port
	 * @return the dashku port
	 */
	public int getPort();
	
	/**
	 * Returns the dashku api key
	 * @return the dashku api key
	 */
	public String getApiKey();

	/**
	 * Closes this dashku
	 */
	public void dispose();
	
	/**
	 * Indicates if this dashku is closed
	 * @return true if this dashku is closed, false if it is still open
	 */
	public boolean isClosed();



	
}
