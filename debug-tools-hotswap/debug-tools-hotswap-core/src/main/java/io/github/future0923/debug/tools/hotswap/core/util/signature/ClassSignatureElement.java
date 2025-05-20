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
package io.github.future0923.debug.tools.hotswap.core.util.signature;

/**
 * 检查签名元素
 */
public enum ClassSignatureElement {
    SUPER_CLASS,
    INTERFACES,
    CLASS_ANNOTATION,
    CONSTRUCTOR,
    CONSTRUCTOR_PRIVATE, // private constructors are used if CONSTRUCTOR && CONSTRUCTOR_PRIVATE are set
    METHOD,
    METHOD_PRIVATE, // private methods are used if METHOD && METHOD_PRIVATE are set
    METHOD_STATIC,  // static methods are used if METHOD && METHOD_STATIC are set
    METHOD_ANNOTATION, // applies to constructors as well
    METHOD_PARAM_ANNOTATION, // applies to constructors as well
    METHOD_EXCEPTION, // applies to constructors as well
    FIELD,
    FIELD_STATIC,  // static fields are used if FIELD && FIELD_STATIC are set
    FIELD_ANNOTATION
}
