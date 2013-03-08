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
package org.helios.dashkuj.transport;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.jboss.netty.logging.InternalLogLevel;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Title: TCPConnector</p>
 * <p>Description: Netty TCP Connector</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashku.transport.TCPConnector</code></p>
 */

public class TCPConnector implements ChannelPipelineFactory {
	/** Singleton instance */
	private static volatile TCPConnector instance = null;
	/** Singleton instance ctor lock */
	private static final Object lock = new Object();
	
	protected static final LoggingHandler loggingHandler = new LoggingHandler("TCPConnector", InternalLogLevel.WARN, false);
	
	static {
		 InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
	}
	
	
	/**
	 * Acquires the TCPConnector singleton instance
	 * @return the TCPConnector singleton instance
	 */
	public static TCPConnector getInstance() {
		if(instance==null) {
			synchronized(lock) {
				if(instance==null) {
					instance = new TCPConnector();
				}
			}
		}
		return instance;
	}
	
	/**
	 * Command line entry point
	 * @param args tbd
	 */
	public static void main(String[] args) {
		TCPConnector connector = TCPConnector.getInstance();
		Channel ch = connector.getSynchChannel("localhost", 80);
		connector.log.info("Channel:" + ch);

	}
	
	
	/** Instance logger */
	protected final Logger log = LoggerFactory.getLogger(getClass());
	/** The channel factory to acquire channels from */
	protected final ChannelFactory factory =
            new NioClientSocketChannelFactory(
                    Executors.newCachedThreadPool(new ThreadFactory(){
                    	private final AtomicInteger serial = new AtomicInteger();
                    	@Override
                    	public Thread newThread(Runnable r) {
                    		Thread t = new Thread(r, "DashkuJBossThread#" + serial.incrementAndGet());
                    		t.setDaemon(true);
                    		return t;
                    	}
                    }),
                    Executors.newCachedThreadPool(new ThreadFactory(){
                    	private final AtomicInteger serial = new AtomicInteger();
                    	@Override
                    	public Thread newThread(Runnable r) {
                    		Thread t = new Thread(r, "DashkuJWorkerThread#" + serial.incrementAndGet());
                    		t.setDaemon(true);
                    		return t;
                    	}                    	
                    }));
	/** The client bootstrap */
	protected final ClientBootstrap bootstrap = new ClientBootstrap(factory);
	/** The client channel group */
	protected final ChannelGroup channelGroup = new DefaultChannelGroup();
	
	/**
	 * Creates a new TCPConnector
	 */
	private TCPConnector() {
        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("keepAlive", true);
        bootstrap.setPipelineFactory(this);
		log.info("Created TCPConnector");
	}

	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.ChannelPipelineFactory#getPipeline()
	 */
	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = Channels.pipeline();
		pipeline.addLast("logging", loggingHandler);
		return pipeline;
	}
	
	/**
	 * Acquires a channel-future for a channel that will be connected to the dashku server at the passed host name and port
	 * @param host the dashku host or ip address
	 * @param port the dashku listening port
	 * @return a channel future that will resolve into a channel when connected
	 */
	public ChannelFuture getChannel(String host, int port) {
		ChannelFuture cf = bootstrap.connect(new InetSocketAddress(host, port));
		cf.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if(future.isSuccess()) {
					channelGroup.add(future.getChannel());
				}
			}
		});
		return cf;
	}
	
	/**
	 * Synchronously acquires a connected chanel connected to the dashku server at the passed host name and port
	 * @param host the dashku host or ip address
	 * @param port the dashku listening port
	 * @return A connected channel
	 */
	public Channel getSynchChannel(String host, int port) {
		ChannelFuture cf = getChannel(host, port);
		Channel channel = cf.awaitUninterruptibly().getChannel();
		if(!cf.isDone()) {
			log.error("Failed to connect to [" + host + ":" + port + "]", cf.getCause());			
			throw new RuntimeException("Failed to connect to [" + host + ":" + port + "]", cf.getCause());
		} 
		return channel;
	}
}
