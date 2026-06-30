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

package org.springframework.ai.model.voyageai.autoconfigure;

import org.jspecify.annotations.Nullable;

import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.voyageai.VoyageAiEmbeddingOptions;
import org.springframework.ai.voyageai.api.VoyageAiApi;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

/**
 * Configuration properties for Voyage AI embedding model.
 *
 * @author Spring AI Onboarding
 * @since 2.0.1
 */
@ConfigurationProperties(VoyageAiEmbeddingProperties.CONFIG_PREFIX)
public class VoyageAiEmbeddingProperties extends VoyageAiParentProperties {

	public static final String CONFIG_PREFIX = "spring.ai.voyageai.embedding";

	public MetadataMode metadataMode = MetadataMode.EMBED;

	public VoyageAiEmbeddingProperties() {
		super.setBaseUrl(VoyageAiApi.DEFAULT_BASE_URL);
	}

	public MetadataMode getMetadataMode() {
		return this.metadataMode;
	}

	public void setMetadataMode(MetadataMode metadataMode) {
		this.metadataMode = metadataMode;
	}

	private @Nullable String model;

	private @Nullable String inputType;

	public @Nullable String getModel() {
		return this.model;
	}

	public void setModel(@Nullable String model) {
		this.model = model;
	}

	public @Nullable String getInputType() {
		return this.inputType;
	}

	public void setInputType(@Nullable String inputType) {
		this.inputType = inputType;
	}

	public VoyageAiEmbeddingOptions toOptions() {
		return VoyageAiEmbeddingOptions.builder().model(this.model).inputType(this.inputType).build();
	}

	private Options options = new Options();

	@DeprecatedConfigurationProperty(replacement = "spring.ai.voyageai.embedding")
	@Deprecated(since = "2.0.0", forRemoval = true)
	public Options getOptions() {
		return this.options;
	}

	public void setOptions(Options options) {
		this.options = options;
	}

	public class Options {

		@DeprecatedConfigurationProperty(replacement = "spring.ai.voyageai.embedding.model")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getModel() {
			return VoyageAiEmbeddingProperties.this.getModel();
		}

		public void setModel(@Nullable String model) {
			VoyageAiEmbeddingProperties.this.setModel(model);
		}

		@DeprecatedConfigurationProperty(replacement = "spring.ai.voyageai.embedding.input-type")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getInputType() {
			return VoyageAiEmbeddingProperties.this.getInputType();
		}

		public void setInputType(@Nullable String inputType) {
			VoyageAiEmbeddingProperties.this.setInputType(inputType);
		}

	}

}
