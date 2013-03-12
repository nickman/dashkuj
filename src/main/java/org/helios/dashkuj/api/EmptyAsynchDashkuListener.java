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
package org.helios.dashkuj.api;

import java.util.Collection;

import org.helios.dashkuj.api.AsynchDashku.DomainObjectListener;
import org.helios.dashkuj.domain.Dashboard;

/**
 * <p>Title: EmptyAsynchDashkuListener</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.api.EmptyAsynchDashkuListener</code></p>
 */

public class EmptyAsynchDashkuListener implements DomainObjectListener<Collection<Dashboard>> {

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku.AsynchErrorHandler#onAsynchError(org.helios.dashkuj.api.AsynchDashku, java.lang.String, java.lang.Throwable, java.lang.Object[])
	 */
	@Override
	public void onAsynchError(AsynchDashku asynchDashku, String requestName, Throwable cause, Object... args) {
		/* No Op */
		
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku.DomainObjectListener#onResponse(java.lang.Object, org.helios.dashkuj.api.AsynchDashku)
	 */
	@Override
	public void onResponse(Collection<Dashboard> response, AsynchDashku asynchDashku) {
		/* No Op */
		
	}
	
	
	
//	/**
//	 * Retrieves all of the dashboards associated with the set Dashku API key
//	 * @param responseListener The listener that handles the async response 
//	 */
//	public void onDashboards(DomainObjectListener<Collection<Dashboard>> responseListener);
//	
//
//	
//	/**
//	 * Retrieves a dashboard, given the id of the dashboard.
//	 * @param dashboardId The id of the dashboard to retrieve
//	 * @param responseListener The listener that handles the async response 
//	 */
//	public void getDashboard(CharSequence dashboardId, DomainObjectListener<Dashboard> responseListener);
//
//	
//	
//	/**
//	 * Creates a new dashboard in the Dashku server.
//	 * <p>Note that currently Dashku ignores the <b><code>css</code></b> and <b><code>screenWidth</code></b> attributes. See Dashku Issue <a href="https://github.com/Anephenix/dashku/issues/16">16</a>.</p>
//	 * @param dashboard the dashboard domain model to create
//	 * @param responseListener The listener that handles the async response 
//	 */
//	public void createDashboard(Dashboard dashboard, DomainObjectListener<Dashboard> responseListener);
	
	
}
