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
package org.helios.dashkuj.redis;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Title: RedisListener</p>
 * <p>Description: Listens on redis events</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.redis.RedisListener</code></p>
 */

public class RedisListener  {
	/** A map of listeners keyed by host:port */
	private static final Map<String, RedisListener> listeners = new ConcurrentHashMap<String, RedisListener>();
	
	/** Instance logger */
	protected final Logger log;
	
	/** The jedis instance redis server */
	private final String host;
	/** The jedis instance redis port */
	private final int port;
	/** Indicates if the jedis listener is started */
	private final AtomicBoolean listenerStarted = new AtomicBoolean(false);
	/** Indicates if the jedis listener is activated */
	private final AtomicBoolean listenerActivated = new AtomicBoolean(false);
	
	
	
	
	/**
	 * Acquires the redis listener for the passed host/port
	 * @param host The redis host
	 * @param port The redis port
	 * @return the listener
	 */
	public static RedisListener getInstance(String host, int port) {
		String key = host + ":" + port;
		RedisListener listener = listeners.get(key);
		if(listener==null) {
			synchronized(listeners) {
				listener = listeners.get(key);
				if(listener==null) {
					listener = new RedisListener(host, port);
					listeners.put(key, listener);
				}
			}
		}
		return listener;
	}
	
	/**
	 * Creates a new RedisListener
	 * @param host The redis host
	 * @param port The redis port
	 */
	private RedisListener(String host, int port) {
		this.host = host;
		this.port = port;
		log = LoggerFactory.getLogger(getClass().getName() + host + ":" + port);
	}
	
	/**
	 * Starts the listener
	 */
	public void start() {
		if(listenerStarted.get()) {
			log.debug("RedisListener already started");
		}
		listenerStarted.set(true);
	}
	
	/**
	 * Stops the listener
	 */
	public void stop() {
		if(!listenerStarted.get()) {
			log.debug("RedisListener not started");
		}
		listenerStarted.set(false);
	}
	



}



