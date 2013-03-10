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
package org.helios.dashkuj.domain;

import com.github.jmkgreen.morphia.annotations.Property;
import com.google.gson.annotations.SerializedName;

/**
 * <p>Title: DashboardId</p>
 * <p>Description: A simple wrapper for a dashku server supplied dashboard id. e.g.<br><b><code>{"dashboardId":"4fd1f55b7e9b8705a1000054"}</code></p></p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.domain.DashboardId</code></p>
 */

public class DashboardId {
	/** the ID of the dashboard being referenced */
	@Property("_id")
	@SerializedName("_id")	
	protected String dashboardId = null;

	/**
	 * Returns the ID of the dashboard being referenced
	 * @return the dashboardId
	 */
	public String getDashboardId() {
		return dashboardId;
	}
	
	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("DashboardID:%s", dashboardId);
	}

	
}
