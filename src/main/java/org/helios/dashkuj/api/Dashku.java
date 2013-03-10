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

import java.net.URL;
import java.util.Collection;

import org.helios.dashkuj.domain.Dashboard;
import org.helios.dashkuj.domain.Transmission;
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
	

//	This is set in the Dashku impl ctor	
//	/**
//	 * Sets the Dashku API key. This needs to be called before any API request can be made. 
//	 * To get your API key, checkout the API docs in <a href="https://github.com/Anephenix/dashku">Dashku</a>.
//	 * @param apiKey the Dashku API key
//	 */
//	public void setApiKey(CharSequence apiKey);
	
	
	/**
	 * Retrieves all of the dashboards associated with the set Dashku API key
	 * @return a collection of dashboards
	 */
	public Collection<Dashboard> getDashboards();
	
	/**
	 * Retrieves a dashboard, given the id of the dashboard.
	 * @param dashboardId The id of the dashboard to retrieve
	 * @return a dashboard
	 */
	public Dashboard getDashboard(CharSequence dashboardId);
	
	/**
	 * Creates a new dashboard in the Dashku server
	 * @param dashboard the dashboard domain model to create
	 * @return The id of the created dashboard
	 */
	public String createDashboard(Dashboard dashboard);
	
	/**
	 * Updates an existing dashboard in the Dashku server
	 * @param dashboard the dashboard to update
	 */
	public void updateDashboard(Dashboard dashboard);
	
	/**
	 * Deletes an existing dashboard from the Dashku server
	 * @param dashboard the dashboard to delete
	 */
	public void deleteDashboard(Dashboard dashboard);
	
	/**
	 * Deletes an existing dashboard from the Dashku server
	 * @param dashboardId the id of the dashboard to delete
	 */
	public void deleteDashboard(CharSequence dashboardId);
	
	/**
	 * Creates a new widget in the Dashku server associated to the dashboard with the passed dashboard id
	 * @param dashboardId The id of the dashboard that this widget belongs to 
	 * @param widget The widget to create and add to the dashboard
	 * @return The id of the created widget
	 */
	public String createWidget(CharSequence dashboardId, Widget widget);
	
	/**
	 * Updates an existing widget in the Dashku server
	 * @param dashboardId The id of the dashboard that this widget belongs to 
	 * @param widget The widget to update
	 * @return the updated widget
	 */
	public Widget updateWidget(CharSequence dashboardId, Widget widget);
	
	/**
	 * Deletes an existing widget from the Dashku server
	 * @param dashboardId The id of the dashboard that this widget belongs to 
	 * @param widgetId The id of the widget to delete
	 */
	public void deleteWidget(CharSequence dashboardId, CharSequence widgetId);
	
	/**
	 * Transmits data to existing widgets
	 * @param transmissions An array of transmissions
	 */
	public void transmit(Transmission...transmissions);
	
	
}
