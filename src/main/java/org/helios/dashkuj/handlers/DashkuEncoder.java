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
package org.helios.dashkuj.handlers;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.helios.dashkuj.json.GsonFactory;
import org.helios.dashkuj.protocol.http.DashkuHttpRequest;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

/**
 * <p>Title: DashkuEncoder</p>
 * <p>Description: A encoder for Dashku server targetted domain objects</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.handlers.DashkuEncoder</code></p>
 * @param <T> The type that this encoder encodes
 */
@ChannelHandler.Sharable
public class DashkuEncoder<T> extends OneToOneEncoder {
	/** The type this instance will encode */
	protected final TypeToken<T> type;
	/** The underlying dashku domain type  */
	protected final Class<T> domainType;
	
	/** Instance logger */
	protected final Logger log = LoggerFactory.getLogger(getClass());

	
	/**
	 * Creates a new DashkuEncoder
	 * @param type The type this instance will encode
	 */
	@SuppressWarnings("unchecked")
	public DashkuEncoder(TypeToken<T> type) {
		this.type = type;
		Type t = this.type.getType();
		if(t instanceof ParameterizedType) {
			domainType = (Class<T>)((ParameterizedType)t).getActualTypeArguments()[0];
		} else {
			domainType = (Class<T>)t;
		}
	}

	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.handler.codec.oneone.OneToOneEncoder#encode(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.Channel, java.lang.Object)
	 */
	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel, Object message) throws Exception {
		Gson gson = GsonFactory.getInstance().newGson();
		if(type.getType().equals(message)) {			
			return ChannelBuffers.wrappedBuffer(gson.toJson(message, type.getType()).getBytes());
		} else if(message instanceof DashkuHttpRequest) {
			DashkuHttpRequest dashkuRequest = (DashkuHttpRequest)message; 
			Object payload = dashkuRequest.getPayload();
			if(payload==null) return dashkuRequest.getHttpRequest();
			if(domainType.isInstance(payload)) {
				dashkuRequest.setChannelBuffer(ChannelBuffers.wrappedBuffer(gson.toJson(message, domainType).getBytes()));
				return dashkuRequest.getHttpRequest();
			} else if(payload instanceof JsonObject) {
				dashkuRequest.setChannelBuffer(ChannelBuffers.wrappedBuffer(payload.toString().getBytes()));
				return dashkuRequest.getHttpRequest();
			} else if(payload instanceof CharSequence) {
				dashkuRequest.setChannelBuffer(ChannelBuffers.wrappedBuffer(payload.toString().getBytes()));
				return dashkuRequest.getHttpRequest();				
			}
		} else if(message instanceof JsonObject) {			
			log.info("Sending Domain Object Update [\n{}\n]", message);
			return ChannelBuffers.wrappedBuffer(gson.toJson((JsonElement)message).getBytes());
		}
		return message;
	}
	
	

}
