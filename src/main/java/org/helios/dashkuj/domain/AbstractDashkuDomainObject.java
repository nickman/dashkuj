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
import java.util.Date;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.github.jmkgreen.morphia.annotations.Property;
import com.google.gson.annotations.SerializedName;

/**
 * <p>Title: AbstractDashkuDomainObject</p>
 * <p>Description: Common base class for dashboards and widgets</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.domain.AbstractDashkuDomainObject</code></p>
 */

public abstract class AbstractDashkuDomainObject implements DashkuDomainObject {
	/** The creation timestamp for this object */
	@Property("createdAt")
	@SerializedName("createdAt")
	protected Date created = null;
	/** The last update timestamp for this object */
	@Property("updatedAt")
	@SerializedName("updatedAt")
	protected Date lastUpdated = null;
	/** The CSS content for this object */
	@Property("css")
	@SerializedName("css")
	protected String css = "";
	/** The name of this object */
	@Property("name")
	@SerializedName("name")
	protected String name = null;
	
	/** The names of the fields that are considered "dirty" */
	protected final Set<String> dirtyFields = new CopyOnWriteArraySet<String>();

	
	
	/**
	 * Copy Constructor
	 * @param abstractDashkuDomainObject a <code>AbstractDashkuDomainObject</code> object
	 */
	protected AbstractDashkuDomainObject(AbstractDashkuDomainObject abstractDashkuDomainObject) {
	    this.created = abstractDashkuDomainObject.created;
	    this.lastUpdated = abstractDashkuDomainObject.lastUpdated;
	    this.css = abstractDashkuDomainObject.css;
	    this.name = abstractDashkuDomainObject.name;
	}
	
	/**
	 * Updates the values in this domain object from another domain object
	 * @param abstractDashkuDomainObject the domain object to update from
	 */
	protected void updateFrom(AbstractDashkuDomainObject abstractDashkuDomainObject) {
	    this.created = abstractDashkuDomainObject.created;
	    this.lastUpdated = abstractDashkuDomainObject.lastUpdated;
	    this.css = abstractDashkuDomainObject.css;
	    this.name = abstractDashkuDomainObject.name;		
	}

	/**
	 * Creates a new AbstractDashkuDomainObject
	 */
	protected AbstractDashkuDomainObject() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.domain.DashkuDomainObject#getId()
	 */
	@Override
	public abstract String getId();

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.domain.DashkuDomainObject#getCreated()
	 */
	@Override
	public Date getCreated() {
		return created;
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.domain.DashkuDomainObject#getLastUpdated()
	 */
	@Override
	public Date getLastUpdated() {
		return lastUpdated;
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.domain.DashkuDomainObject#getCss()
	 */
	@Override
	public String getCss() {
		return css;
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.domain.DashkuDomainObject#setCss(java.lang.String)
	 */
	@Override
	public void setCss(String css) {
		dirty(this.css, css, "css");
		this.css = css;
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.domain.DashkuDomainObject#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.domain.DashkuDomainObject#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		dirty(this.name, name, "name");
		this.name = name;
	}
	
	
	/**
	 * Conditionally adds the field name to the dirty fields if the before and after values are not the same
	 * @param before The value of the field before a setter is called
	 * @param after The value of the field after a setter is called
	 * @param fieldName the field name
	 */
	protected void dirty(Object before, Object after, String fieldName) {
		if(before==null && after==null) return;
		if(before==null) dirtyFields.add(fieldName);
		else {
			if(!before.equals(after)) dirtyFields.add(fieldName);
		}
	}
	
	/**
	 * Indicates if the object has dirty fields
	 * @return true if the object has dirty fields
	 */
	public boolean isDirty() {
		return !dirtyFields.isEmpty();
	}
	
	/**
	 * Returns the dirty field names
	 * @return the dirty field names
	 */
	public Set<String> getDirtyFieldNames() {
		return Collections.unmodifiableSet(dirtyFields);
	}
	
	
	/**
	 * Clears the dirty fields.
	 */
	public void clearDirtyFields() {
		dirtyFields.clear();
	}

}
