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

import org.junit.jupiter.api.Test;

import org.springframework.ai.retry.autoconfigure.SpringAiRetryAutoConfiguration;
import org.springframework.ai.voyageai.VoyageAiEmbeddingModel;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.restclient.autoconfigure.RestClientAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Voyage AI embedding auto-configuration.
 *
 * @author Spring AI Onboarding
 * @since 2.0.1
 */
class VoyageAiEmbeddingAutoConfigurationTests {

	private final ApplicationContextRunner embeddingContextRunner = new ApplicationContextRunner()
		.withPropertyValues("spring.ai.voyageai.api-key=test-api-key")
		.withConfiguration(AutoConfigurations.of(VoyageAiEmbeddingAutoConfiguration.class,
				RestClientAutoConfiguration.class, SpringAiRetryAutoConfiguration.class));

	@Test
	void embeddingModelActivation() {
		this.embeddingContextRunner
			.run(context -> assertThat(context.getBeansOfType(VoyageAiEmbeddingModel.class)).isNotEmpty());

		this.embeddingContextRunner.withPropertyValues("spring.ai.model.embedding=none").run(context -> {
			assertThat(context.getBeansOfType(VoyageAiEmbeddingProperties.class)).isEmpty();
			assertThat(context.getBeansOfType(VoyageAiEmbeddingModel.class)).isEmpty();
		});

		this.embeddingContextRunner.withPropertyValues("spring.ai.model.embedding=voyageai").run(context -> {
			assertThat(context.getBeansOfType(VoyageAiEmbeddingProperties.class)).isNotEmpty();
			assertThat(context.getBeansOfType(VoyageAiEmbeddingModel.class)).isNotEmpty();
		});
	}

	@Test
	void propertiesTest() {
		this.embeddingContextRunner
			.withPropertyValues("spring.ai.voyageai.base-url=TEST_BASE_URL",
					"spring.ai.voyageai.embedding.model=voyage-3-lite", "spring.ai.voyageai.embedding.input-type=query")
			.run(context -> {
				var embeddingProperties = context.getBean(VoyageAiEmbeddingProperties.class);
				var commonProperties = context.getBean(VoyageAiCommonProperties.class);

				assertThat(embeddingProperties.getModel()).isEqualTo("voyage-3-lite");
				assertThat(embeddingProperties.getInputType()).isEqualTo("query");
				assertThat(commonProperties.getBaseUrl()).isEqualTo("TEST_BASE_URL");
			});
	}

}
