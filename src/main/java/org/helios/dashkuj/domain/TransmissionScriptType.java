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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: TransmissionScriptType</p>
 * <p>Description: Enumerates the transmission script types that can be downloaded from the dashku server</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.domain.TransmissionScriptType</code></p>
 */

public enum TransmissionScriptType {
	/** A node.js script */
	NODE("js"),
	/** A coffeescript script */
	COFFEE("coffee"),
	/** A ruby script */
	RUBY("rb"),
	/** A php script */
	PHP("php"),
	/** A python script */
	PYTHON("py"),
	/** A groovy script */
	GROOVY("groovy"),
	/** A java scripting script */
	JAVA("jav");
	
	/** A map decoding the extension to the enum */
	public static final Map<String, TransmissionScriptType> EXT2ENUM;
	
	static {
		TransmissionScriptType[] values = TransmissionScriptType.values();
		Map<String, TransmissionScriptType> tmp = new HashMap<String, TransmissionScriptType>(values.length);
		for(TransmissionScriptType t: values) {
			tmp.put(t.extension, t);
		}
		EXT2ENUM = Collections.unmodifiableMap(tmp);
	}
	
	private TransmissionScriptType(String extension) {
		this.extension = extension;
	}
	
	private final String extension;
	
	
	/** The default transmission type */
	public static final TransmissionScriptType DEFAULT_TYPE = NODE;
	
	/**
	 * Retrieves the TransmissionScriptType for the passed extension
	 * @param ext The extension of the TransmissionScriptType, which will be trimmed and lowercased.
	 * @return the decoded TransmissionScriptType
	 */
	public static TransmissionScriptType forExtension(CharSequence ext) {
		if(ext==null) throw new IllegalArgumentException("The passed extension was null", new Throwable());
		String extension = ext.toString().trim().toLowerCase();
		TransmissionScriptType t = EXT2ENUM.get(extension);
		if(t==null) throw new IllegalArgumentException("The passed extension [" + extension + "] was not a valid TransmissionScriptType extension", new Throwable());
		return t;
	}

	/**
	 * Returns the extension of this script type
	 * @return the extension of this script type
	 */
	public String getExtension() {
		return extension;
	}
}
