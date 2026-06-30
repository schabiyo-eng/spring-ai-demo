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
import org.mockito.Mockito;

import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.voyageai.api.VoyageAiApi;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link VoyageAiEmbeddingModel}. The Voyage API is mocked, so no live API
 * key or network access is required.
 *
 * @author Spring AI Onboarding
 */
class VoyageAiEmbeddingModelTests {

	@Test
	void callReturnsMappedEmbeddings() {
		VoyageAiApi mockApi = createMockApiWithEmbeddingResponse(1024);

		VoyageAiEmbeddingModel model = VoyageAiEmbeddingModel.builder()
			.voyageAiApi(mockApi)
			.metadataMode(MetadataMode.EMBED)
			.options(VoyageAiEmbeddingOptions.builder().model("voyage-3").build())
			.retryTemplate(RetryUtils.DEFAULT_RETRY_TEMPLATE)
			.build();

		EmbeddingResponse response = model.call(new EmbeddingRequest(List.of("hello world"), null));

		assertThat(response.getResults()).hasSize(1);
		assertThat(response.getResults().get(0).getOutput()).hasSize(1024);
		assertThat(response.getMetadata().getModel()).isEqualTo("voyage-3");
	}

	@Test
	void embedSingleStringReturnsVector() {
		VoyageAiApi mockApi = createMockApiWithEmbeddingResponse(512);

		VoyageAiEmbeddingModel model = VoyageAiEmbeddingModel.builder()
			.voyageAiApi(mockApi)
			.options(VoyageAiEmbeddingOptions.builder().model("voyage-3-lite").build())
			.build();

		float[] vector = model.embed("hello");

		assertThat(vector).hasSize(512);
	}

	@Test
	void builderCreatesValidModel() {
		VoyageAiApi mockApi = createMockApiWithEmbeddingResponse(1024);

		VoyageAiEmbeddingModel model = VoyageAiEmbeddingModel.builder()
			.voyageAiApi(mockApi)
			.options(VoyageAiEmbeddingOptions.builder().model("voyage-3").build())
			.build();

		assertThat(model).isNotNull();
	}

	@Test
	void optionsUseDefaultsWhenUnset() {
		VoyageAiEmbeddingOptions options = VoyageAiEmbeddingOptions.builder().build();

		assertThat(options.getModel()).isEqualTo(VoyageAiEmbeddingOptions.DEFAULT_EMBEDDING_MODEL);
		assertThat(options.getInputType()).isNull();
	}

	@Test
	void dimensionsReturnsKnownModelSize() {
		VoyageAiApi mockApi = createMockApiWithEmbeddingResponse(1024);

		VoyageAiEmbeddingModel model = VoyageAiEmbeddingModel.builder()
			.voyageAiApi(mockApi)
			.options(VoyageAiEmbeddingOptions.builder()
				.model(VoyageAiApi.EmbeddingModel.VOYAGE_3_LITE.getValue())
				.build())
			.build();

		assertThat(model.dimensions()).isEqualTo(512);
	}

	private VoyageAiApi createMockApiWithEmbeddingResponse(int dimensions) {
		VoyageAiApi mockApi = Mockito.mock(VoyageAiApi.class);

		float[] embedding = new float[dimensions];
		for (int i = 0; i < dimensions; i++) {
			embedding[i] = 0.1f;
		}

		VoyageAiApi.Embedding embeddingData = new VoyageAiApi.Embedding(0, embedding);
		VoyageAiApi.Usage usage = new VoyageAiApi.Usage(10);
		VoyageAiApi.EmbeddingList<VoyageAiApi.Embedding> embeddingList = new VoyageAiApi.EmbeddingList<>("list",
				List.of(embeddingData), "voyage-3", usage);

		when(mockApi.embeddings(any())).thenReturn(ResponseEntity.ok(embeddingList));

		return mockApi;
	}

}
