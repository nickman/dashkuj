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

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.helios.dashkuj.json.GsonFactory;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.vertx.java.core.buffer.Buffer;

import com.github.jmkgreen.morphia.annotations.Embedded;
import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.annotations.Property;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

/**
 * <p>Title: Dashboard</p>
 * <p>Description: Represents a Dashku dashboard</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.domain.Dashboard</code></p>
 */
@Entity(value="dashboards", noClassnameStored=true)
public class Dashboard extends  AbstractDashkuDomainObject {
	/** The dashku unique identifier for this object */
	@Id()
	@Property("_id")
	@SerializedName("_id")
	protected String id = null;
	
	/** The screenwidth definition for this dashboard */
	@Property("screenWidth")
	@SerializedName("screenWidth")
	protected String screenWidth = ScreenWidth.fixed.name();

	/** The id of the user that this dashboard is owned by */
	@Property("userId")
	@SerializedName("userId")
	protected String userId = null;
//	/** A map of the widgets in this dashboard, keyed by the widget id */
//	protected Map<String, Widget> widgets = new ConcurrentHashMap<String, Widget>();
	@Embedded("widgets")
	protected final List<Widget> widgets = new CopyOnWriteArrayList<Widget>();
	
	/** A map of widgets keyed by the widget id */
	protected final Map<String, Widget> widgetsById = new ConcurrentHashMap<String, Widget>();

	
	/** The type of a Status */
	public static final TypeToken<Status> STATUS_TYPE = new TypeToken<Status>(){/* No Op */};	
	/** The type of a DashboardId */
	public static final TypeToken<DashboardId> DASHBOARD_ID_TYPE = new TypeToken<DashboardId>(){/* No Op */};	
	/** The type of a WidgetId */
	public static final TypeToken<WidgetId> WIDGET_ID_TYPE = new TypeToken<WidgetId>(){/* No Op */};	
	/** The type of a dashboard */
	public static final TypeToken<Dashboard> DASHBOARD_TYPE = new TypeToken<Dashboard>(){/* No Op */};	
	/** The type of a collection of dashboards */
	public static final TypeToken<Collection<Dashboard>> DASHBOARD_COLLECTION_TYPE = new TypeToken<Collection<Dashboard>>(){/* No Op */};
	/** The type of a collection of widgets */
	public static final TypeToken<Collection<Widget>> WIDGET_COLLECTION_TYPE = new TypeToken<Collection<Widget>>(){/* No Op */};
	/** The type of a widget */
	public static final TypeToken<Widget> WIDGET_TYPE = new TypeToken<Widget>(){/* No Op */};
	
	/** An unmarshaller for dashboard collections */
	public static final DomainUnmarshaller<Collection<Dashboard>> DASHBOARD_COLLECTION_UNMARSHALLER = new DomainUnmarshaller<Collection<Dashboard>>() {
		public Collection<Dashboard> unmarshall(Buffer buffer) {
			InputStreamReader jsonReader = new InputStreamReader(new ChannelBufferInputStream(buffer.getChannelBuffer(), buffer.length()));
			return GsonFactory.getInstance().newGson().fromJson(jsonReader, Dashboard.DASHBOARD_COLLECTION_TYPE.getType());
		}
				
	};
	
	/** An unmarshaller for dashboards */
	public static final DomainUnmarshaller<Dashboard> DASHBOARD_UNMARSHALLER = new DomainUnmarshaller<Dashboard>() {
		public Dashboard unmarshall(Buffer buffer) {
			InputStreamReader jsonReader = new InputStreamReader(new ChannelBufferInputStream(buffer.getChannelBuffer(), buffer.length()));
			return GsonFactory.getInstance().newGson().fromJson(jsonReader, Dashboard.DASHBOARD_TYPE.getType());
		}
				
	};
	
	
	/**
	 * <p>Title: DashboardTypeAdapter</p>
	 * <p>Description: A custom deserializer for dashboards to handle some post-creation bits-n-pieces.</p> 
	 * <p>Company: Helios Development Group LLC</p>
	 * @author Whitehead (nwhitehead AT heliosdev DOT org)
	 * <p><code>org.helios.dashkuj.domain.Dashboard.DashboardTypeAdapter</code></p>
	 */
	public static class DashboardTypeAdapter implements JsonSerializer<Dashboard>, JsonDeserializer<Dashboard> {
		/**
		 * {@inheritDoc}
		 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
		 */
		@Override
		public JsonElement serialize(Dashboard src, Type typeOfSrc, JsonSerializationContext context) {
			return context.serialize(src, Dashboard.class);
		}
		/**
		 * {@inheritDoc}
		 * @see com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement, java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)
		 */
		@Override
		public Dashboard deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			Dashboard d = GsonFactory.getInstance().newNoSerGson().fromJson(json, Dashboard.class);
			for(Widget w: d.widgets) {
				d.widgetsById.put(w.getId(), w);
			}
			return d;
		}
	}
	
	

	/**
	 * Creates a new Dashboard
	 */
	public Dashboard() {
		
	}
	
	/**
	 * Copy Constructor
	 * @param dashboard a <code>Dashboard</code> object
	 */
	public Dashboard(Dashboard dashboard) {
		super(dashboard);
	    this.id = dashboard.id;
	    this.screenWidth = dashboard.screenWidth;
	    this.userId = dashboard.userId;
	}
	
	/**
	 * Updates this dashboard from another transient dashboard
	 * @param dashboard the dashboard to update from
	 * @return this dashboard
	 */
	public Dashboard updateFrom(Dashboard dashboard) {
		super.updateFrom(dashboard);
	    this.id = dashboard.id;
	    this.screenWidth = dashboard.screenWidth;
	    this.userId = dashboard.userId;
	    clearDirtyFields();
	    return this;
	}



	public static void main(String[] args) {
		System.out.println("DASHBOARD_COLLECTION_TYPE:" + DASHBOARD_COLLECTION_TYPE.getClass().getName() + "/" + DASHBOARD_COLLECTION_TYPE);
	}
	
	
	
	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.domain.AbstractDashkuDomainObject#getId()
	 */
	@Override
	public String getId() {
		return id;
	}
	
	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Dashboard [\n\tscreenWidth=");
		builder.append(screenWidth);
		builder.append("\n\tuserId=");
		builder.append(userId);
		builder.append("\n\tid=");
		builder.append(id);
		builder.append("\n\tcreated=");
		builder.append(created);
		builder.append("\n\tlastUpdated=");
		builder.append(lastUpdated);
		builder.append("\n\tcss=");
		builder.append(css==null ? "<empty>" : ("" + css.length() + " chars"));
		builder.append("\n\tname=");
		builder.append(name);
		if(!widgetsById.isEmpty()) {
			builder.append("\n\twidgets=");
			for(Map.Entry<String, Widget> entry: widgetsById.entrySet()) {
				builder.append("\n\t\t").append(entry.getKey()).append(":").append(entry.getValue().getName());				
			}
		}		
		builder.append("\n]");
		return builder.toString();
	}
	
	/**
	 * Returns the dashboard screen width
	 * @return the screenWidth the dashboard screen width
	 */
	public String getScreenWidth() {
		return screenWidth;
	}

	/**
	 * Sets the dashboard screen width
	 * @param screenWidth the dashboard screen width
	 */
	public void setScreenWidth(ScreenWidth screenWidth) {
		dirty(this.screenWidth, screenWidth, "screenWidth");
		this.screenWidth = screenWidth.name();
	}

	
	
	
	
	
	
}


//[
//
//  {
//      "_id": "5139bcfe1b1b77bb06000003",
//      "createdAt": "2013-03-08T10:27:10.638Z",
//      "css": "\n\nYou can use custom CSS to style the dashboard as you like \n\nMake the changes that you like, then close the editor when you are happy.\n\nUncomment the block below to see the changes in real-time */\n\n/*\n\nbody {\n background: #111;\n} \n\n",
//      "name": "Your Dashboard",
//      "screenWidth": "fixed",
//      "updatedAt": "2013-03-08T10:27:10.638Z",
//      "userId": "5139bcfe1b1b77bb06000002",
//      "widgets": [
//          {
//              "_id": "5139bd531b1b77bb06000015",
//              "userId": "5139bcfe1b1b77bb06000002",
//              "widgetTemplateId": "5139bd32ddfc5ad60600000b",
//              "updatedAt": "2013-03-08T10:28:35.666Z",
//              "createdAt": "2013-03-08T10:28:35.666Z",
//              "height": 180,
//              "width": 200,
//              "json": "{\n \"bigNumber\": 500,\n \"_id\": \"5139bd531b1b77bb06000015\",\n \"apiKey\": \"245ef354-3d60-42a3-b47e-78ee0159fda6\"\n}",
//              "scriptType": "javascript",
//              "script": "// The widget's html as a jQuery object\nvar widget = this.widget;\n\n// This runs when the widget is loaded\nthis.on('load', function(data){\n console.log('loaded'); \n});\n// This runs when the widget receives a transmission\nthis.on('transmission', function(data){\n widget.find('#bigNumber').text(data.bigNumber);\n});",
//              "scopedCSS": ".widget[data-id='5139bd531b1b77bb06000015'] #bigNumber {\n padding: 10px;\n margin-top: 50px;\n font-size: 36pt;\n font-weight: bold;\n}",
//              "css": "#bigNumber {\n padding: 10px;\n margin-top: 50px;\n font-size: 36pt;\n font-weight: bold;\n}",
//              "html": "<div id='bigNumber'></div>",
//              "name": "Big Number"
//          }
//      ]
//  }
//
//]
// 
