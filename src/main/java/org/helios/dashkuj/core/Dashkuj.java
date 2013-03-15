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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.helios.dashkuj.api.Dashku;
import org.helios.dashkuj.core.apiimpl.DashkuImpl;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.tools.shell.Global;
import org.mozilla.javascript.tools.shell.Main;
import org.vertx.java.core.Vertx;
import org.vertx.java.deploy.impl.VertxLocator;
import org.vertx.java.deploy.impl.rhino.RhinoContextFactory;
import org.vertx.java.deploy.impl.rhino.RhinoVerticle;

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
	
	protected static Context cx = null;
	protected static ScriptableObject scope = null;
	
	public static void main(String[] args) {
		log("Dashkuj test");
		Dashku d = Dashkuj.getInstance().getDashku("dfb6c8d9-58bc-42e1-b6df-3c587c9c4928", "dashku", 3000);
		//Dashku d = Dashkuj.getInstance().getDashku("5750ac28-96fb-4af5-b218-6f855e03ebcf", "dashku", 3000);
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
//		Dashboard newDash = new Dashboard();
//		newDash.setName("JVM Monitor");
//		newDash.setCss("#salesNumber {\n font-weight: bold; \n font-size: 24pt;\n}");
//		newDash.setScreenWidth(ScreenWidth.fixed);
//		String id = d.createDashboard(newDash);
//		log("Created new dashboard:" + id + "  name:" + newDash.getName());
//		
//		newDash.setName(newDash.getName() + "2");
//		d.updateDashboard(newDash);
//		log("Updated dashboard:" + id + "  name:" + newDash.getName());
//		
//		Widget w = new Widget();
//        w.setCss("#salesNumber {  font-weight: bold;  font-size: 24pt;}");
//        w.setHeight(150);
//        w.setWidth(300);
//        w.setHtml("<div id='salesNumber'></div>");
//        w.setJson("{  \"revenue\": \"346729.00\"}");
//        w.setName("New Account Sales");
//        w.setScriptType(ScriptType.javascript);
//        w.setScript("var widget = this.widget;this.on('load', function(data){  // Nothing to do});this.on('transmission', function(data){  var salesNumber = widget.find('#salesNumber');  salesNumber.text('$'+data.revenue).hide().fadeIn();});");		
//		d.createWidget(newDash.getId(), w);
//		
//		log("Created Widget[" + w.getName() + "/" + w.getId());
//		
//		log("Fetching js script for widget [" + w.getName() + "/" + w.getId());
//		Resource resource = d.getResource(w.getScriptURI(TransmissionScriptType.NODE));
//		log("Acquired Script " + resource);
//		
//		w.setName("JVM Memory");
//		w.setWidth(377);
//		d.updateWidget(newDash.getId(), w);
//		
//		log("Updated Widget[" + w.getName() + "/" + w.getId());
//		
//		log("Deleting Widget[" + w.getName() + "/" + w.getId());
//		
//		d.deleteWidget(newDash.getId(), w.getId());
//		
//		log("Deleted Widget[" + w.getName() + "/" + w.getId());
//		
//		
//		
//		id = d.deleteDashboard(newDash);
//		log("Deleted dashboard:" + id);
		
//		ScriptEngineManager sem = new ScriptEngineManager();
//		
//		for(ScriptEngineFactory sef: sem.getEngineFactories()) {
//			//log("ScriptEngine:" + sef.getEngineName() + " (" + sef.getEngineVersion() + "]");			
//		}
//		
//		ScriptEngine se = sem.getEngineByExtension("js");
//		log("ScriptEngine:" + se.getFactory().getEngineName() + " (" + se.getFactory().getEngineVersion() + "]");
//		try {
//			InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("vertx.js");
//			se.eval(new InputStreamReader(is));
//			
//			log("vertx.js loaded");
//		} catch (Exception ex) {
//			ex.printStackTrace(System.err);
//		}
		RhinoContextFactory rcf = new RhinoContextFactory();
		ContextFactory.initGlobal(rcf);
		Global global = Main.getGlobal();
		Class<?> rvc = RhinoVerticle.class;
		try {
			//Require installRequire(final ClassLoader cl, Context cx, ScriptableObject scope) {
			// private static void loadScript(ClassLoader cl, Context cx, ScriptableObject scope, String scriptName) th
			Method m = rvc.getDeclaredMethod("installRequire", ClassLoader.class, Context.class, ScriptableObject.class);
			Method loadMethod = rvc.getDeclaredMethod("loadScript", ClassLoader.class, Context.class, ScriptableObject.class, String.class);
			Field scopeThreadLocal = rvc.getDeclaredField("scopeThreadLocal");  scopeThreadLocal.setAccessible(true);
			Field clThreadLocal = rvc.getDeclaredField("clThreadLocal");  clThreadLocal.setAccessible(true);
//			  private static ThreadLocal<ScriptableObject> scopeThreadLocal = new ThreadLocal<>();
//			  private static ThreadLocal<ClassLoader> clThreadLocal = new ThreadLocal<>();

//			((ThreadLocal<ClassLoader>)clThreadLocal.get(null)).set(ClassLoader.getSystemClassLoader());
//			
//			
//			
//			m.setAccessible(true);
//			loadMethod.setAccessible(true);
//			
//			
//			
//			InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("vertx.js");
			cx = Context.enter();			
			scope = cx.initStandardObjects();
//			((ThreadLocal<ScriptableObject>)scopeThreadLocal.get(null)).set(scope);
//			Require require = (Require) m.invoke(null, rvc.getClassLoader(), cx, scope);		      

		      //addStandardObjectsToScope(scope);
//		      scope.defineFunctionProperties(new String[]{"load"}, RhinoVerticle.class, ScriptableObject.DONTENUM);
//
		    //loadMethod.invoke(null, ClassLoader.getSystemClassLoader(), cx, scope, "vertx.js");
		    load("core/buffer.js");
		    //load("core/event_bus.js");
		    load("core/net.js");
		    load("core/http.js");
		    load("core/streams.js");
		    load("core/timers.js");
		    load("core/utils.js");
		    load("core/sockjs.js");
		    load("core/parse_tools.js");
		    load("core/shared_data.js");
		    //load("core/filesystem.js");
		    //load("core/deploy.js");
		    //load("core/logger.js");
		    //load("core/env.js");
		    
		    String post = "var require = function(x) { var poster = {};  var httpClient = vertx.createHttpClient(); httpClient.setHost('dashku'); httpClient.setPort(3000);\n" + 
		    		"poster.httpClient = httpClient;\n" + 
		    		"poster.post = function(postReq) { var request = httpClient.post('POST', postReq.url); request.setChunked(true); request.putHeader('Content-Type', 'application/json'); request.write(postReq.body); request.end(); return request;}\n" + 
		    		"return poster }\n" + 
		    		"";

		    VertxLocator.vertx = Vertx.newVertx();
		    
		    ScriptableObject.putProperty(scope, "out", System.out);
		    
		    cx.evaluateString(scope, post, "require.js", 1, null);
		    
		    String node = "var request = require('request');\n" + 
		    		"var data = {\n" + 
		    		"\"value\": 40,\n" + 
		    		"\"_id\": \"5141e43cb69129400b000067\",\n" + 
		    		"\"apiKey\": \"dfb6c8d9-58bc-42e1-b6df-3c587c9c4928\"\n" + 
					"};\n" + 
					"request.post({url: \"http://dashku:3000/api/transmission\", body: data, json: true});\n" + 
					
					"out.println('Request Complete:' + JSON.stringify(request));";
			
			
		    cx.evaluateString(scope, node, "node.js", 1, null);
		    
			//
		
			log("vertx.js loaded");
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		
	}
	
	public static void  load(String name) {
		try {
			InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(name);
			cx.evaluateReader(scope, new InputStreamReader(is), name, 1, null);
			//cx.compileReader(new InputStreamReader(is), name, 1, null);
			log("\t\tLoaded [" + name + "]");
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/*
    load("core/buffer.js");
    load("core/event_bus.js");
    load("core/net.js");
    load("core/http.js");
    load("core/streams.js");
    load("core/timers.js");
    load("core/utils.js");
    load("core/sockjs.js");
    load("core/parse_tools.js");
    load("core/shared_data.js");
    load("core/filesystem.js");
    load("core/deploy.js");
    load("core/logger.js");
    load("core/env.js");

	 */
	
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
