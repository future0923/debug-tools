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
package io.github.future0923.debug.tools.server.utils;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;

import java.util.List;

/**
 * @author future0923
 */
public class JavaParserUtils {

    public static String process(String sourceCode) {
        CompilationUnit compilationUnit = StaticJavaParser.parse(sourceCode);
        processRequiredArgsConstructor(compilationUnit);
        processNoArgsConstructor(compilationUnit);
        processAllArgsConstructor(compilationUnit);
        return compilationUnit.toString();
    }

    private static void processRequiredArgsConstructor(CompilationUnit compilationUnit) {
        compilationUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(clazz -> {
            if (clazz.isAnnotationPresent("RequiredArgsConstructor")) {
                genConstructor(clazz, true, "RequiredArgsConstructor");
            }
        });
    }

    private static void processNoArgsConstructor(CompilationUnit compilationUnit) {
        compilationUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(clazz -> {
            if (clazz.isAnnotationPresent("NoArgsConstructor")) {
                ConstructorDeclaration constructor = clazz.addConstructor(Modifier.Keyword.PUBLIC);
                BlockStmt constructorBody = new BlockStmt();
                constructor.setBody(constructorBody);
                clazz.getAnnotations().removeIf(a -> a.getName().getIdentifier().equals("NoArgsConstructor"));
            }
        });
    }

    private static void processAllArgsConstructor(CompilationUnit compilationUnit) {
        compilationUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(clazz -> {
            if (clazz.isAnnotationPresent("AllArgsConstructor")) {
                genConstructor(clazz, false, "AllArgsConstructor");
            }
        });
    }

    private static void genConstructor(ClassOrInterfaceDeclaration clazz, boolean onlyFinal, String annotationName) {
        List<FieldDeclaration> fields = clazz.getFields();
        ConstructorDeclaration constructor = clazz.addConstructor(Modifier.Keyword.PUBLIC);
        BlockStmt constructorBody = new BlockStmt();
        for (FieldDeclaration field : fields) {
            if (onlyFinal && !field.isFinal()) {
                continue;
            }
            for (VariableDeclarator var : field.getVariables()) {
                String name = var.getNameAsString();
                String type = var.getType().asString();
                constructor.addParameter(type, name);
                constructorBody.addStatement(
                        new ExpressionStmt(new AssignExpr(
                                new FieldAccessExpr(new ThisExpr(), name),
                                new NameExpr(name),
                                AssignExpr.Operator.ASSIGN
                        ))
                );
            }
        }
        constructor.setBody(constructorBody);
        clazz.getAnnotations().removeIf(a -> a.getName().getIdentifier().equals(annotationName));
    }
}
