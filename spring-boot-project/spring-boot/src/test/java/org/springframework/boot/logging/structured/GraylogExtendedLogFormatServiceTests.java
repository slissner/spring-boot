/*
 * Copyright 2012-2024 the original author or authors.
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

package org.springframework.boot.logging.structured;

import org.junit.jupiter.api.Test;

import org.springframework.boot.json.JsonWriter;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link GraylogExtendedLogFormatService}.
 *
 * @author Samuel Lissner
 */
class GraylogExtendedLogFormatServiceTests {

	@Test
	void getBindsFromEnvironment() {
		MockEnvironment environment = new MockEnvironment();
		environment.setProperty("logging.structured.gelf.service.name", "spring");
		environment.setProperty("logging.structured.gelf.service.version", "1.2.3");
		environment.setProperty("logging.structured.gelf.service.environment", "prod");
		environment.setProperty("logging.structured.gelf.service.node-name", "boot");
		GraylogExtendedLogFormatService service = GraylogExtendedLogFormatService.get(environment);
		assertThat(service).isEqualTo(new GraylogExtendedLogFormatService("spring", "1.2.3", "prod", "boot"));
	}

	@Test
	void getWhenNoServiceNameUsesApplicationName() {
		MockEnvironment environment = new MockEnvironment();
		environment.setProperty("spring.application.name", "spring");
		GraylogExtendedLogFormatService service = GraylogExtendedLogFormatService.get(environment);
		assertThat(service).isEqualTo(new GraylogExtendedLogFormatService("spring", null, null, null));
	}

	@Test
	void getWhenNoServiceVersionUsesApplicationVersion() {
		MockEnvironment environment = new MockEnvironment();
		environment.setProperty("spring.application.version", "1.2.3");
		GraylogExtendedLogFormatService service = GraylogExtendedLogFormatService.get(environment);
		assertThat(service).isEqualTo(new GraylogExtendedLogFormatService(null, "1.2.3", null, null));
	}

	@Test
	void getWhenNoPropertiesToBind() {
		MockEnvironment environment = new MockEnvironment();
		GraylogExtendedLogFormatService service = GraylogExtendedLogFormatService.get(environment);
		assertThat(service).isEqualTo(new GraylogExtendedLogFormatService(null, null, null, null));
	}

	@Test
	void addToJsonMembersCreatesValidJson() {
		GraylogExtendedLogFormatService service = new GraylogExtendedLogFormatService("spring", "1.2.3", "prod",
				"boot");
		JsonWriter<GraylogExtendedLogFormatService> writer = JsonWriter.of(service::jsonMembers);
		assertThat(writer.writeToString(service)).isEqualTo("{\"host\":\"spring\",\"_service_version\":\"1.2.3\","
				+ "\"_service_environment\":\"prod\",\"_service_node_name\":\"boot\"}");
	}

}