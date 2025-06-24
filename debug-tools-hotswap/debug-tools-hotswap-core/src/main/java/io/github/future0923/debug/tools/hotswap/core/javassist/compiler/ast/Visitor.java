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
package io.github.future0923.debug.tools.hotswap.core.javassist.compiler.ast;

import io.github.future0923.debug.tools.hotswap.core.javassist.compiler.CompileError;

/**
 * The visitor pattern.
 *
 * @see ASTree#accept(Visitor)
 */
public class Visitor {
    public void atASTList(ASTList n) throws CompileError {}
    public void atPair(Pair n) throws CompileError {}

    public void atFieldDecl(FieldDecl n) throws CompileError {}
    public void atMethodDecl(MethodDecl n) throws CompileError {}
    public void atStmnt(Stmnt n) throws CompileError {}
    public void atDeclarator(Declarator n) throws CompileError {}

    public void atAssignExpr(AssignExpr n) throws CompileError {}
    public void atCondExpr(CondExpr n) throws CompileError {}
    public void atBinExpr(BinExpr n) throws CompileError {}
    public void atExpr(Expr n) throws CompileError {}
    public void atCallExpr(CallExpr n) throws CompileError {}
    public void atCastExpr(CastExpr n) throws CompileError {}
    public void atInstanceOfExpr(InstanceOfExpr n) throws CompileError {}
    public void atNewExpr(NewExpr n) throws CompileError {}

    public void atSymbol(Symbol n) throws CompileError {}
    public void atMember(Member n) throws CompileError {}
    public void atVariable(Variable n) throws CompileError {}
    public void atKeyword(Keyword n) throws CompileError {}
    public void atStringL(StringL n) throws CompileError {}
    public void atIntConst(IntConst n) throws CompileError {}
    public void atDoubleConst(DoubleConst n) throws CompileError {}
    public void atArrayInit(ArrayInit n) throws CompileError {}
}
