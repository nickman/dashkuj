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
package org.helios.dashkuj.domain;

import com.google.gson.reflect.TypeToken;

/**
 * <p>Title: Resource</p>
 * <p>Description: An miscellaneous/untyped resource retrieved from dashku</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.domain.Resource</code></p>
 */

public class Resource {
	/** The bytes of the retrieved content */
	protected final byte[] content;
	/** The server advertised type of the retrieved content */
	protected final String contentType;
	
	/** The type of a resource */
	public static final TypeToken<Resource> RESOURCE_TYPE = new TypeToken<Resource>(){/* No Op */};
	
	

	/**
	 * Creates a new Resource
	 * @param content The bytes of the retrieved content 
	 * @param contentType The server advertised type of the retrieved content
	 */
	public Resource(byte[] content, String contentType) {
		this.content = content;
		this.contentType = contentType;
	}
	/**
	 * Returns the bytes of the retrieved content
	 * @return the bytes of the retrieved content
	 */
	public byte[] getContent() {
		return content;
	}
	/**
	 * Returns the server advertised type of the retrieved content
	 * @return the server advertised type of the retrieved content
	 */
	public String getContentType() {
		return contentType;
	}
	
	@Override
	public String toString() {
		return String.format("Resource [content=%s, contentType=%s]",
				content==null||content.length==0 ? "<no content>" : "" + content.length + " bytes", contentType);
	}
	
	
	
	
	
	
	
}
