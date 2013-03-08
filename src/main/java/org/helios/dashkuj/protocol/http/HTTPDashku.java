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
package org.helios.dashkuj.protocol.http;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.helios.dashkuj.api.AbstractDashku;
import org.helios.dashkuj.domain.Dashboard;
import org.helios.dashkuj.transport.TCPConnector;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpVersion;

/**
 * <p>Title: HTTPDashku</p>
 * <p>Description: Dashku API implementation over HTTP/JSON</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.dashkuj.protocol.http.HTTPDashku</code></p>
 */

public class HTTPDashku extends AbstractDashku implements ChannelDownstreamHandler {
	/** The netty channel connecting to the dashku server */
	protected final Channel channel;
	/**
	 * Creates a new HTTPDashku
	 * @param host The dashku server name or ip address
	 * @param port The dashku server listening port
	 * @param apiKey The dashku API key
	 */
	public HTTPDashku(String apiKey, String host, int port) {
		super(apiKey);
		channel = TCPConnector.getInstance().getSynchChannel(host, port);
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.helios.dashkuj.api.AbstractDashku#getDashboards()
	 */
	@Override
	public Collection<Dashboard> getDashboards() {
		Set<Dashboard> dashes = new HashSet<Dashboard>();
		DefaultHttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, String.format(URI_DASHBOARDS, apiKey));
		channel.write(httpRequest);
		return dashes;
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
			if(message instanceof HttpResponse) {
				HttpResponse response = (HttpResponse)message;
				int contentLength = (int)HttpHeaders.getContentLength(response);
				
				response.getContent()
				return;
			}
		}
		ctx.sendDownstream(e);
		
	}

}
