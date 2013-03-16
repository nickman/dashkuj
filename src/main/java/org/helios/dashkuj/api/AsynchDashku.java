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

import java.util.Collection;

import org.helios.dashkuj.domain.Dashboard;
import org.helios.dashkuj.domain.Resource;
import org.helios.dashkuj.domain.Status;
import org.helios.dashkuj.domain.Transmission;
import org.helios.dashkuj.domain.Widget;

/**
 * <p>Title: AsynchDashku</p>
 * <p>Description: The asynchronous dashku API</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.api.AsynchDashku</code></p>
 */

public interface AsynchDashku {
	
	/**
	 * Retrieves all of the dashboards associated with the set Dashku API key
	 * @param listener the response listener
	 */
	public void getDashboards(DashkuListener listener);
	
	/**
	 * Retrieves a dashboard, given the id of the dashboard.
	 * @param dashboardId The id of the dashboard to retrieve
	 * @param listener the response listener
	 */
	public void getDashboard(CharSequence dashboardId, DashkuListener listener);
	
	/**
	 * Creates a new dashboard in the Dashku server.
	 * <p>Note that currently Dashku ignores the <b><code>css</code></b> and <b><code>screenWidth</code></b> attributes. See Dashku Issue <a href="https://github.com/Anephenix/dashku/issues/16">16</a>.</p>
	 * @param dashboard the dashboard domain model to create
	 * @param listener the response listener
	 */
	public void createDashboard(Dashboard dashboard, DashkuListener listener);
	
	/**
	 * Updates an existing dashboard in the Dashku server
	 * @param dashboard the dashboard to update
	 * @param listener the response listener
	 */
	public void updateDashboard(Dashboard dashboard, DashkuListener listener);
	
	/**
	 * Deletes an existing dashboard from the Dashku server
	 * @param dashboard the dashboard to delete
	 * @param listener the response listener
	 */
	public void deleteDashboard(Dashboard dashboard, DashkuListener listener);
	
	/**
	 * Deletes an existing dashboard from the Dashku server
	 * @param dashboardId the id of the dashboard to delete
	 * @param listener the response listener
	 */
	public void deleteDashboard(CharSequence dashboardId, DashkuListener listener);
	
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
	 * @return The id of the deleted widget
	 */
	public String deleteWidget(CharSequence dashboardId, CharSequence widgetId);
	
	/**
	 * Transmits data to existing widgets
	 * @param transmissions An array of transmissions
	 */
	public void transmit(Transmission...transmissions);
	
	/**
	 * Acquires the named resource from the server
	 * @param resourceUri The URI of the resource
	 * @return the resource
	 */
	public Resource getResource(CharSequence resourceUri);
	
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
	 * Simple data post
	 * @param uri The URI to post to
	 * @param data The data to post in JSON format
	 * @return The returned status
	 */
	public Status post(String uri, String data);
	

}
