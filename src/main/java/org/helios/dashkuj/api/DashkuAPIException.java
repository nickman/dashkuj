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

import org.helios.dashkuj.domain.Status;

/**
 * <p>Title: DashkuAPIException</p>
 * <p>Description: Base class for runtime exceptions thrown from Dashku API calls</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.api.DashkuAPIException</code></p>
 */

public class DashkuAPIException extends RuntimeException {

	/**  */
	private static final long serialVersionUID = 2808534493490498390L;

	/**
	 * Creates a new DashkuAPIException
	 */
	public DashkuAPIException() {
	}
	
	/**
	 * Creates a new DashkuAPIException
	 * @param apiStatus The API status returned by a failed API call
	 */
	public DashkuAPIException(Status apiStatus) {
		super(apiStatus.toString());
	}
	
	/**
	 * Creates a new DashkuAPIException
	 * @param apiStatus The API status returned by a failed API call
	 * @param cause The exception's underlying cause
	 */
	public DashkuAPIException(Status apiStatus, Throwable cause) {
		super(apiStatus.toString(), cause);
	}
	
	

	/**
	 * Creates a new DashkuAPIException
	 * @param message The exception message
	 */
	public DashkuAPIException(String message) {
		super(message);
	}

	/**
	 * Creates a new DashkuAPIException
	 * @param cause The exception's underlying cause
	 */
	public DashkuAPIException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates a new DashkuAPIException
	 * @param message The exception message
	 * @param cause The exception's underlying cause
	 */
	public DashkuAPIException(String message, Throwable cause) {
		super(message, cause);
	}

}
