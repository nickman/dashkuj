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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.helios.dashkuj.redis.RedisListener;
import org.helios.dashkuj.redis.RedisPubSub;

/**
 * <p>Title: DomainRepository</p>
 * <p>Description: A repository of dashboards and widgets, loaded on first call, kept in synch by the redis listener and optionally persisted.</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.domain.DomainRepository</code></p>
 */

public class DomainRepository implements RedisListener {
	/** A map of repositories keyed by repository key which is the <b><code>host:port</code></p> of the dashku server for which domain objects are stored. */
	private static final Map<String, DomainRepository> repos = new ConcurrentHashMap<String, DomainRepository>();
	
	/** A map of dashboards keyed by the dashboard id */
	private final Map<String, Dashboard> dashboardsById = new ConcurrentHashMap<String, Dashboard>();
	/** A map of widgets keyed by the widget id */
	private final Map<String, Widget> widgetsById = new ConcurrentHashMap<String, Widget>();
	
	/** The dashku instance server */
	private final String host;
	/** The dashku instance port */
	private final int port;
	
	
	/**
	 * Acquires the domain repository for the passed host/port
	 * @param host The dashku host or ip address
	 * @param port The dashku port
	 * @return the domain repository
	 */
	public static DomainRepository getInstance(String host, int port) {
		String key = host + ":" + port;
		DomainRepository repo = repos.get(key);
		if(repo==null) {
			synchronized(repos) {
				repo = repos.get(key);
				if(repo==null) {
					repo = new DomainRepository(host, port);
					repos.put(key, repo);
				}
			}
		}
		return repo;
	}
	
	/**
	 * Creates a new DomainRepository
	 */
	private DomainRepository(String host, int port) {
		this.host = host;
		this.port = port;
		RedisPubSub.getInstance(host, port).registerListener(this);
	}
	
	/**
	 * Returns the dashboard with the passed id, or null if one is not found.
	 * @param dashboardId The id of the dashboard to retrieve
	 * @return the located dashboard or null if one is not found.
	 */
	public Dashboard getDashboardOrNull(String dashboardId) {
		if(dashboardId==null || dashboardId.trim().isEmpty()) return null;
		return dashboardsById.get(dashboardId);
	}
	
	/**
	 * Returns the dashboard with the passed id
	 * @param dashboardId The id of the dashboard to retrieve
	 * @return the located dashboard
	 */
	public Dashboard getDashboard(String dashboardId) {
		if(dashboardId==null || dashboardId.trim().isEmpty()) throw new IllegalArgumentException("The passed dashboardId was null or empty", new Throwable());
		Dashboard db = dashboardsById.get(dashboardId);
		if(db==null) throw new IllegalArgumentException("The passed dashboardId [" + dashboardId + "] could not be found", new Throwable());
		return db;
	}

	/**
	 * {@inheritDoc}
	 * @see redis.clients.nedis.netty.SubListener#onChannelMessage(java.lang.String, java.lang.String)
	 */
	@Override
	public void onChannelMessage(String channel, String message) {		
		
	}

	/**
	 * <p>Callback from the {@link RedisPubSub} for this dashku domain when a change in a domain object occurs.</p> 
	 * {@inheritDoc}
	 * @see redis.clients.nedis.netty.SubListener#onPatternMessage(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void onPatternMessage(String pattern, String channel, String message) {		
		
	}

	/**
	 * Returns the host of the domain dashku server
	 * @return the host of the domain dashku server
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Returns the port of the domain dashku server
	 * @return the port of the domain dashku server
	 */
	public int getPort() {
		return port;
	}

}
