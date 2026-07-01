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
package io.github.future0923.debug.tools.test.spring.boot.mybatis.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/http")
public class HttpController {

    @GetMapping("/get")
    public Map<String, Object> get(@RequestParam(required = false) String keyword) {
        return result("GET", "/http/get", "keyword", keyword);
    }

    @GetMapping({"/get/alias", "/get/alias2"})
    public Map<String, Object> getAlias() {
        return result("GET", "/http/get/alias");
    }

    @GetMapping("/get/{id}")
    public Map<String, Object> getPathVariable(@PathVariable Long id) {
        return result("GET", "/http/get/" + id, "id", id);
    }

    @PostMapping("/post/json")
    public Map<String, Object> postJson(@RequestBody HttpRequestBody requestBody) {
        return result("POST", "/http/post/json", "body", requestBody);
    }

    @PostMapping(path = "/post/form", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Map<String, Object> postForm(@RequestParam String username, @RequestParam Integer age) {
        return result("POST", "/http/post/form", "username", username, "age", age);
    }

    @PostMapping(path = "/post/form-data", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> postFormData(
            @RequestParam String username,
            @RequestParam Integer age,
            @RequestParam(required = false) MultipartFile file
    ) {
        String fileName = file == null ? null : file.getOriginalFilename();
        long fileSize = file == null ? 0 : file.getSize();
        return result("POST", "/http/post/form-data", "username", username, "age", age, "fileName", fileName, "fileSize", fileSize);
    }

    @PostMapping(path = "/post/binary/bytes", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Map<String, Object> postBinaryBytes(@RequestBody byte[] body) {
        return result("POST", "/http/post/binary/bytes", "size", body.length);
    }

    @PostMapping(path = "/post/binary/stream", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Map<String, Object> postBinaryStream(HttpServletRequest request) throws IOException {
        return result("POST", "/http/post/binary/stream", "size", readRequestBody(request.getInputStream()).length);
    }

    @PutMapping("/put/{id}")
    public Map<String, Object> put(@PathVariable Long id, @RequestBody HttpRequestBody requestBody) {
        return result("PUT", "/http/put/{id}", "id", id, "body", requestBody);
    }

    @DeleteMapping("/delete/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        return result("DELETE", "/http/delete/{id}", "id", id);
    }

    @PatchMapping("/patch/{id}")
    public Map<String, Object> patch(@PathVariable Long id, @RequestBody Map<String, Object> patchBody) {
        return result("PATCH", "/http/patch/{id}", "id", id, "body", patchBody);
    }

    @RequestMapping(value = "/head", method = RequestMethod.HEAD)
    public void head() {
    }

    @RequestMapping(value = "/options", method = RequestMethod.OPTIONS)
    public Map<String, Object> options() {
        return result("OPTIONS", "/http/options");
    }

    @RequestMapping(value = "/request/multi-method", method = {RequestMethod.GET, RequestMethod.POST})
    public Map<String, Object> requestMultiMethod() {
        return result("GET|POST", "/http/request/multi-method");
    }

    @RequestMapping(value = "/request/params", method = RequestMethod.GET, params = "debug=true")
    public Map<String, Object> requestParams(@RequestParam String debug) {
        return result("GET", "/http/request/params", "debug", debug);
    }

    @RequestMapping(value = "/request/headers", method = RequestMethod.GET, headers = "X-Debug-Tools=true")
    public Map<String, Object> requestHeaders(@RequestHeader("X-Debug-Tools") String debugTools) {
        return result("GET", "/http/request/headers", "X-Debug-Tools", debugTools);
    }

    @RequestMapping(
            value = "/request/consumes",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public Map<String, Object> requestConsumes(@RequestBody HttpRequestBody requestBody) {
        return result("POST", "/http/request/consumes", "body", requestBody);
    }

    @RequestMapping(
            value = "/request/produces",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Map<String, Object> requestProduces() {
        return result("GET", "/http/request/produces");
    }

    @GetMapping("/regex/{code:[0-9]+}")
    public Map<String, Object> regexPathVariable(@PathVariable String code) {
        return result("GET", "/http/regex/{code:[0-9]+}", "code", code);
    }

    @GetMapping("/matrix/{category}")
    public Map<String, Object> matrixVariable(
            @PathVariable String category,
            @MatrixVariable(pathVar = "category", required = false) Map<String, List<String>> filters
    ) {
        return result("GET", "/http/matrix/{category}", "category", category, "filters", filters);
    }

    @GetMapping("/nested/{tenant}/orders/{orderId}")
    public Map<String, Object> nestedPath(
            @PathVariable String tenant,
            @PathVariable Long orderId,
            @RequestHeader(value = "X-Request-Id", required = false) String requestId,
            @CookieValue(value = "debug_token", required = false) String debugToken
    ) {
        return result(
                "GET",
                "/http/nested/{tenant}/orders/{orderId}",
                "tenant",
                tenant,
                "orderId",
                orderId,
                "requestId",
                requestId,
                "debugToken",
                debugToken
        );
    }

    private byte[] readRequestBody(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }
        return outputStream.toByteArray();
    }

    private Map<String, Object> result(String method, String path, Object... attributes) {
        Map<String, Object> result = new HashMap<>();
        result.put("method", method);
        result.put("path", path);
        for (int i = 0; i + 1 < attributes.length; i += 2) {
            result.put(String.valueOf(attributes[i]), attributes[i + 1]);
        }
        return result;
    }

    public static class HttpRequestBody {

        private Long id;
        private String name;
        private List<String> tags;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }
    }

}
