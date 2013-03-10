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

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Property;
import com.google.gson.annotations.SerializedName;

/**
 * <p>Title: Widget</p>
 * <p>Description: Represents a widget in a dashboard</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.domain.Widget</code></p>
 */
@Entity
public class Widget extends AbstractDashkuDomainObject {
	/** The dashku unique identifier for this object */
	@Property("_id")
	@SerializedName("_id")
	protected String id = null;

	/** The widget's height */
	@Property("height")
	@SerializedName("height")
	protected int height = -1;
	/** The widget's width */
	@Property("width")
	@SerializedName("width")
	protected int width = -1;
	/** The widget's json template */
	@Property("json")
	@SerializedName("json")
	protected String json = null;
	/** The widget's script type */
	@Property("scriptType")
	@SerializedName("scriptType")
	protected String scriptType = null;
	/** The widget's script */
	@Property("script")
	@SerializedName("script")
	protected String script = null;
	/** The widget's scoped css */
	@Property("scopedCss")
	@SerializedName("scopedCss")
	protected String scopedCss = null;
	/** The widget's html */
	@Property("html")
	@SerializedName("html")
	protected String html = null;
	
	// http://dashku:3000/api/dashboards/513b2821a03ed86f05000011/widgets/513b2827a03ed86f05000021/downloads/dashku_513b2827a03ed86f05000021.js
	// http://dashku:3000/api/dashboards/513b2821a03ed86f05000011/widgets/513b2827a03ed86f05000021/downloads/dashku_513b2827a03ed86f05000021.coffee
	
	
	
	
	/**
	 * Copy Constructor
	 * @param widget a <code>Widget</code> object
	 */
	public Widget(Widget widget) {
		super(widget);
	    this.id = widget.id;
	    this.height = widget.height;
	    this.width = widget.width;
	    this.json = widget.json;
	    this.scriptType = widget.scriptType;
	    this.script = widget.script;
	    this.scopedCss = widget.scopedCss;
	    this.html = widget.html;
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
		builder.append("Widget [\n\theight:");
		builder.append(height);
		builder.append("\n\twidth:");
		builder.append(width);
		builder.append("\n\tjson:");
		builder.append(json);
		builder.append("\n\tscriptType:");
		builder.append(scriptType);
		builder.append("\n\tscript:");
		builder.append(script);
		builder.append("\n\tscopedCss:");
		builder.append(scopedCss);
		builder.append("\n\thtml:");
		builder.append(html);
		builder.append("\n\tid:");
		builder.append(id);
		builder.append("\n\tcreated:");
		builder.append(created);
		builder.append("\n\tlastUpdated:");
		builder.append(lastUpdated);
		builder.append("\n\tcss:");
		builder.append(css);
		builder.append("\n\tname:");
		builder.append(name);
		builder.append("\n]");
		return builder.toString();
	}



	/**
	 * Returns the widget's height
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}



	/**
	 * Sets the widget's height
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		dirty(this.height, height, "height");
		this.height = height;
	}



	/**
	 * Returns the widget's width
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}



	/**
	 * Sets the widget's width
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		dirty(this.width, width, "width");
		this.width = width;
	}



	/**
	 * Returns the widget's json
	 * @return the json
	 */
	public String getJson() {
		return json;
	}



	/**
	 * Sets the widget's json
	 * @param json the json to set
	 */
	public void setJson(String json) {
		dirty(this.json, json, "json");
		this.json = json;
	}



	/**
	 * Returns the widget's script type
	 * @return the scriptType
	 */
	public String getScriptType() {
		return scriptType;
	}



	/**
	 * Sets the widget's script type
	 * @param scriptType the scriptType to set
	 */
	public void setScriptType(String scriptType) {
		dirty(this.scriptType, scriptType, "scriptType");
		this.scriptType = scriptType;
	}



	/**
	 * Returns the widget's script
	 * @return the script
	 */
	public String getScript() {
		return script;
	}



	/**
	 * Sets the widget's script
	 * @param script the script to set
	 */
	public void setScript(String script) {
		dirty(this.script, script, "script");
		this.script = script;
	}



	/**
	 * Returns the widget's css
	 * @return the scopedCss
	 */
	public String getScopedCss() {
		return scopedCss;
	}



	/**
	 * Sets the widget's css
	 * @param scopedCss the scopedCss to set
	 */
	public void setScopedCss(String scopedCss) {
		dirty(this.scopedCss, scopedCss, "scopedCss");
		this.scopedCss = scopedCss;
	}



	/**
	 * Returns the widget's html 
	 * @return the html
	 */
	public String getHtml() {
		return html;
	}



	/**
	 * Sets the widget's html
	 * @param html the html to set
	 */
	public void setHtml(String html) {
		dirty(this.html, html, "html");
		this.html = html;
	}
	
	
	
	
	
//  "height": 180,
//  "width": 200,
//  "json": "{\n \"bigNumber\": 500,\n \"_id\": \"5139bd531b1b77bb06000015\",\n \"apiKey\": \"245ef354-3d60-42a3-b47e-78ee0159fda6\"\n}",
//  "scriptType": "javascript",
//  "script": "// The widget's html as a jQuery object\nvar widget = this.widget;\n\n// This runs when the widget is loaded\nthis.on('load', function(data){\n console.log('loaded'); \n});\n// This runs when the widget receives a transmission\nthis.on('transmission', function(data){\n widget.find('#bigNumber').text(data.bigNumber);\n});",
//  "scopedCSS": ".widget[data-id='5139bd531b1b77bb06000015'] #bigNumber {\n padding: 10px;\n margin-top: 50px;\n font-size: 36pt;\n font-weight: bold;\n}",
//  "css": "#bigNumber {\n padding: 10px;\n margin-top: 50px;\n font-size: 36pt;\n font-weight: bold;\n}",
//  "html": "<div id='bigNumber'></div>",
	


}
