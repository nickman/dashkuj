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
	 * @param responseListener The listener that handles the async response 
	 */
	public void getDashboards(DomainObjectListener<Collection<Dashboard>> responseListener);
	

	
	/**
	 * Retrieves a dashboard, given the id of the dashboard.
	 * @param dashboardId The id of the dashboard to retrieve
	 * @param responseListener The listener that handles the async response 
	 */
	public void getDashboard(CharSequence dashboardId, DomainObjectListener<Dashboard> responseListener);

	
	
	/**
	 * Creates a new dashboard in the Dashku server.
	 * <p>Note that currently Dashku ignores the <b><code>css</code></b> and <b><code>screenWidth</code></b> attributes. See Dashku Issue <a href="https://github.com/Anephenix/dashku/issues/16">16</a>.</p>
	 * @param dashboard the dashboard domain model to create
	 * @param responseListener The listener that handles the async response 
	 */
	public void createDashboard(Dashboard dashboard, DomainObjectListener<Dashboard> responseListener);
	
	/**
	 * Creates a new dashboard in the Dashku server, fire-n-forget style.
	 * <p>Note that currently Dashku ignores the <b><code>css</code></b> and <b><code>screenWidth</code></b> attributes. See Dashku Issue <a href="https://github.com/Anephenix/dashku/issues/16">16</a>.</p>
	 * @param dashboard the dashboard domain model to create
	 */
	public void createDashboard(Dashboard dashboard);
	
	
	/**
	 * Updates an existing dashboard in the Dashku server
	 * @param dashboard the dashboard to update
	 * @param responseListener The listener that handles the async response
	 */
	public void updateDashboard(Dashboard dashboard, DomainObjectListener<Dashboard> responseListener);
	
	/**
	 * Updates an existing dashboard in the Dashku server, fire-n-forget style.
	 * @param dashboard the dashboard to update
	 */
	public void updateDashboard(Dashboard dashboard);
	
	
	/**
	 * Deletes an existing dashboard from the Dashku server
	 * @param dashboard the dashboard to delete
	 * @param responseListener The listener that handles the async response
	 */
	public void deleteDashboard(Dashboard dashboard, DomainObjectListener<String> responseListener);
	
	/**
	 * Deletes an existing dashboard from the Dashku server, fire-n-forget style.
	 * @param dashboard the dashboard to delete
	 */
	public void deleteDashboard(Dashboard dashboard);
	
	
	/**
	 * Deletes an existing dashboard from the Dashku server
	 * @param dashboardId the id of the dashboard to delete
	 * @param responseListener The listener that handles the async response 
	 */
	public void deleteDashboard(CharSequence dashboardId, DomainObjectListener<String> responseListener);
	
	/**
	 * Deletes an existing dashboard from the Dashku server, fire-n-forget style.
	 * @param dashboardId the id of the dashboard to delete 
	 */
	public void deleteDashboard(CharSequence dashboardId);
	
	
	
	/**
	 * Creates a new widget in the Dashku server associated to the dashboard with the passed dashboard id
	 * @param dashboardId The id of the dashboard that this widget belongs to 
	 * @param widget The widget to create and add to the dashboard
	 * @param responseListener The listener that handles the async response
	 */
	public void createWidget(CharSequence dashboardId, Widget widget, DomainObjectListener<Widget> responseListener);
	
	/**
	 * Creates a new widget in the Dashku server associated to the dashboard with the passed dashboard id, fire-n-forget style.
	 * @param dashboardId The id of the dashboard that this widget belongs to 
	 * @param widget The widget to create and add to the dashboard
	 */
	public void createWidget(CharSequence dashboardId, Widget widget);
	
	
	/**
	 * Updates an existing widget in the Dashku server
	 * @param dashboardId The id of the dashboard that this widget belongs to 
	 * @param widget The widget to update
	 * @param responseListener The listener that handles the async response
	 */
	public void updateWidget(CharSequence dashboardId, Widget widget, DomainObjectListener<Widget> responseListener);
	
	/**
	 * Updates an existing widget in the Dashku server, fire-n-forget style.
	 * @param dashboardId The id of the dashboard that this widget belongs to 
	 * @param widget The widget to update
	 */
	public void updateWidget(CharSequence dashboardId, Widget widget);	
	
	/**
	 * Deletes an existing widget from the Dashku server
	 * @param dashboardId The id of the dashboard that this widget belongs to 
	 * @param widgetId The id of the widget to delete
	 * @param repsonseListener The listener that handles the async response 
	 */
	public void deleteWidget(CharSequence dashboardId, CharSequence widgetId, DomainObjectListener<String> repsonseListener);
	
	/**
	 * Deletes an existing widget from the Dashku server, fire-n-forget style.
	 * @param dashboardId The id of the dashboard that this widget belongs to 
	 * @param widgetId The id of the widget to delete
	 */
	public void deleteWidget(CharSequence dashboardId, CharSequence widgetId);
	
	/**
	 * Transmits data to existing widgets, fire-n-forget style.
	 * @param transmissions An array of transmissions
	 */
	public void transmit(Transmission...transmissions);
	
	
	/**
	 * Transmits data to existing widgets
	 * @param errorListener The asynch error listener
	 * @param transmissions An array of transmissions
	 */
	public void transmit(AsynchErrorHandler errorListener, Transmission...transmissions);
	
	/**
	 * Acquires the named resource from the server as a string
	 * @param resourceUri The URI of the resource
	 * @param repsonseListener The listener that handles the async response
	 */
	public void getResourceString(CharSequence resourceUri, AsynchHandler<Object> repsonseListener);
	
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
	 * <p>Title: DomainObjectListener</p>
	 * <p>Description: Callback definition for asynch request responses</p> 
	 * <p><code>org.helios.dashkuj.api.AsynchDashku.DomainObjectListener</code></p>
	 * @param <T> The expected type of the callback return value 
	 */
	public static interface DomainObjectListener<T> extends AsynchErrorHandler {
		/**
		 * Callback for asynch requests 
		 * @param response The asynch response
		 * @param asynchDashku The asynch dashku instance that returned the collection
		 */
		public void onResponse(T response, AsynchDashku asynchDashku);
	}	
	
//	/**
//	 * <p>Title: DashboardListener</p>
//	 * <p>Description: Callback definition for an asynch dashboard request</p> 
//	 * <p><code>org.helios.dashkuj.api.AsynchDashku.DashboardListener</code></p>
//	 */
//	public static interface DashboardListener<T> extends AsynchErrorHandler {
//		/**
//		 * Asynch callback for get/create/update dashboard calls.
//		 * @param dashboard The returned dashboard
//		 * @param asynchDashku The asynch dashku instance that returned the dashboard
//		 */
//		public void onResponse(/*Dashboard*/T dashboard, AsynchDashku asynchDashku);
//	}
	
	
//	/**
//	 * <p>Title: DashboardDeletionListener</p>
//	 * <p>Description: Callback definition for an asynch dashboard deletion request</p> 
//	 * <p><code>org.helios.dashkuj.api.AsynchDashku.DashboardDeletionListener</code></p>
//	 */
//	public static interface DashboardDeletionListener<T> extends AsynchErrorHandler {
//		/**
//		 * Asynch callback for delete dashboard calls.
//		 * @param dashboardId The returned dashboard id
//		 * @param asynchDashku The asynch dashku instance that returned the dashboard
//		 */
//		public void onResponse(String dashboardId, AsynchDashku asynchDashku);
//	}		

//	/**
//	 * <p>Title: WidgetListener</p>
//	 * <p>Description: Callback definition for an asynch widget request</p> 
//	 * <p><code>org.helios.dashkuj.api.AsynchDashku.WidgetListener</code></p>
//	 */
//	public static interface WidgetListener extends AsynchErrorHandler {
//		/**
//		 * Asynch callback for get/create/update widget calls.
//		 * @param widget The returned widget
//		 * @param asynchDashku The asynch dashku instance that returned the widget
//		 */
//		public void onResponse(Widget widget, AsynchDashku asynchDashku);
//	}		
	
//	/**
//	 * <p>Title: WidgetDeletionListener</p>
//	 * <p>Description: Callback definition for an asynch widget deletion request</p> 
//	 * <p><code>org.helios.dashkuj.api.AsynchDashku.WidgetDeletionListener</code></p>
//	 */
//	public static interface WidgetDeletionListener extends AsynchErrorHandler {
//		/**
//		 * Asynch callback for delete widget calls.
//		 * @param widgetId The returned widget id
//		 * @param asynchDashku The asynch dashku instance that returned the widget id
//		 */
//		public void onResponse(String widgetId, AsynchDashku asynchDashku);
//	}		
	
	/**
	 * <p>Title: AsynchErrorHandler</p>
	 * <p>Description: Callback definition for an asynch request failure</p> 
	 * <p><code>org.helios.dashkuj.api.AsynchErrorHandler</code></p>
	 */
	public static interface AsynchErrorHandler {
		/**
		 * Callback when an asynchronous dashku request fails
		 * @param asynchDashku The asynch dashku instance that returned the collection
		 * @param requestName The failed request method name
		 * @param cause The root cause of the failure
		 * @param args The original arguments to the request
		 */
		public void onAsynchError(AsynchDashku asynchDashku, String requestName, Throwable cause, Object...args);
	}
	
//	/**
//	 * <p>Title: ResourceListener</p>
//	 * <p>Description: Callback definition for an asynch resource request</p> 
//	 * <p><code>org.helios.dashkuj.api.AsynchDashku.ResourceListener</code></p>
//	 */
//	public static interface ResourceListener extends AsynchErrorHandler {
//		/**
//		 * Asynch callback for resource request calls.
//		 * @param resource The returned resource
//		 * @param asynchDashku The asynch dashku instance that returned the widget
//		 */
//		public void onResponse(String resource, AsynchDashku asynchDashku);
//	}			

//	/**
//	 * <p>Title: AsynchDashkuListener</p>
//	 * <p>Description: An aggregate listener for all asynch dashku callbacks</p> 
//	 * <p><code>org.helios.dashkuj.api.AsynchDashku.AsynchDashkuListener</code></p>
//	 */
//	public static interface AsynchDashkuListener extends WidgetDeletionListener, WidgetListener, DashboardDeletionListener, DashboardListener, DashboardCollectionListener, ResourceListener {
//		
//	}
	

}
