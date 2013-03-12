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

import org.helios.dashkuj.json.GsonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.nedis.netty.ConnectionListener;
import redis.clients.nedis.netty.OptimizedPubSub;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * <p>Title: RedisPubSub</p>
 * <p>Description: Listens on redis events</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.redis.RedisPubSub</code></p>
 */

public class RedisPubSub implements ConnectionListener, RedisListener {
	/** A map of listeners keyed by host:port */
	private static final Map<String, RedisPubSub> listeners = new ConcurrentHashMap<String, RedisPubSub>();
	
	/** Instance logger */
	protected final Logger log;
	/** Pub/Sub interface */
	protected final OptimizedPubSub pubsub;
	
	/** The dashku instance redis server */
	private final String host;
	/** The dashku instance redis port */
	private final int port;
	/** Indicates if the redis listener is started */
	private final AtomicBoolean listenerStarted = new AtomicBoolean(false);
	
	
	
	
	/**
	 * Acquires the redis listener for the passed host/port
	 * @param host The redis host
	 * @param port The redis port
	 * @return the listener
	 */
	public static RedisPubSub getInstance(String host, int port) {
		String key = host + ":" + port;
		RedisPubSub listener = listeners.get(key);
		if(listener==null) {
			synchronized(listeners) {
				listener = listeners.get(key);
				if(listener==null) {
					listener = new RedisPubSub(host, port);
					listeners.put(key, listener);
				}
			}
		}
		return listener;
	}
	
	/**
	 * Creates a new RedisPubSub
	 * @param host The redis host
	 * @param port The redis port
	 */
	private RedisPubSub(String host, int port) {
		this.host = host;
		this.port = port;
		pubsub = OptimizedPubSub.getInstance(this.host, this.port);
		pubsub.registerListener(this);
		pubsub.registerConnectionListener(this);
		log = LoggerFactory.getLogger(getClass().getName() + host + ":" + port);
	}
	
	/**
	 * Starts the listener
	 * @return this RedisPubSub 
	 */
	public RedisPubSub start() {
		if(listenerStarted.get()) {
			log.debug("RedisPubSub already started");
		} else {
			listenerStarted.set(true);
			log.info("RedisPubSub [{}:{}] Started", pubsub.getHost(), pubsub.getPort());
		}
		return this;
	}
	
	/**
	 * Stops the listener
	 */
	public void stop() {
		if(!listenerStarted.get()) {
			log.debug("RedisPubSub not started");
		} else {
			pubsub.close();
			listenerStarted.set(false);
		}
	}
	
	/**
	 * Registers a redis publication listener
	 * @param listener a redis publication listener
	 */
	public void registerListener(RedisListener listener) {
		pubsub.registerListener(listener);
	}
	
	/**
	 * Unregisters a redis publication listener
	 * @param listener a redis publication listener
	 */
	public void unregisterListener(RedisListener listener) {
		pubsub.unregisterListener(listener);
	}

	/**
	 * Subscribes to the passed redis channels
	 * @param channels the redis channels to subscribe to
	 */
	public void subscribe(String... channels) {
		pubsub.subscribe(channels);
	}

	/**
	 * Unsubscribes from the passed redis channels
	 * @param channels the redis channels to unsubscribe from
	 */
	public void unsubscribe(String... channels) {
		pubsub.unsubscribe(channels);
	}

	/**
	 * Subscribes to the passed patterns
	 * @param patterns the patterns to subscribe to
	 */
	public void psubscribe(String... patterns) {
		pubsub.psubscribe(patterns);
	}

	/**
	 * Unsubscribes from the passed patterns
	 * @param patterns the patterns to unsubscribe from
	 */
	public void punsubscribe(String... patterns) {
		pubsub.punsubscribe(patterns);
	}

	/**
	 * Publishes the passed messages to the specified channel
	 * @param channel The channel to publish to 
	 * @param messages The messages to publish
	 */
	public void publish(String channel, String... messages) {
		pubsub.publish(channel, messages);
	}

	/**
	 * {@inheritDoc}
	 * @see redis.clients.nedis.netty.SubListener#onChannelMessage(java.lang.String, java.lang.String)
	 */
	@Override
	public void onChannelMessage(String channel, String message) {
		if(!log.isDebugEnabled()) return;
		try {
			JsonElement el = new JsonParser().parse(message);
			String formattedMessage = GsonFactory.getInstance().printer().toJson(el);
			log.debug("Received message on channel [{}]-->[{}]", channel, formattedMessage);
		} catch (Exception ex) {
			log.debug("Received message on channel [{}]-->[{}]", channel, message);
		}
	}

	/**
	 * {@inheritDoc}
	 * @see redis.clients.nedis.netty.SubListener#onPatternMessage(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void onPatternMessage(String pattern, String channel, String message) {
		if(!log.isDebugEnabled()) return;
		try {
			JsonElement el = new JsonParser().parse(message);
			String formattedMessage = GsonFactory.getInstance().printer().toJson(el);
			log.debug("Received pattern message for pattern [{}] on channel [{}]-->[{}]", pattern, channel, formattedMessage);
		} catch (Exception ex) {
			log.debug("Received pattern message for pattern [{}] on channel [{}]-->[{}]", pattern, channel, message);
		}
		
		
	}

	/**
	 * {@inheritDoc}
	 * @see redis.clients.nedis.netty.ConnectionListener#onConnect(redis.clients.nedis.netty.OptimizedPubSub)
	 */
	@Override
	public void onConnect(OptimizedPubSub pubSub) {
		log.info("RedisListener connected to [{}:{}]", pubSub.getHost(), pubSub.getPort());		
	}

	/**
	 * {@inheritDoc}
	 * @see redis.clients.nedis.netty.ConnectionListener#onDisconnect(redis.clients.nedis.netty.OptimizedPubSub, java.lang.Throwable)
	 */
	@Override
	public void onDisconnect(OptimizedPubSub pubSub, Throwable cause) {
		String key = pubSub.getHost() + ":" + pubSub.getPort();
		listeners.remove(key);
		if(cause==null) {
			log.info("RedisListener disconnected from [{}]", key);
		} else {
			log.error("RedisListener disconnected badly from [{}]", key, cause);
		}		
	}



}



