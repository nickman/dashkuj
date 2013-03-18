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
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.helios.dashkuj.api.AsynchDashku;
import org.helios.dashkuj.api.SynchDashku;
import org.helios.dashkuj.core.apiimpl.AsynchDashkuImpl;
import org.helios.dashkuj.core.apiimpl.SynchDashkuImpl;
import org.helios.dashkuj.util.URLHelper;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJSON;
import org.mozilla.javascript.ScriptableObject;
import org.vertx.java.core.Vertx;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
		SynchDashku d = Dashkuj.getInstance().getSynchDashku("dfb6c8d9-58bc-42e1-b6df-3c587c9c4928", "dashku", 3000);
		//Dashku d = Dashkuj.getInstance().getDashku("5750ac28-96fb-4af5-b218-6f855e03ebcf", "dashku", 3000);
		//Dashku d = Dashkuj.getInstance().getDashku("5750ac28-96fb-4af5-b218-6f855e03ebcf", "localhost", 8087);
		((SynchDashkuImpl)d).setTimeout(10000);
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
//		RhinoContextFactory rcf = new RhinoContextFactory();
//		ContextFactory.initGlobal(rcf);
//		Global global = Main.getGlobal();
//		Class<?> rvc = RhinoVerticle.class;
		try {
			//Require installRequire(final ClassLoader cl, Context cx, ScriptableObject scope) {
			// private static void loadScript(ClassLoader cl, Context cx, ScriptableObject scope, String scriptName) th
//			Method m = rvc.getDeclaredMethod("installRequire", ClassLoader.class, Context.class, ScriptableObject.class);
//			Method loadMethod = rvc.getDeclaredMethod("loadScript", ClassLoader.class, Context.class, ScriptableObject.class, String.class);
//			Field scopeThreadLocal = rvc.getDeclaredField("scopeThreadLocal");  scopeThreadLocal.setAccessible(true);
//			Field clThreadLocal = rvc.getDeclaredField("clThreadLocal");  clThreadLocal.setAccessible(true);
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
//		    load("core/buffer.js");
//		    load("core/net.js");
//		    load("core/http.js");
//		    load("core/streams.js");
//		    load("core/timers.js");
//		    load("core/utils.js");
//		    load("core/sockjs.js");
		    load("core/parse_tools.js");
//		    load("core/shared_data.js");
		    
		    ScriptableObject.putProperty(scope, "out", System.out);
		    ScriptableObject.putProperty(scope, "err", System.err);
		    
		    

		    String requireJs = URLHelper.getTextFromURL(ClassLoader.getSystemResource("require.js"));
		    String transmission = URLHelper.getTextFromURL(ClassLoader.getSystemResource("transmission.js"));
		    String bar = URLHelper.getTextFromURL(ClassLoader.getSystemResource("bar.js"));
		    String d3 = URLHelper.getTextFromURL(ClassLoader.getSystemResource("d3.js"));
		    cx.evaluateString(scope, requireJs, "require.js", 1, null);
		    
		    log("vertx.js loaded");
		    
		    
		    
		    

		    log("Issuing Post");
		    cx.evaluateString(scope, transmission, "transmission.js", 1, null);
		    Object transmissionData = scope.get("data");
		    cx.evaluateString(scope, bar, "bar.js", 1, null);
		    Object barData = scope.get("data");
		    cx.evaluateString(scope, d3, "d3.js", 1, null);
		    Object d3Data = scope.get("data");		    
		    Random random = new Random(System.currentTimeMillis());
		    
		    Object transmissionJson = NativeJSON.stringify(cx, scope, transmissionData, null, 2);
		    Object barJson = NativeJSON.stringify(cx, scope, barData, null, 2);
		    Object d3Json = NativeJSON.stringify(cx, scope, d3Data, null, 2);
		    
		    
		    
		    
		    JsonObject tran = new JsonParser().parse(transmissionJson.toString()).getAsJsonObject();
		    JsonObject b = new JsonParser().parse(barJson.toString()).getAsJsonObject();
		    JsonObject d3s = new JsonParser().parse(d3Json.toString()).getAsJsonObject();
		    for(int i = 0; i < 10000000; i++) {
		    	final long start = System.currentTimeMillis();
		    	b.addProperty("value", Math.abs(random.nextInt(10)));
		    	
		    	d.post("/api/transmission", b.toString());
		    	JsonObject dsData = d3s.get("data").getAsJsonObject();
		    	dsData.addProperty("Stream0", Math.abs(random.nextInt(10)));
		    	dsData.addProperty("Stream1", Math.abs(random.nextInt(10)));
		    	dsData.addProperty("Stream2", Math.abs(random.nextInt(10)));
		    	d.post("/api/transmission", d3s.toString());
		    	final long elapsed = System.currentTimeMillis()-start;
		    	tran.addProperty("bigNumber", elapsed);
		    	d.post("/api/transmission", tran.toString());
		    	//log("DSDATA:" + d3s.toString());
		    	Thread.sleep(2000);
		    }
		    
		    //Thread.sleep(4000);
		    
		    log("Done");
		    
			
		
			
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
	protected final Map<String, SynchDashku> synchDashkus = new ConcurrentHashMap<String, SynchDashku>();
	/** A cache of asynch dashkus keyed by <b><code>host:port</code></b> */
	protected final Map<String, AsynchDashku> asynchDashkus = new ConcurrentHashMap<String, AsynchDashku>();
	
	
	/**
	 * Acquires the Synchronous Dashku instance for the Dashku server at the passed host and port
	 * @param apiKey The dashku api key
	 * @param host The dashku server host or ip address
	 * @param port The dashku server port
	 * @return a synchronous dashku 
	 */
	public SynchDashku getSynchDashku(String apiKey, String host, int port) {
		final String key = String.format("%s:%s", host, port);
		SynchDashku d = synchDashkus.get(key);
		if(d==null) {
			synchronized(synchDashkus) {
				d = synchDashkus.get(key);
				if(d==null) {
					d = new SynchDashkuImpl(vertx.createHttpClient(), apiKey, host, port);
					synchDashkus.put(key, d);
				}
			}
		}
		return d;
	}
	
	/**
	 * Acquires the Synchronous Dashku instance for the Dashku server at the passed host and port
	 * @param apiKey The dashku api key
	 * @param host The dashku server host or ip address
	 * @param port The dashku server port
	 * @return a synchronous dashku 
	 */
	public AsynchDashku getAsynchDashku(String apiKey, String host, int port) {
		final String key = String.format("%s:%s", host, port);
		AsynchDashku d = asynchDashkus.get(key);
		if(d==null) {
			synchronized(asynchDashkus) {
				d = asynchDashkus.get(key);
				if(d==null) {
					d = new AsynchDashkuImpl(vertx.createHttpClient(), apiKey, host, port);
					asynchDashkus.put(key, d);
				}
			}
		}
		return d;
	}
	

}
