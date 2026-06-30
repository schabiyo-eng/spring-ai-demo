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

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.voyageai.api.VoyageAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link VoyageAiEmbeddingModel}.
 *
 * @author Spring AI Onboarding
 * @since 2.0.1
 */
@SpringBootTest(classes = VoyageAiTestConfiguration.class)
@EnabledIfEnvironmentVariable(named = "VOYAGE_AI_API_KEY", matches = ".+")
class VoyageAiEmbeddingIT {

	private static final int VOYAGE_3_DIMENSIONS = 1024;

	@Autowired
	private VoyageAiApi voyageAiApi;

	@Autowired
	private VoyageAiEmbeddingModel voyageAiEmbeddingModel;

	@Test
	void defaultEmbedding() {
		var response = this.voyageAiEmbeddingModel.call(new EmbeddingRequest(List.of("hello world"), null));

		assertThat(response.getResults()).hasSize(1);
		assertThat(response.getResults().get(0).getOutput()).hasSize(VOYAGE_3_DIMENSIONS);
		assertThat(response.getMetadata().getModel()).isNotBlank();
		assertThat(this.voyageAiEmbeddingModel.dimensions()).isEqualTo(VOYAGE_3_DIMENSIONS);
	}

	@Test
	void embedSingleString() {
		float[] vector = this.voyageAiEmbeddingModel.embed("hello");

		assertThat(vector).hasSize(VOYAGE_3_DIMENSIONS);
	}

	@Test
	void embedWithInputType() {
		var options = VoyageAiEmbeddingOptions.builder()
			.model(VoyageAiApi.EmbeddingModel.VOYAGE_3.getValue())
			.inputType("query")
			.build();

		var response = this.voyageAiEmbeddingModel.call(new EmbeddingRequest(List.of("search query"), options));

		assertThat(response.getResults()).hasSize(1);
		assertThat(response.getResults().get(0).getOutput()).hasSize(VOYAGE_3_DIMENSIONS);
	}

}
