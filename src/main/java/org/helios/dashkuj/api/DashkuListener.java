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
import org.helios.dashkuj.domain.Widget;

/**
 * <p>Title: DashkuListener</p>
 * <p>Description: Defines a listener that will listen on asynch dashku responses</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.api.DashkuListener</code></p>
 */

public interface DashkuListener {
	/**
	 * Callback when an asynch dashboard collection request completes
	 * @param dashboards the returned collection of dashboards
	 */
	public void onDashboardCollection(Collection<Dashboard> dashboards);
	
	/**
	 * Callback when an asynch dashboard request completes
	 * @param dashboard the returned dashboard
	 */
	public void onDashboard(Dashboard dashboard);
	
	/**
	 * Callback when an asynch widget request completes
	 * @param widget the returned widget
	 */
	public void onWidget(Widget widget);	
	
	/**
	 * Callback when an asynch void request completes
	 */
	public void onComplete();
	
	/**
	 * Callback when an asynch request fails 
	 * @param exception The cause of the failure
	 */
	public void onException(DashkuAPIException exception);
	
}
