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
 * <p>Each API call has 3 variations:<ol>
 * 	<li>Accepts an asynchronous response handler and exception handler. Both callbacks are processed according to the passed handlers if they are not null</li>
 * 	<li>Accepts an asynchronous response handler and implements an implicit exception handler which simply logs the error</li>
 *  <li>Non query-only operations have a variation that accepts no handlers. This is essentially a fire-and-forget operation</li>
 * </ol>
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.api.AsynchDashku</code></p>
 */

public interface AsynchDashku extends Dashku {
	
	/**
	 * Returns a synchronous instance of this dashku
	 * @return a synchronous instance of this dashku
	 */
	public SynchDashku getSynchDashku();
	
	/**
	 * Retrieves all of the dashboards associated with the set Dashku API key
	 * @param handler the response listener
	 * @param errorHandler The error handler
	 */
	public void getDashboards(DashkuHandler<Collection<Dashboard>> handler, DashkuHandler<Exception> errorHandler);
	
	/**
	 * Retrieves all of the dashboards associated with the set Dashku API key
	 * @param handler the response listener
	 */
	public void getDashboards(DashkuHandler<Collection<Dashboard>> handler);
	
	
	/**
	 * Retrieves a dashboard, given the id of the dashboard.
	 * @param dashboardId The id of the dashboard to retrieve
	 * @param handler the response listener
	 * @param errorHandler The error handler
	 */
	public void getDashboard(CharSequence dashboardId, DashkuHandler<Dashboard> handler, DashkuHandler<Exception> errorHandler);
	
	/**
	 * Retrieves a dashboard, given the id of the dashboard.
	 * @param dashboardId The id of the dashboard to retrieve
	 * @param handler the response listener
	 */
	public void getDashboard(CharSequence dashboardId, DashkuHandler<Dashboard> handler);
	
	
	/**
	 * Creates a new dashboard in the Dashku server.
	 * <p>Note that currently Dashku ignores the <b><code>css</code></b> and <b><code>screenWidth</code></b> attributes. See Dashku Issue <a href="https://github.com/Anephenix/dashku/issues/16">16</a>.</p>
	 * @param dashboard the dashboard domain model to create
	 * @param handler the response listener
	 * @param errorHandler The error handler
	 */
	public void createDashboard(Dashboard dashboard, DashkuHandler<Dashboard> handler, DashkuHandler<Exception> errorHandler);
	
	/**
	 * Creates a new dashboard in the Dashku server.
	 * <p>Note that currently Dashku ignores the <b><code>css</code></b> and <b><code>screenWidth</code></b> attributes. See Dashku Issue <a href="https://github.com/Anephenix/dashku/issues/16">16</a>.</p>
	 * @param dashboard the dashboard domain model to create
	 * @param handler the response listener
	 */
	public void createDashboard(Dashboard dashboard, DashkuHandler<Dashboard> handler);
	
	/**
	 * Creates a new dashboard in the Dashku server.
	 * <p>Note that currently Dashku ignores the <b><code>css</code></b> and <b><code>screenWidth</code></b> attributes. See Dashku Issue <a href="https://github.com/Anephenix/dashku/issues/16">16</a>.</p>
	 * @param dashboard the dashboard domain model to create
	 */
	public void createDashboard(Dashboard dashboard);
	
	
	
	/**
	 * Updates an existing dashboard in the Dashku server
	 * @param dashboard the dashboard to update
	 * @param handler the response listener
	 * @param errorHandler The error handler
	 */
	public void updateDashboard(Dashboard dashboard, DashkuHandler<Dashboard> handler, DashkuHandler<Exception> errorHandler);
	
	/**
	 * Updates an existing dashboard in the Dashku server
	 * @param dashboard the dashboard to update
	 * @param handler the response listener
	 */
	public void updateDashboard(Dashboard dashboard, DashkuHandler<Dashboard> handler);
	
	/**
	 * Updates an existing dashboard in the Dashku server
	 * @param dashboard the dashboard to update
	 */
	public void updateDashboard(Dashboard dashboard);
	
	
	/**
	 * Deletes an existing dashboard from the Dashku server
	 * @param dashboard the dashboard to delete
	 * @param handler the response listener
	 * @param errorHandler The error handler
	 */
	public void deleteDashboard(Dashboard dashboard, DashkuHandler<Void> handler, DashkuHandler<Exception> errorHandler);
	
	/**
	 * Deletes an existing dashboard from the Dashku server
	 * @param dashboard the dashboard to delete
	 * @param handler the response listener
	 */
	public void deleteDashboard(Dashboard dashboard, DashkuHandler<Void> handler);
	
	/**
	 * Deletes an existing dashboard from the Dashku server
	 * @param dashboard the dashboard to delete
	 */
	public void deleteDashboard(Dashboard dashboard);
	
	/**
	 * Deletes an existing dashboard from the Dashku server
	 * @param dashboardId the id of the dashboard to delete
	 * @param handler the response listener
	 * @param errorHandler The error handler
	 */
	public void deleteDashboard(CharSequence dashboardId, DashkuHandler<Void> handler, DashkuHandler<Exception> errorHandler);
	
	/**
	 * Deletes an existing dashboard from the Dashku server
	 * @param dashboardId the id of the dashboard to delete
	 * @param handler the response listener
	 */
	public void deleteDashboard(CharSequence dashboardId, DashkuHandler<Void> handler);
	
	/**
	 * Deletes an existing dashboard from the Dashku server
	 * @param dashboardId the id of the dashboard to delete
	 */
	public void deleteDashboard(CharSequence dashboardId);	
	
	
	/**
	 * Creates a new widget in the Dashku server associated to the dashboard with the passed dashboard id
	 * @param dashboardId The id of the dashboard that this widget belongs to 
	 * @param widget The widget to create and add to the dashboard
	 * @param handler the response listener
	 * @param errorHandler The error handler
	 */
	public void createWidget(CharSequence dashboardId, Widget widget, DashkuHandler<Widget> handler, DashkuHandler<Exception> errorHandler);
	
	/**
	 * Creates a new widget in the Dashku server associated to the dashboard with the passed dashboard id
	 * @param dashboardId The id of the dashboard that this widget belongs to 
	 * @param widget The widget to create and add to the dashboard
	 * @param handler the response listener
	 */
	public void createWidget(CharSequence dashboardId, Widget widget, DashkuHandler<Widget> handler);
	
	/**
	 * Creates a new widget in the Dashku server associated to the dashboard with the passed dashboard id
	 * @param dashboardId The id of the dashboard that this widget belongs to 
	 * @param widget The widget to create and add to the dashboard
	 */
	public void createWidget(CharSequence dashboardId, Widget widget);
	
	
	
	/**
	 * Updates an existing widget in the Dashku server
	 * @param dashboardId The id of the dashboard that this widget belongs to 
	 * @param widget The widget to update
	 * @param handler the response listener
	 * @param errorHandler The error handler
	 */
	public void updateWidget(CharSequence dashboardId, Widget widget, DashkuHandler<Widget> handler, DashkuHandler<Exception> errorHandler);
	
	/**
	 * Updates an existing widget in the Dashku server
	 * @param dashboardId The id of the dashboard that this widget belongs to 
	 * @param widget The widget to update
	 * @param handler the response listener
	 */
	public void updateWidget(CharSequence dashboardId, Widget widget, DashkuHandler<Widget> handler);
	
	/**
	 * Updates an existing widget in the Dashku server
	 * @param dashboardId The id of the dashboard that this widget belongs to 
	 * @param widget The widget to update
	 */
	public void updateWidget(CharSequence dashboardId, Widget widget);
	
	
	
	/**
	 * Deletes an existing widget from the Dashku server
	 * @param dashboardId The id of the dashboard that this widget belongs to 
	 * @param widgetId The id of the widget to delete
	 * @param handler the response listener
	 * @param errorHandler The error handler
	 */
	public void deleteWidget(CharSequence dashboardId, CharSequence widgetId, DashkuHandler<Void> handler, DashkuHandler<Exception> errorHandler);
	
	/**
	 * Deletes an existing widget from the Dashku server
	 * @param dashboardId The id of the dashboard that this widget belongs to 
	 * @param widgetId The id of the widget to delete
	 * @param handler the response listener
	 */
	public void deleteWidget(CharSequence dashboardId, CharSequence widgetId, DashkuHandler<Void> handler);
	
	/**
	 * Deletes an existing widget from the Dashku server
	 * @param dashboardId The id of the dashboard that this widget belongs to 
	 * @param widgetId The id of the widget to delete
	 */
	public void deleteWidget(CharSequence dashboardId, CharSequence widgetId);
	
	/**
	 * Transmits data to existing widgets
	 * @param handler the response listener
	 * @param errorHandler The error handler
	 * @param transmissions An array of transmissions
	 */
	public void transmit(DashkuHandler<Void> handler, DashkuHandler<Exception> errorHandler, Transmission...transmissions);
	
	
	/**
	 * Transmits data to existing widgets
	 * @param handler the response listener
	 * @param transmissions An array of transmissions
	 */
	public void transmit(DashkuHandler<Void> handler, Transmission...transmissions);
	
	/**
	 * Transmits data to existing widgets
	 * @param transmissions An array of transmissions
	 */
	public void transmit(Transmission...transmissions);
	
	
	/**
	 * Acquires the named resource from the server
	 * @param resourceUri The URI of the resource
	 * @param handler the response listener
	 * @param errorHandler The error handler
	 */
	public void getResource(CharSequence resourceUri, DashkuHandler<Resource> handler, DashkuHandler<Exception> errorHandler);
	
	/**
	 * Acquires the named resource from the server
	 * @param resourceUri The URI of the resource
	 * @param handler the response listener
	 */
	public void getResource(CharSequence resourceUri, DashkuHandler<Resource> handler);
	
	
	/**
	 * Simple data post
	 * @param uri The URI to post to
	 * @param data The data to post in JSON format
	 * @param handler the response listener
	 * @param errorHandler The error handler
	 */
	public void post(String uri, String data, DashkuHandler<Status> handler, DashkuHandler<Exception> errorHandler);
	
	/**
	 * Simple data post
	 * @param uri The URI to post to
	 * @param data The data to post in JSON format
	 * @param handler the response listener
	 */
	public void post(String uri, String data, DashkuHandler<Status> handler);
	
	/**
	 * Simple data post
	 * @param uri The URI to post to
	 * @param data The data to post in JSON format
	 */
	public void post(String uri, String data);
	

}
