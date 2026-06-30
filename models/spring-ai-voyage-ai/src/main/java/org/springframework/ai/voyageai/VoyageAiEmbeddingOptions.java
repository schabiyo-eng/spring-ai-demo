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

package org.springframework.ai.voyageai;

import org.jspecify.annotations.Nullable;

import org.springframework.ai.embedding.EmbeddingOptions;
import org.springframework.ai.voyageai.api.VoyageAiApi;

/**
 * Options for the Voyage AI Embedding API.
 *
 * @author Spring AI Onboarding
 * @since 2.0.1
 */
public class VoyageAiEmbeddingOptions implements EmbeddingOptions {

	public static final String DEFAULT_EMBEDDING_MODEL = VoyageAiApi.EmbeddingModel.VOYAGE_3.getValue();

	/**
	 * ID of the model to use.
	 */
	private final String model;

	/**
	 * Optional input type hint, "query" or "document".
	 */
	private final @Nullable String inputType;

	protected VoyageAiEmbeddingOptions(@Nullable String model, @Nullable String inputType) {
		this.model = (model != null ? model : DEFAULT_EMBEDDING_MODEL);
		this.inputType = inputType;
	}

	@Override
	public String getModel() {
		return this.model;
	}

	public @Nullable String getInputType() {
		return this.inputType;
	}

	@Override
	public @Nullable Integer getDimensions() {
		return null;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {

		private @Nullable String model;

		private @Nullable String inputType;

		public Builder() {
		}

		public Builder model(@Nullable String model) {
			this.model = model;
			return this;
		}

		public Builder inputType(@Nullable String inputType) {
			this.inputType = inputType;
			return this;
		}

		public VoyageAiEmbeddingOptions build() {
			return new VoyageAiEmbeddingOptions(this.model, this.inputType);
		}

	}

}
