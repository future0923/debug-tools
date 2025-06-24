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
package io.github.future0923.debug.tools.hotswap.core.javassist.expr;

import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtBehavior;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.Bytecode;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.CodeAttribute;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.CodeIterator;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.ConstPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.ExceptionTable;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.MethodInfo;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.Opcode;
import io.github.future0923.debug.tools.hotswap.core.javassist.compiler.CompileError;
import io.github.future0923.debug.tools.hotswap.core.javassist.compiler.Javac;

/**
 * A <code>catch</code> clause or a <code>finally</code> block.
 */
public class Handler extends Expr {
    private static String EXCEPTION_NAME = "$1";
    private ExceptionTable etable;
    private int index;

    /**
     * Undocumented constructor.  Do not use; internal-use only.
     */
    protected Handler(ExceptionTable et, int nth,
                      CodeIterator it, CtClass declaring, MethodInfo m) {
        super(et.handlerPc(nth), it, declaring, m);
        etable = et;
        index = nth;
    }

    /**
     * Returns the method or constructor containing the catch clause.
     */
    @Override
    public CtBehavior where() { return super.where(); }

    /**
     * Returns the source line number of the catch clause.
     *
     * @return -1       if this information is not available.
     */
    @Override
    public int getLineNumber() {
        return super.getLineNumber();
    }

    /**
     * Returns the source file containing the catch clause.
     *
     * @return null     if this information is not available.
     */
    @Override
    public String getFileName() {
        return super.getFileName();
    }

    /**
     * Returns the list of exceptions that the catch clause may throw.
     */
    @Override
    public CtClass[] mayThrow() {
        return super.mayThrow();
    }

    /**
     * Returns the type handled by the catch clause.
     * If this is a <code>finally</code> block, <code>null</code> is returned.
     */
    public CtClass getType() throws NotFoundException {
        int type = etable.catchType(index);
        if (type == 0)
            return null;
        ConstPool cp = getConstPool();
        String name = cp.getClassInfo(type);
        return thisClass.getClassPool().getCtClass(name);
    }

    /**
     * Returns true if this is a <code>finally</code> block.
     */
    public boolean isFinally() {
        return etable.catchType(index) == 0;
    }

    /**
     * This method has not been implemented yet.
     *
     * @param statement         a Java statement except try-catch.
     */
    @Override
    public void replace(String statement) throws CannotCompileException {
        throw new RuntimeException("not implemented yet");
    }

    /**
     * Inserts bytecode at the beginning of the catch clause.
     * The caught exception is stored in <code>$1</code>.
     *
     * @param src       the source code representing the inserted bytecode.
     *                  It must be a single statement or block.
     */
    public void insertBefore(String src) throws CannotCompileException {
        edited = true;

        @SuppressWarnings("unused")
        ConstPool cp = getConstPool();
        CodeAttribute ca = iterator.get();
        Javac jv = new Javac(thisClass);
        Bytecode b = jv.getBytecode();
        b.setStackDepth(1);
        b.setMaxLocals(ca.getMaxLocals());

        try {
            CtClass type = getType();
            int var = jv.recordVariable(type, EXCEPTION_NAME);
            jv.recordReturnType(type, false);
            b.addAstore(var);
            jv.compileStmnt(src);
            b.addAload(var);

            int oldHandler = etable.handlerPc(index);
            b.addOpcode(Opcode.GOTO);
            b.addIndex(oldHandler - iterator.getCodeLength()
                       - b.currentPc() + 1);

            maxStack = b.getMaxStack();
            maxLocals = b.getMaxLocals();

            int pos = iterator.append(b.get());
            iterator.append(b.getExceptionTable(), pos);
            etable.setHandlerPc(index, pos);
        }
        catch (NotFoundException e) {
            throw new CannotCompileException(e);
        }
        catch (CompileError e) {
            throw new CannotCompileException(e);
        }
    }
}
