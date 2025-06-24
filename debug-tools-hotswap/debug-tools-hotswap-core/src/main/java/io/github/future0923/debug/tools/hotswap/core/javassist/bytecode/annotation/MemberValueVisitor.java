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
