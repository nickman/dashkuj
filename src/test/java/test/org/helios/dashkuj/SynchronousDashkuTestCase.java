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
package test.org.helios.dashkuj;

import java.util.Collection;

import junit.framework.Assert;

import org.helios.dashkuj.api.SynchDashku;
import org.helios.dashkuj.core.apiimpl.SynchDashkuImpl;
import org.helios.dashkuj.domain.Dashboard;
import org.helios.dashkuj.domain.DomainRepository;
import org.junit.Before;
import org.junit.Test;

/**
 * <p>Title: SynchronousDashkuTestCase</p>
 * <p>Description: Test cases for {@link SynchDashkuImpl}</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>test.org.helios.dashkuj.SynchronousDashkuTestCase</code></p>
 */

public class SynchronousDashkuTestCase extends BaseTest {
	/** The synchronous dashku */
	protected SynchDashku sd = null;
	/** The dashku repository */
	protected DomainRepository repo = null;
	
	/**
	 * Acquires the synchronous dashku
	 */
	@Before
	public void getDashku()  {
		if(sd==null) {
			sd = DASH.getSynchDashku(defaultApiKey, defaultDashkuHost, defaultDashkuPort);
			repo = DomainRepository.getInstance(defaultDashkuHost, defaultDashkuPort);
		}
		repo.flush();
	}

	/**
	 * Tests getDashboards
	 */
	@Test
	public void getDashboards() {
		Collection<Dashboard> dashboards = sd.getDashboards();
		for(Dashboard d: dashboards) {
			log("D1 ID:[" + d.getId() + "]");
			Assert.assertNotNull("The API Dash was null", d);
			log("Fetching DB Dash [" + d.getId() + "]");
			Dashboard dbD = getDbDashboard(d.getId());			
			Assert.assertNotNull("The DB Dash was null for id [" + d.getId() + "]" , dbD);
			compareDashboards(d, dbD);
		}
		Collection<Dashboard> repoDashboards = repo.getDashboards();
		Assert.assertEquals("The number of dashboards is not the same from the API as in the repo", dashboards.size(), repoDashboards.size());
		for(Dashboard d: dashboards) {
			compareDashboards(d, repo.getDashboard(d.getId()));
		}
		
	}
	
	/**
	 * Tests getDashboard driving by api key
	 */
	@Test
	public void getDashboardUsingApiKey() {
		Dashboard d1 = getDbDashboardsByApiKey(defaultApiKey).iterator().next();		
		Dashboard d2 = sd.getDashboard(d1.getId());
		compareDashboards(d1, d2);
	}
	
	/**
	 * Tests getDashboard driving by user name
	 */
	@Test
	public void getDashboardUsingUserName() {
		Dashboard d1 = getDbDashboardsByUser(defaultUserName).iterator().next();		
		Dashboard d2 = sd.getDashboard(d1.getId());
		compareDashboards(d1, d2);
	}
	
	
}
