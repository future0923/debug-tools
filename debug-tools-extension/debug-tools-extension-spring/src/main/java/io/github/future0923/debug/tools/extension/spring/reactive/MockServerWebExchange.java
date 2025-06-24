/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.future0923.debug.tools.extension.spring.reactive;

import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.lang.Nullable;
import org.springframework.web.server.WebSession;
import org.springframework.web.server.adapter.DefaultServerWebExchange;
import org.springframework.web.server.i18n.AcceptHeaderLocaleContextResolver;
import org.springframework.web.server.session.DefaultWebSessionManager;
import org.springframework.web.server.session.WebSessionManager;
import reactor.core.publisher.Mono;

/**
 * Extension of {@link DefaultServerWebExchange} for use in tests, along with
 * {@link MockServerHttpRequest} and {@link MockServerHttpResponse}.
 *
 * <p>See static factory methods to create an instance.
 *
 * @author Rossen Stoyanchev
 * @since 5.0
 */
public final class MockServerWebExchange extends DefaultServerWebExchange {

	private MockServerWebExchange(MockServerHttpRequest request, WebSessionManager sessionManager) {
		super(request, new MockServerHttpResponse(), sessionManager,
				ServerCodecConfigurer.create(), new AcceptHeaderLocaleContextResolver());
	}


	@Override
	public MockServerHttpResponse getResponse() {
		return (MockServerHttpResponse) super.getResponse();
	}


	/**
	 * Create a {@link MockServerWebExchange} from the given mock request.
	 * @param request the request to use.
	 * @return the exchange
	 */
	public static MockServerWebExchange from(MockServerHttpRequest request) {
		return builder(request).build();
	}

	/**
	 * Variant of {@link #from(MockServerHttpRequest)} with a mock request builder.
	 * @param requestBuilder the builder for the mock request.
	 * @return the exchange
	 */
	public static MockServerWebExchange from(MockServerHttpRequest.BaseBuilder<?> requestBuilder) {
		return builder(requestBuilder).build();
	}

	/**
	 * Create a {@link Builder} starting with the given mock request.
	 * @param request the request to use.
	 * @return the exchange builder
	 * @since 5.1
	 */
	public static Builder builder(MockServerHttpRequest request) {
		return new Builder(request);
	}

	/**
	 * Variant of {@link #builder(MockServerHttpRequest)} with a mock request builder.
	 * @param requestBuilder the builder for the mock request.
	 * @return the exchange builder
	 * @since 5.1
	 */
	public static Builder builder(MockServerHttpRequest.BaseBuilder<?> requestBuilder) {
		return new Builder(requestBuilder.build());
	}


	/**
	 * Builder for a {@link MockServerWebExchange}.
	 * @since 5.1
	 */
	public static class Builder {

		private final MockServerHttpRequest request;

		@Nullable
		private WebSessionManager sessionManager;

		public Builder(MockServerHttpRequest request) {
			this.request = request;
		}

		/**
		 * Set the session to use for the exchange.
		 * <p>This method is mutually exclusive with
		 * {@link #sessionManager(WebSessionManager)}.
		 * @param session the session to use
		 * @see MockWebSession
		 */
		public Builder session(WebSession session) {
			this.sessionManager = exchange -> Mono.just(session);
			return this;
		}

		/**
		 * Provide a {@code WebSessionManager} instance to use with the exchange.
		 * <p>This is mutually exclusive with {@link #session(WebSession)}.
		 * @param sessionManager the session manager to use
		 */
		public Builder sessionManager(WebSessionManager sessionManager) {
			this.sessionManager = sessionManager;
			return this;
		}

		/**
		 * Build the {@code MockServerWebExchange} instance.
		 */
		public MockServerWebExchange build() {
			return new MockServerWebExchange(this.request,
					this.sessionManager != null ? this.sessionManager : new DefaultWebSessionManager());
		}
	}

}
