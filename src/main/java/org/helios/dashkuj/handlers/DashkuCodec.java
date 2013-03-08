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

import java.io.InputStreamReader;

import org.helios.dashkuj.api.DashkuAPIException;
import org.helios.dashkuj.domain.Status;
import org.helios.dashkuj.json.GsonFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.DownstreamMessageEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.UpstreamMessageEvent;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * <p>Title: DashkuCodec</p>
 * <p>Description: Codec for marshalling and unmarshalling dashku domain objects</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.handlers.DashkuCodec</code></p>
 * @param <T> The type that this codec encodes/decodes
 */

public class DashkuCodec<T> implements ChannelDownstreamHandler, ChannelUpstreamHandler {
	/** The type this instance will code/decode */
	protected final Class<T> type;
	
	/**
	 * Creates a new DashkuCodec
	 * @param type The type this instance will code/decode
	 */
	public DashkuCodec(Class<T> type) {
		this.type = type;
	}

	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.ChannelUpstreamHandler#handleUpstream(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelEvent)
	 */
	@Override
	public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
		if(e instanceof MessageEvent) {
			MessageEvent me = (MessageEvent)e;			
			Object message = me.getMessage();
			if(message instanceof HttpResponse) {
				try {
					Object readObject = null;
					HttpResponse response = (HttpResponse)message;
					ChannelBuffer content = response.getContent();
					if(response.getStatus().equals(HttpResponseStatus.OK)) {
						int contentLength = (int)HttpHeaders.getContentLength(response);
						Gson gson = GsonFactory.getInstance().newGson();	
						TypeToken.get(this.getClass());
						InputStreamReader jsonReader = new InputStreamReader(new ChannelBufferInputStream(content, contentLength));
						readObject = gson.fromJson(jsonReader, type);
						if(readObject instanceof Status) {
							((Status)readObject).responseCode(response.getStatus());
						}										
					} else {
						int contentLength = (int)HttpHeaders.getContentLength(response);
						Gson gson = GsonFactory.getInstance().newGson();	
						InputStreamReader jsonReader = new InputStreamReader(new ChannelBufferInputStream(content, contentLength));
						readObject = gson.fromJson(jsonReader, Status.class).responseCode(response.getStatus());
					}
					ctx.sendUpstream(new UpstreamMessageEvent(e.getChannel(), readObject, ((MessageEvent) e).getRemoteAddress()));
					return;
				} catch (Exception ex) {
					ctx.sendUpstream(new UpstreamMessageEvent(e.getChannel(), new DashkuAPIException("UpstreamHandler for [" + type.getName() + "] failed", ex), ((MessageEvent) e).getRemoteAddress()));
					return;					
				}
			}
		}
		ctx.sendUpstream(e);
	}
	
	

	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.ChannelDownstreamHandler#handleDownstream(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelEvent)
	 */
	@Override
	public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
		if(e instanceof MessageEvent) {
			MessageEvent me = (MessageEvent)e;			
			Object message = me.getMessage();
			if(type.isInstance(message)) {
				Gson gson = GsonFactory.getInstance().newGson();
				byte[] bytes = gson.toJson(message, type).getBytes();
				Channel channel = e.getChannel();
				ctx.sendDownstream(new DownstreamMessageEvent(channel, Channels.future(channel), ChannelBuffers.wrappedBuffer(bytes), ((MessageEvent) e).getRemoteAddress()));
				return;
			}
		}
		ctx.sendDownstream(e);

	}

}
