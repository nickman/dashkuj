/**
 * Helios, OpenSource Monitoring
 * Brought to you by the Helios Development Group
 *
 * Copyright 2013, Helios Development Group and individual contributors
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
package org.jboss.netty.util.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;

import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

/**
 * <p>Title: QueueFactory</p>
 * <p>Description: A factory used to create the "optimal" {@link BlockingQueue} instance for the running JVM.</p>
 * <p>Copied from Netty 3.5.2.</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.jboss.netty.util.internal.QueueFactory</code></p>
 */

public class QueueFactory {
    private static final boolean useUnsafe = DetectionUtil.hasUnsafe();
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(QueueFactory.class);
    
    /** The class name for the Java 7 LinkedTransferQueue */
    public static final String LTQ_CLASS_NAME = "java.util.concurrent.LinkedTransferQueue";
    
    /** The parameter-less constructor for the Java 7 LinkedTransferQueue */
    private static final Constructor<? extends BlockingQueue<?>> LTQ_CTOR;
    
    static {
    	if (DetectionUtil.javaVersion() >= 7)  {
    		try {
    			Class<? extends BlockingQueue<?>> clazz = (Class<? extends BlockingQueue<?>>) Class.forName(LTQ_CLASS_NAME);
    			LTQ_CTOR = clazz.getDeclaredConstructor();
    		} catch (Exception ex) {
    			throw new RuntimeException("Failed to load [" + LTQ_CLASS_NAME + "] ctor", ex);
    		}
    	} else {
    		LTQ_CTOR = null;
    	}
    }

    private QueueFactory() {
        // only use static methods!
    }


    /**
     * Create a new unbound {@link BlockingQueue}
     *
     * @param itemClass  the {@link Class} type which will be used as {@link BlockingQueue} items
     * @return queue     the {@link BlockingQueue} implementation
     */
    public static <T> BlockingQueue<T> createQueue(Class<T> itemClass) {
        // if we run in java >=7 its the best to just use the LinkedTransferQueue which
        // comes with java bundled. See #273
        if (DetectionUtil.javaVersion() >= 7)  {
            try {
				return (BlockingQueue<T>) LTQ_CTOR.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Failed to create Java 7 LinkedTransferQueue from Ctor", e);
			}
        }

        try {
            if (useUnsafe) {
                return new LinkedTransferQueue<T>();
            }
        } catch (Throwable t) {
            // For whatever reason an exception was thrown while loading the LinkedTransferQueue
            //
            // This mostly happens because of a custom classloader or security policy that did not
            // allow us to access the com.sun.Unmisc class. So just log it and fallback to the old
            // LegacyLinkedTransferQueue that works in all cases
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Unable to instance LinkedTransferQueue, fallback to LegacyLinkedTransferQueue", t);
            }
        }

        return new LegacyLinkedTransferQueue<T>();

    }

    /**
     * Create a new unbound {@link BlockingQueue}
     *
     * @param collection  the collection which should get copied to the newly created {@link BlockingQueue}
     * @param itemClass   the {@link Class} type which will be used as {@link BlockingQueue} items
     * @return queue      the {@link BlockingQueue} implementation
     */
    public static <T> BlockingQueue<T> createQueue(Collection<? extends T> collection, Class<T> itemClass) {
        // if we run in java >=7 its the best to just use the LinkedTransferQueue which
        // comes with java bundled. See #273
        if (DetectionUtil.javaVersion() >= 7)  {
            try {
				return (BlockingQueue<T>) LTQ_CTOR.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Failed to create Java 7 LinkedTransferQueue from Ctor", e);
			}
        }

        try {
            if (useUnsafe) {
                return new LinkedTransferQueue<T>(collection);
            }
        } catch (Throwable t) {
            // For whatever reason an exception was thrown while loading the LinkedTransferQueue
            //
            // This mostly happens because of a custom classloader or security policy that did not
            // allow us to access the com.sun.Unmisc class. So just log it and fallback to the old
            // LegacyLinkedTransferQueue that works in all cases
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Unable to instance LinkedTransferQueue, fallback to LegacyLinkedTransferQueue", t);
            }
        }

        return new LegacyLinkedTransferQueue<T>(collection);

    }

}
