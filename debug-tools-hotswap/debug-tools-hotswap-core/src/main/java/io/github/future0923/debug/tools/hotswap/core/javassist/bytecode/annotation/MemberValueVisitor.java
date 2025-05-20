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
package io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.annotation;


/**
 * Visitor for traversing member values included in an annotation.
 *
 * @see MemberValue#accept(MemberValueVisitor)
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 */
public interface MemberValueVisitor {
   public void visitAnnotationMemberValue(AnnotationMemberValue node);
   public void visitArrayMemberValue(ArrayMemberValue node);
   public void visitBooleanMemberValue(BooleanMemberValue node);
   public void visitByteMemberValue(ByteMemberValue node);
   public void visitCharMemberValue(CharMemberValue node);
   public void visitDoubleMemberValue(DoubleMemberValue node);
   public void visitEnumMemberValue(EnumMemberValue node);
   public void visitFloatMemberValue(FloatMemberValue node);
   public void visitIntegerMemberValue(IntegerMemberValue node);
   public void visitLongMemberValue(LongMemberValue node);
   public void visitShortMemberValue(ShortMemberValue node);
   public void visitStringMemberValue(StringMemberValue node);
   public void visitClassMemberValue(ClassMemberValue node);
}
