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

import java.lang.management.ManagementFactory;

import org.helios.dashkuj.protocol.http.HTTPDashku;
import org.helios.dashkuj.redis.RedisPubSub;

import com.google.gson.JsonObject;

/**
 * <p>Title: CL</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.domain.CL</code></p>
 */

public class CL {

	/**
	 * Creates a new CL
	 */
	public CL() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		log("CL Test");
		try {
//			Mongo mongo = new Mongo("dashku", 27017);
//			Morphia morphia = new Morphia();
//			morphia.map(Dashboard.class);
//			Datastore ds = morphia.createDatastore(mongo, "dashku_development");
//			
//			Gson gson = new GsonBuilder().create();
//						
//			for(Dashboard db : ds.find(Dashboard.class).asList()) {
//				log(db);
//				log("====================");
//				log(gson.toJson(db));
//			}
			
			//HTTPDashku http = new HTTPDashku("31e3b92f-dcf3-468d-bd97-53327c6786a9", "dashku", 3000);
			HTTPDashku http = new HTTPDashku("f136167f-5026-440c-a77a-d38b5441206c", "dashku", 3000);
			String contentUri = "/api/dashboards/513bd839e9fc007c07000003/widgets/513e64d36ee3bab80600005c/downloads/dashku_513e64d36ee3bab80600005c.rb";
			log("Retrieving content as string: [" + contentUri + "]");
			String content = http.getResourceString(contentUri);
			log("Content:\n" + content);
//			RedisPubSub pubSub = RedisPubSub.getInstance("dashku", 6379).start();
//			pubSub.psubscribe("*");
//			pubSub.subscribe("*");
			
			Thread.sleep(12000000);
//			http.setTimeout(60000);
//			Dashboard d = new Dashboard();
//			d.setName("JVM Monitor");
//			d.setCss("#salesNumber {\n font-weight: bold; \n font-size: 24pt;\n}");
//			d.setScreenWidth(ScreenWidth.fluid);
//			http.createDashboard(d);
//			log("Created new dashboard:" + d);
//			Widget w = new Widget();
//			w.setCss("#heapSavant {\n font-weight: bold; \n font-size: 24pt;\n}");
//			w.setHeight(150);
//			w.setWidth(300);
//			w.setHtml(URLEncoder.encode("<div id=\"heapSavant\"></div>", "UTF-8"));
//			w.setJson(URLEncoder.encode("{max=100,alloc=60,used=30}", "UTF-8"));
//			w.setName("HeapSpace");
//			w.setScriptType(ScriptType.javascript);
//			w.setScript(URLEncoder.encode(new String(URLHelper.getBytesFromURL(URLHelper.toURL(new File("src/test/resources/scripts/js/newWidgetScript.js")))), "UTF-8"));
//			http.createWidget(d.getId(), w);
//			log("Created new widget:" + w);
//			
//			log("Deleting Widget");
//			String deletedWid = http.deleteWidget(d.getId(), w.getId());
//			log("Deleted Widget [" + deletedWid + "]");
//			
//			
//			log("Updating Dashboard");
//			d.setName("JVM2 Monitor");
//			http.updateDashboard(d);
//			log("Updated Dashboard:[" + d.getName() + "]");
//
//			
//			log("Deleting Dashboard");
//			String deletedDid = http.deleteDashboard(d.getId());
//			log("Deleted Dashboard:[" + deletedDid + "]");
//			
//			Thread.currentThread().join(100000);
			
 			JsonObject transmission = new JsonObject();
			JsonObject colours = new JsonObject();
			colours.addProperty("amount", "#51FF00");
			colours.addProperty("total", "#FF002B");
			
			transmission.addProperty("bigNumber", 50);
//			transmission.addProperty("amount", 50);
//			transmission.addProperty("total", 100);
//			colours.addProperty("amount", "#51FF00");
//			colours.addProperty("total", "#FF002B");
//			transmission.add("colours", colours);
			JsonObject msg = new JsonObject();
			msg.addProperty("message", "Hello Sean Shih");
			http.transmit("513e4333d7b1a5e709000087", msg);
			http.transmit("5138a8bf124965c506000037", transmission);
			for(int i = 0; i < 10000; i++) {
				transmission.addProperty("bigNumber", i);
				http.transmit("5138a8bf124965c506000037", transmission);
				JsonObject heap = new JsonObject();
				heap.addProperty("amount", ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed()*((i%30)+1));
				heap.addProperty("total", ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax());
				heap.add("colours", colours);
				http.transmit("5138a957124965c50600003d", heap);
				Thread.sleep(1000);
			}
//			Collection<Dashboard> dboards = http.getDashboards();
//			log("Retrieved [" + dboards.size() + "] Dashboard Instances");
//			Dashboard dboard = dboards.iterator().next();
//			log(dboard);
//			dboard = http.getDashboard(dboard.getId());
//			log("Retrieved [" + dboard.getName() + "] Dashboard Instance");
//			log(dboard);
//			Widget widget = dboard.widgets.get(0);
//			log("Updating Widget [" + widget.getName() + "]");
//			Random r = new Random(System.currentTimeMillis());
//			widget.setHeight(100 + Math.abs(r.nextInt(100)));
//			widget.setWidth(100 + Math.abs(r.nextInt(100))*2);	
//			widget.setName("FooBar" + Math.abs(r.nextInt(100)) + "/" + Math.abs(r.nextInt(100)));
//			Widget updatedWidget = http.updateWidget(dboard.getId(), widget);
//			log("Updated Widget:\n" + updatedWidget);
			
			
			
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		System.exit(1);

	}
	
	public static void log(Object msg) {
		System.out.println(msg);
	}

}
