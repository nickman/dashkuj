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
package org.helios.dashkuj.handlers;

import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.helios.dashkuj.api.DashkuAPIException;
import org.helios.dashkuj.domain.Status;
import org.helios.dashkuj.json.GsonFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.buffer.DirectChannelBufferFactory;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelLocal;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.UpstreamMessageEvent;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * <p>Title: DashkuDecoder</p>
 * <p>Description: A decoder for Dashku server returned domain objects</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.handlers.DashkuDecoder</code></p>
 * @param <T> The type that this decoder decodes
 */
@ChannelHandler.Sharable
public class DashkuDecoder<T> extends SimpleChannelUpstreamHandler {
	/** The type this instance will decode */
	protected final TypeToken<T> type;
	/** The underlying dashku domain type  */
	protected final Class<T> domainType;
	
	/** Instance logger */
	protected final Logger log = LoggerFactory.getLogger(getClass());

	
	/** An accumulating channel buffer for reading chunked http content */
	protected static final ChannelLocal<ChannelBuffer> decodingState = new ChannelLocal<ChannelBuffer>(true) {
		private final DirectChannelBufferFactory bufferFactory = new DirectChannelBufferFactory();
		@Override
		protected ChannelBuffer initialValue(Channel channel) {
			return ChannelBuffers.dynamicBuffer(2048, bufferFactory);
		}
	};
	/** Retains the state of chunked handling */
	protected static final ChannelLocal<Boolean> readingChunks = new ChannelLocal<Boolean>(true) {
		@Override
		protected Boolean initialValue(Channel channel) {
			return false;
		}
	};
	/** Retains the http response code while unchunking */
	protected static final ChannelLocal<HttpResponseStatus> responseStatus = new ChannelLocal<HttpResponseStatus>(true);
	
	/**
	 * Creates a new DashkuDecoder
	 * @param type The type this instance will decode
	 */
	@SuppressWarnings("unchecked")
	public DashkuDecoder(TypeToken<T> type) {
		this.type = type;
		Type t = this.type.getType();
		if(t instanceof ParameterizedType) {
			domainType = (Class<T>)((ParameterizedType)t).getActualTypeArguments()[0];
		} else {
			domainType = (Class<T>)t;
		}
	}


	/**
	 * <p>Decoding logic:<ul>
	 * 		<li>If response is <b>OK</b> then:<ul>
	 * 			<li>Input should decode to {@link #type}</li>
	 * 		</ul></li>
	 * 		<li>If response is <b>NOT OK</b> then:<ul>
	 * 			<li>Try decoding to {@link Status} and return as {@link DashkuAPIException}</li>
	 * 			<li>If fails, return generic {@link DashkuAPIException}</li>
	 * 		</ul></li> 
	 * </ul></p>
	 * {@inheritDoc}
	 * @see org.jboss.netty.handler.codec.oneone.OneToOneDecoder#decode(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.Channel, java.lang.Object)
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) throws Exception {
		Channel channel = event.getChannel();
		Object message = event.getMessage();
		if(!readingChunks.get(channel)) {
			HttpResponse response = (HttpResponse)message;
			logHeaders(response);
			if(response.isChunked()) {
				readingChunks.set(channel, true);
				responseStatus.set(channel, response.getStatus());
				log.info("Started Chunking");
			} else {
				ChannelBuffer buffer = decodingState.remove(channel);	
				readingChunks.remove(channel);
				buffer.writeBytes(response.getContent());
				ctx.sendUpstream(new UpstreamMessageEvent(channel, decodeDomainObject(buffer, response.getStatus()), event.getRemoteAddress()));
			}			
		} else {
			HttpChunk chunk = (HttpChunk)message;
			if(chunk.isLast()) {
				readingChunks.set(channel, false);
				ChannelBuffer buffer = decodingState.remove(channel);	
				readingChunks.remove(channel);
				buffer.writeBytes(chunk.getContent());				
				ctx.sendUpstream(new UpstreamMessageEvent(channel, decodeDomainObject(buffer, responseStatus.remove(channel)), event.getRemoteAddress()));				
			} else {
				decodingState.get(channel).writeBytes(chunk.getContent());	
			}
				
		}
	}
	
	/**
	 * Reads the domain object or exception from the http response content
	 * @param content The http response content
	 * @param responseStatus The http response status
	 * @return the read object
	 */
	protected Object decodeDomainObject(ChannelBuffer content, HttpResponseStatus responseStatus) {
		Object readObject = null;
		InputStreamReader jsonReader = new InputStreamReader(new ChannelBufferInputStream(content, content.readableBytes()));
		Gson gson = GsonFactory.getInstance().newGson();
		if(responseStatus.getCode()>=200 && responseStatus.getCode()<300) {
			readObject = gson.fromJson(jsonReader, type.getType());
			if(readObject instanceof Status) {
				((Status)readObject).responseCode(responseStatus);
			}										
		} else {
			try {
				readObject = gson.fromJson(jsonReader, Status.class);
				((Status)readObject).responseCode(responseStatus);
				readObject = new DashkuAPIException((Status)readObject);
			} catch (Exception ex) {
				readObject = new DashkuAPIException("UpstreamHandler for [" + type + "] failed", ex);
			}
		}
		return readObject;		
	}
	
	/**
	 * Debug logs the headers of the passed repsonse
	 * @param response The HttpResponse to print the headers for
	 */
	protected void logHeaders(HttpResponse response) {
		if (log.isDebugEnabled() && !response.getHeaderNames().isEmpty()) {
			StringBuilder b = new StringBuilder("HTTPResponse Headers ===============:");
            for (String name: response.getHeaderNames()) {
            	b.append("\n\t").append(name).append(":");
                for (String value: response.getHeaders(name)) {
                    b.append(value).append(",");
                }
                b.deleteCharAt(b.length()-1);
            }
            log.debug(b.toString());
        }						
	}

}
