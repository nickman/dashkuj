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

import java.util.Date;

/**
 * <p>Title: DashkuDomainObject</p>
 * <p>Description: Common interface for all Dashku domain objects</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.domain.DashkuDomainObject</code></p>
 */

public interface DashkuDomainObject {
	/**
	 * Returns the id of the object, or null if the object has never been persisted
	 * @return the id of the object
	 */
	public String getId();
	/**
	 * Returns the create timestamp of the object, or null if the object has never been persisted
	 * @return the create timestamp of the object
	 */
	public Date getCreated();
	/**
	 * Returns the last update timestamp of the object, or null if the object has never been persisted
	 * @return the last update of the object
	 */
	public Date getLastUpdated();
	
	/**
	 * Returns the object's CSS content
	 * @return the object's CSS content
	 */
	public String getCss();

	/**
	 * Sets the object's CSS content
	 * @param css the CSS content to set
	 */
	public void setCss(String css);

	/**
	 * Returns the name of the object
	 * @return the name of the object
	 */
	public String getName();

	/**
	 * Sets the name of the object
	 * @param name the name to set
	 */
	public void setName(String name);
	
}
