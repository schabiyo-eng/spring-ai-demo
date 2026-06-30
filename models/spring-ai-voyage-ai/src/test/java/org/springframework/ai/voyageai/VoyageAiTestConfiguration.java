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

import org.springframework.ai.voyageai.api.VoyageAiApi;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

/**
 * Test configuration for Voyage AI integration tests.
 *
 * @author Spring AI Onboarding
 * @since 2.0.1
 */
@SpringBootConfiguration
public class VoyageAiTestConfiguration {

	private static String retrieveApiKey() {
		var apiKey = System.getenv("VOYAGE_AI_API_KEY");
		if (!StringUtils.hasText(apiKey)) {
			throw new IllegalArgumentException(
					"Missing VOYAGE_AI_API_KEY environment variable. Please set it to your Voyage AI API key.");
		}
		return apiKey;
	}

	@Bean
	public VoyageAiApi voyageAiApi() {
		return VoyageAiApi.builder().apiKey(retrieveApiKey()).build();
	}

	@Bean
	public VoyageAiEmbeddingModel voyageAiEmbeddingModel(VoyageAiApi api) {
		return VoyageAiEmbeddingModel.builder().voyageAiApi(api).build();
	}

}
