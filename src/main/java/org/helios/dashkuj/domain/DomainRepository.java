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

import java.util.ArrayList;
import java.util.Collection;
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
	/** The dashku redis listener */
	private final RedisPubSub redisPubSub;
	
	
	/**
	 * Indicates if the repository is being live updated by the pub/sub listener
	 * @return true if the repository is being live updated by the pub/sub listener, false otherwise
	 */
	public boolean isLive() {
		return redisPubSub.isStarted();
	}

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
		redisPubSub = RedisPubSub.getInstance(host, port); 
		redisPubSub.registerListener(this);
	}
	
	/**
	 * Synchronizes the repository with the passed dashboards
	 * @param dashboards the dashboards to synch into the repository
	 * @return the same dashboards for fluent style updates
	 */
	public Collection<Dashboard> synch(Collection<Dashboard> dashboards) {
		if(!isLive()) {
			for(Dashboard d: dashboards) {
				synch(d);
			}
		}
		return dashboards;
	}
	
	/**
	 * Synchronizes the repository dashboard with the passed dashboard
	 * @param dashboard the dashboard to synch into the repository\
	 * @return the same dashboard for fluent style updates
	 */
	public Dashboard synch(Dashboard dashboard) {
		if(!isLive()) {
			Dashboard d = dashboardsById.get(dashboard.getId());
			if(d==null) {
				synchronized(dashboardsById) {
					d = dashboardsById.get(dashboard.getId());
					if(d==null) {
						d = dashboard;
						dashboardsById.put(d.getId(), d);
						for(Widget w: d.widgets) {
							synch(w);
						}
						return dashboard;
					}
				}
			}
			d.updateFrom(dashboard);
		}
		return dashboard;
	}
	
	/**
	 * Deletes the identified dashboard from the repository
	 * @param dashboardId the id of the dashboard to delete
	 * @return the id of the deleted dashboard or null if it was not found 
	 */
	public String deleteDashboard(String dashboardId) {
		if(dashboardId!=null) {
			Dashboard d = dashboardsById.remove(dashboardId);
			if(d!=null) {
				for(Widget w: d.widgets) {
					deleteWidget(w.getId());
				}
				return d.getId();
			}
			return null;
		}
		return null;
		
	}
	
	/**
	 * Deletes the identified widget from the repository
	 * @param widgetId the id of the widget to delete
	 * @return the id of the deleted widget or null if it was not found 
	 */
	public String deleteWidget(String widgetId) {
		if(widgetId!=null) {
			Widget w = widgetsById.remove(widgetId);
			return w==null ? null : w.getId();
			
		}
		return null;
	}
	
	
	/**
	 * Synchronizes the repository widget with the passed widget
	 * @param widget the widget to synch into the repository
	 * @return the same widget for fluent style updates
	 */
	public Widget synch(Widget widget) {
		if(!isLive()) {
			Widget w = widgetsById.get(widget.getId());
			if(w==null) {
				synchronized(widgetsById) {
					w = widgetsById.get(widget.getId());
					if(w==null) {
						w = widget;
						widgetsById.put(w.getId(), w);
						return widget;
					}
				}
			}
			w.updateFrom(widget);
		}
		return widget;
		
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
	 * Returns all the dashboards in this repository.
	 * @return all the dashboards in this repository.
	 */
	public Collection<Dashboard> getDashboards() {
		return new ArrayList<Dashboard>(dashboardsById.values());
	}
	
	/**
	 * Flushes the repo
	 */
	public void flush() {
		dashboardsById.clear();
		widgetsById.clear();
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
