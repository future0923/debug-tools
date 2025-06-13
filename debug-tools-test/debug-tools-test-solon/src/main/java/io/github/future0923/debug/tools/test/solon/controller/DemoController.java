/*
 * Copyright 2024-2025 the original author or authors.
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
package io.github.future0923.debug.tools.test.solon.controller;

import io.github.future0923.debug.tools.test.solon.service.DemoService;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

@Controller
public class DemoController {

    @Inject
    private DemoService demoService;

    @Get
    @Mapping("/demo1")
    public String demo1() {
        return demoService.hello3211("solon1");
    }

    @Get
    @Mapping("/demo2")
    public String dem1o2() {
        return "demo2";
    }

}