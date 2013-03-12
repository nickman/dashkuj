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
package org.helios.dashkuj.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

/**
 * <p>Title: GsonFactory</p>
 * <p>Description: Singleton factory for configured {@link Gson} instances</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.json.GsonFactory</code></p>
 */

public class GsonFactory {
	/** The singleton instance */
	private static volatile GsonFactory instance = null;
	/** The singleton instance ctor lock */
	private static final Object lock = new Object();
	
	/** The configured builder */
	private final GsonBuilder builder;
	/** The configured printer */
	private final GsonBuilder printer;
	
	/** The timestamp format used by dashku */
	public static final String JS_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	/** Empty JSON const */
	public static final String EMPTY_JSON = "{}";
	
	/**
	 * Acquires the GsonFactory singleton instance
	 * @return the GsonFactory singleton instance
	 */
	public static GsonFactory getInstance() {
		if(instance==null) {
			synchronized(lock) {
				if(instance==null) {
					instance = new GsonFactory();
				}
			}
		}
		return instance;
	}
	
	/**
	 * Creates a new GsonFactory
	 */
	private GsonFactory() {
		builder = new GsonBuilder().setDateFormat(JS_DATE_FORMAT).serializeNulls();
		printer = new GsonBuilder().setDateFormat(JS_DATE_FORMAT).serializeNulls().setPrettyPrinting();
	}
	
	/**
	 * Returns a new {@link Gson} instance, configured for DashkuJ specifics
	 * @return a new {@link Gson} instance
	 */
	public Gson newGson() {
		return builder.create();
	}
	
	/**
	 * Returns a pretty printing Gson
	 * @return a pretty printing Gson
	 */
	public Gson printer() {
		return printer.create();
	}
	
	
	/**
	 * Renders the passed object to a pretty printed JSON string
	 * @param target the object to render
	 * @return a pretty printed JSON string
	 */
	public static String renderToJson(Object target) {
		if(target==null) return EMPTY_JSON;
		if(target instanceof CharSequence) {
			return getInstance().printer().toJson(new JsonParser().parse(target.toString()));
		}
		return getInstance().printer().toJson(target);
	}
	
}
