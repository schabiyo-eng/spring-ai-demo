/*
 * Copyright 2023-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.ai.voyageai.api;

import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;

import org.springframework.ai.retry.RetryUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;

/**
 * Single-class client for the <a href="https://docs.voyageai.com/docs/embeddings">Voyage
 * AI Embeddings API</a>.
 *
 * @author Spring AI Onboarding
 * @since 2.0.1
 */
public class VoyageAiApi {

	/**
	 * Provider name used for observation instrumentation.
	 */
	public static final String PROVIDER_NAME = "voyage_ai";

	/**
	 * Default Voyage AI API base URL.
	 */
	public static final String DEFAULT_BASE_URL = "https://api.voyageai.com";

	/**
	 * Supported Voyage AI embedding models.
	 */
	public enum EmbeddingModel {

		VOYAGE_3("voyage-3"),

		VOYAGE_3_LITE("voyage-3-lite"),

		VOYAGE_CODE_3("voyage-code-3"),

		VOYAGE_CODE_2("voyage-code-2"),

		VOYAGE_2("voyage-2"),

		VOYAGE_FINANCE_2("voyage-finance-2"),

		VOYAGE_LAW_2("voyage-law-2");

		private final String value;

		EmbeddingModel(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}

	}

	private final RestClient restClient;

	public VoyageAiApi(String baseUrl, String apiKey, RestClient.Builder restClientBuilder,
			ResponseErrorHandler responseErrorHandler) {
		Consumer<HttpHeaders> defaultHeaders = headers -> {
			headers.setBearerAuth(apiKey);
			headers.setContentType(MediaType.APPLICATION_JSON);
		};

		this.restClient = restClientBuilder.clone()
			.baseUrl(baseUrl)
			.defaultHeaders(defaultHeaders)
			.defaultStatusHandler(responseErrorHandler)
			.build();
	}

	/**
	 * Creates an embedding vector representing the input text.
	 * @param embeddingRequest the embedding request.
	 * @param <T> the input type, a {@link String} or a {@link List} of strings.
	 * @return the embedding list wrapped in a {@link ResponseEntity}.
	 */
	public <T> ResponseEntity<EmbeddingList<Embedding>> embeddings(EmbeddingRequest<T> embeddingRequest) {

		Assert.notNull(embeddingRequest, "The request body can not be null.");
		Assert.notNull(embeddingRequest.input(), "The input can not be null.");
		Assert.isTrue(embeddingRequest.input() instanceof String || embeddingRequest.input() instanceof List,
				"The input must be either a String, or a List of Strings.");

		if (embeddingRequest.input() instanceof List<?> list) {
			Assert.isTrue(!CollectionUtils.isEmpty(list), "The input list can not be empty.");
		}

		return this.restClient.post()
			.uri("/v1/embeddings")
			.body(embeddingRequest)
			.retrieve()
			.toEntity(new ParameterizedTypeReference<>() {

			});
	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Usage statistics for an embeddings request.
	 *
	 * @param totalTokens total number of tokens used in the request.
	 */
	@JsonInclude(Include.NON_NULL)
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Usage(@JsonProperty("total_tokens") Integer totalTokens) {
	}

	/**
	 * Represents a single embedding vector returned by the embeddings endpoint.
	 *
	 * @param index the index of the embedding in the list.
	 * @param embedding the embedding vector.
	 * @param object the object type, always 'embedding'.
	 */
	@JsonInclude(Include.NON_NULL)
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Embedding(@JsonProperty("index") Integer index, @JsonProperty("embedding") float[] embedding,
			@JsonProperty("object") String object) {

		public Embedding(Integer index, float[] embedding) {
			this(index, embedding, "embedding");
		}
	}

	/**
	 * Request body for the embeddings endpoint.
	 *
	 * @param <T> the input type.
	 * @param input input text to embed, a string or list of strings.
	 * @param model id of the model to use.
	 * @param inputType optional hint, "query" or "document".
	 */
	@JsonInclude(Include.NON_NULL)
	public record EmbeddingRequest<T>(@JsonProperty("input") T input, @JsonProperty("model") String model,
			@JsonProperty("input_type") @Nullable String inputType) {

		public EmbeddingRequest(T input, String model) {
			this(input, model, null);
		}
	}

	/**
	 * List of embeddings returned by the embeddings endpoint.
	 *
	 * @param <T> the data entity type.
	 * @param object must be "list".
	 * @param data the list of embeddings.
	 * @param model the model used.
	 * @param usage usage statistics.
	 */
	@JsonInclude(Include.NON_NULL)
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record EmbeddingList<T>(@JsonProperty("object") String object, @JsonProperty("data") List<T> data,
			@JsonProperty("model") String model, @JsonProperty("usage") Usage usage) {
	}

	public static final class Builder {

		private String baseUrl = DEFAULT_BASE_URL;

		private @Nullable String apiKey;

		private RestClient.Builder restClientBuilder = RestClient.builder();

		private ResponseErrorHandler responseErrorHandler = RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER;

		public Builder baseUrl(String baseUrl) {
			Assert.hasText(baseUrl, "baseUrl cannot be null or empty");
			this.baseUrl = baseUrl;
			return this;
		}

		public Builder apiKey(String apiKey) {
			Assert.hasText(apiKey, "apiKey cannot be null or empty");
			this.apiKey = apiKey;
			return this;
		}

		public Builder restClientBuilder(RestClient.Builder restClientBuilder) {
			Assert.notNull(restClientBuilder, "restClientBuilder cannot be null");
			this.restClientBuilder = restClientBuilder;
			return this;
		}

		public Builder responseErrorHandler(ResponseErrorHandler responseErrorHandler) {
			Assert.notNull(responseErrorHandler, "responseErrorHandler cannot be null");
			this.responseErrorHandler = responseErrorHandler;
			return this;
		}

		public VoyageAiApi build() {
			Assert.state(this.apiKey != null, "The API key must not be null");
			return new VoyageAiApi(this.baseUrl, this.apiKey, this.restClientBuilder, this.responseErrorHandler);
		}

	}

}
