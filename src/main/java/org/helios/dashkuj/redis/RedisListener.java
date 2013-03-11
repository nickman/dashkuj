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

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * <p>Title: RedisListener</p>
 * <p>Description: Listens on redis events</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.redis.RedisListener</code></p>
 */

public class RedisListener extends JedisPubSub implements ThreadFactory, Runnable {
	/** A map of listeners keyed by host:port */
	private static final Map<String, RedisListener> listeners = new ConcurrentHashMap<String, RedisListener>();
	/** Listener thread serial number factory */
	private static final AtomicLong threadSerial = new AtomicLong(0L);
	/** Listener thread thread group */
	private static final ThreadGroup threadGroup = new ThreadGroup("RedisListenerThreadGroup");
	
	/** Instance logger */
	protected final Logger log;
	
	/** The jedis instance */
	private final Jedis jedis;
	/** The jedis instance redis server */
	private final String host;
	/** The jedis instance redis port */
	private final int port;
	/** Indicates if the jedis listener is started */
	private final AtomicBoolean listenerStarted = new AtomicBoolean(false);
	/** Indicates if the jedis listener is activated */
	private final AtomicBoolean listenerActivated = new AtomicBoolean(false);
	/** The activation latch for the psubscriber thread, triggered on the first psubscribe */
	private final AtomicReference<CountDownLatch> listenerLatch = new AtomicReference<CountDownLatch>(null);
	/** A reference to the first psubscribe argument which must be executed by the psub thread */
	private final AtomicReference<String[]> initialPsubArg = new AtomicReference<String[]>(null);
	
	/** The patterns that have been psubscribed to */
	private final Set<String> psubscriptions = new CopyOnWriteArraySet<String>();
	
	/** The psub thread for this listener instance */
	private volatile Thread psubThread = null;
	
	
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
		jedis = new Jedis(this.host, this.port);
	}
	
	/**
	 * Starts the listener
	 */
	public void start() {
		if(listenerStarted.get()) {
			log.debug("RedisListener already started");
		}
		psubThread = newThread(this);
		psubThread.start();
		listenerActivated.set(false);
		listenerLatch.set(new CountDownLatch(1));
		listenerStarted.set(true);
	}
	
	/**
	 * Stops the listener
	 */
	public void stop() {
		if(!listenerStarted.get()) {
			log.debug("RedisListener not started");
		}
		this.punsubscribe("*");
		listenerStarted.set(false);
		psubThread.interrupt();
	}
	
	/**
	 * {@inheritDoc}
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		log.info("RedisListener psub thread starting...");
		try {
			log.info("RedisListener psub thread activated");
			jedis.psubscribe(this);
			log.info("RedisListener psub thread exiting...");
		} catch (Exception e) {
			if(listenerStarted.get()) {
				log.error("psub thread terminated", e);
			} else {
				log.info("RedisListener psub thread ended normally");
			}
		}
		log.info("RedisListener psub thread exited");
	}
	
	/**
	 * {@inheritDoc}
	 * @see redis.clients.jedis.JedisPubSub#psubscribe(java.lang.String[])
	 */
	@Override
	public void psubscribe(String... patterns) {
		if(patterns!=null) {
			Collections.addAll(psubscriptions, patterns);
			super.psubscribe(patterns);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @see redis.clients.jedis.JedisPubSub#punsubscribe(java.lang.String[])
	 */
	@Override
	public void punsubscribe(String... patterns) {
		if(patterns!=null) {
			for(String pattern: patterns) {
				psubscriptions.remove(pattern);
				super.punsubscribe(pattern);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * @see redis.clients.jedis.JedisPubSub#onMessage(java.lang.String, java.lang.String)
	 */
	@Override
	public void onMessage(String channel, String message) {
		log.info("Redis Message Received on channel [{}]:[{}]", channel, message);		
	}

	/**
	 * {@inheritDoc}
	 * @see redis.clients.jedis.JedisPubSub#onPMessage(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void onPMessage(String pattern, String channel, String message) {
		
//		JsonParser parser = new JsonParser();
//		JsonElement element = parser.parse(message);
		String element = null;
		try {	
			element = new org.json.JSONObject(message).toString(2); 
		} catch (Exception ex) {
			element = message;
		}
		log.info("Redis PMessage Received on channel [{}] for pattern [{}]:[{}]", channel, pattern, element);		
	}

	/**
	 * {@inheritDoc}
	 * @see redis.clients.jedis.JedisPubSub#onSubscribe(java.lang.String, int)
	 */
	@Override
	public void onSubscribe(String channel, int subscribedChannels) {
		log.info("Redis Listener Subscribed to channel [{}]:[{}]", channel, subscribedChannels);		
	}

	/**
	 * {@inheritDoc}
	 * @see redis.clients.jedis.JedisPubSub#onUnsubscribe(java.lang.String, int)
	 */
	@Override
	public void onUnsubscribe(String channel, int subscribedChannels) {
		log.info("Redis Listener Unsubscribed from channel [{}]:[{}]", channel, subscribedChannels);		
	}

	/**
	 * {@inheritDoc}
	 * @see redis.clients.jedis.JedisPubSub#onPUnsubscribe(java.lang.String, int)
	 */
	@Override
	public void onPUnsubscribe(String pattern, int subscribedChannels) {
		log.info("Redis Listener Punsubscribed for pattern [{}]:[{}]", pattern, subscribedChannels);		
	}

	/**
	 * {@inheritDoc}
	 * @see redis.clients.jedis.JedisPubSub#onPSubscribe(java.lang.String, int)
	 */
	@Override
	public void onPSubscribe(String pattern, int subscribedChannels) {
		log.info("Redis Listener Psubscribed for pattern [{}]:[{}]", pattern, subscribedChannels);
		
	}

	/**
	 * {@inheritDoc}
	 * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
	 */
	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(threadGroup, r, "RedisListenerThread#" + threadSerial.incrementAndGet());
		t.setDaemon(true);
		return t;
	}

}



