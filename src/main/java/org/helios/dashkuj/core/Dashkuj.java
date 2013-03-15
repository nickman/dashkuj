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
package org.helios.dashkuj.core;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.helios.dashkuj.api.Dashku;
import org.helios.dashkuj.core.apiimpl.DashkuImpl;
import org.helios.dashkuj.domain.Dashboard;
import org.helios.dashkuj.domain.ScreenWidth;
import org.vertx.java.core.Vertx;

/**
 * <p>Title: Dashkuj</p>
 * <p>Description: The primary API interface for DashkuJ services.</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.core.Dashkuj</code></p>
 */

public class Dashkuj {
	/** The singleton instance */
	private static volatile Dashkuj instance = null;
	/** The singleton instance ctor lock*/
	private static final Object lock = new Object();
	
	/** The managing vertx */
	protected final Vertx vertx = Vertx.newVertx();
	
	static {
		System.setProperty("org.vertx.logger-delegate-factory-class-name", "org.vertx.java.core.logging.impl.SLF4JLogDelegateFactory");
	}
	
	/**
	 * Acquires the singleton Dashkuj instance
	 * @return the singleton Dashkuj instance
	 */
	public static Dashkuj getInstance() {
		if(instance==null) {
			synchronized(lock) {
				if(instance==null) {
					instance = new Dashkuj();
				}
			}
		}
		return instance;
	}
	
	public static void main(String[] args) {
		log("Dashkuj test");
		//Dashku d = Dashkuj.getInstance().getDashku("dfb6c8d9-58bc-42e1-b6df-3c587c9c4928", "dashku", 3000);
		Dashku d = Dashkuj.getInstance().getDashku("5750ac28-96fb-4af5-b218-6f855e03ebcf", "dashku", 3000);
		//Dashku d = Dashkuj.getInstance().getDashku("5750ac28-96fb-4af5-b218-6f855e03ebcf", "localhost", 8087);
		((DashkuImpl)d).setTimeout(10000);
		// curl -X POST -d "name=Account%20Management" "http://dashku:3000/api/dashboards?apiKey=5750ac28-96fb-4af5-b218-6f855e03ebcf"
//		
//		Collection<Dashboard> dboards = d.getDashboards();
//		log("Acquired [" + dboards.size() + "] Dashboards");
//		for(Dashboard db: dboards) {
//			Dashboard sdb = d.getDashboard(db.getId());
//			log("Single Dash:[" + sdb.getName() + "/" + sdb.getId());
//		}
		Dashboard newDash = new Dashboard();
		newDash.setName("JVM Monitor");
		newDash.setCss("#salesNumber {\n font-weight: bold; \n font-size: 24pt;\n}");
		newDash.setScreenWidth(ScreenWidth.fixed);
		String id = d.createDashboard(newDash);
		log("Created new dashboard:" + id + "  name:" + newDash.getName());
		
		newDash.setName(newDash.getName() + "2");
		d.updateDashboard(newDash);
		log("Updated dashboard:" + id + "  name:" + newDash.getName());
		
		id = d.deleteDashboard(newDash);
		log("Deleted dashboard:" + id);
		
		
	}
	
	public static void log(Object msg) {
		System.out.println(msg);
	}
	
	/**
	 * Creates a new Dashkuj
	 */
	private Dashkuj() {
		// TODO Auto-generated constructor stub
	}
	
	/** A cache of synch dashkus keyed by <b><code>host:port</code></b> */
	protected final Map<String, Dashku> dashkus = new ConcurrentHashMap<String, Dashku>();
	
	/**
	 * Acquires the Synchronous Dashku instance for the Dashku server at the passed host and port
	 * @param apiKey The dashku api key
	 * @param host The dashku server host or ip address
	 * @param port The dashku server port
	 * @return a synchronous dashku 
	 */
	public Dashku getDashku(String apiKey, String host, int port) {
		final String key = String.format("%s:%s", host, port);
		Dashku d = dashkus.get(key);
		if(d==null) {
			synchronized(dashkus) {
				d = dashkus.get(key);
				if(d==null) {
					d = new DashkuImpl(vertx.createHttpClient(), apiKey, host, port);
					dashkus.put(key, d);
				}
			}
		}
		return d;
	}

}
