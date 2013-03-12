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

import org.helios.dashkuj.api.AsynchDashku.AsynchDashkuListener;
import org.helios.dashkuj.domain.Dashboard;
import org.helios.dashkuj.domain.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Title: EmptyAsynchDashkuListener</p>
 * <p>Description: An empty implementation of the aggregate asynch dashku response listener</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.api.EmptyAsynchDashkuListener</code></p>
 */

public class EmptyAsynchDashkuListener implements AsynchDashkuListener {
	
	/** Instance logger */
	protected final Logger log = LoggerFactory.getLogger(getClass());
	// 
	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku.WidgetDeletionListener#onResponse(java.lang.String, org.helios.dashkuj.api.AsynchDashku)
	 */
	@Override
	public void onResponse(String widgetId, AsynchDashku asynchDashku) {
		/* No Op */

	}

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
	 * @see org.helios.dashkuj.api.AsynchDashku.WidgetListener#onResponse(org.helios.dashkuj.domain.Widget, org.helios.dashkuj.api.AsynchDashku)
	 */
	@Override
	public void onResponse(Widget widget, AsynchDashku asynchDashku) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku.DashboardListener#onResponse(org.helios.dashkuj.domain.Dashboard, org.helios.dashkuj.api.AsynchDashku)
	 */
	@Override
	public void onResponse(Dashboard dashboard, AsynchDashku asynchDashku) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku.DashboardCollectionListener#onResponse(java.util.Collection, org.helios.dashkuj.api.AsynchDashku)
	 */
	@Override
	public void onResponse(Collection<Dashboard> dashboards, AsynchDashku asynchDashku) {
		/* No Op */

	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AsynchDashku.ResourceListener#onResponse(java.lang.String, org.helios.dashkuj.api.AsynchDashku)
	 */
	@Override
	public void onResponse(String resource, AsynchDashku asynchDashku) {
		/* No Op */
		
	}

}
