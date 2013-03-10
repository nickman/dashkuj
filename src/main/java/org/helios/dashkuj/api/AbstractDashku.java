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
package org.helios.dashkuj.api;

import java.net.URL;
import java.util.Collection;

import org.helios.dashkuj.domain.Dashboard;
import org.helios.dashkuj.domain.Transmission;
import org.helios.dashkuj.domain.Widget;

/**
 * <p>Title: AbstractDashku</p>
 * <p>Description: Abstract base class for {@link Dashku} implementations</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.api.AbstractDashku</code></p>
 */

public abstract class AbstractDashku implements Dashku {
	/** The API key */
	protected final String apiKey;

	/**
	 * Creates a new AbstractDashku
	 * @param apiKey The dashku api key
	 */
	protected AbstractDashku(String apiKey) {
		if(apiKey==null || apiKey.trim().isEmpty()) throw new IllegalArgumentException("The passed APIKey was null", new Throwable());
		this.apiKey = apiKey;
		
	}




	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#updateDashboard(org.helios.dashkuj.domain.Dashboard)
	 */
	@Override
	public void updateDashboard(Dashboard dashboard) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#deleteDashboard(org.helios.dashkuj.domain.Dashboard)
	 */
	@Override
	public void deleteDashboard(Dashboard dashboard) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#deleteDashboard(java.lang.CharSequence)
	 */
	@Override
	public void deleteDashboard(CharSequence dashboardId) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#createWidget(java.lang.CharSequence, org.helios.dashkuj.domain.Widget)
	 */
	@Override
	public String createWidget(CharSequence dashboardId, Widget widget) {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#deleteWidget(java.lang.CharSequence, java.lang.CharSequence)
	 */
	@Override
	public void deleteWidget(CharSequence dashboardId, CharSequence widgetId) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.Dashku#transmit(org.helios.dashkuj.domain.Transmission[])
	 */
	@Override
	public void transmit(Transmission... transmissions) {
		// TODO Auto-generated method stub
		
	}

}
