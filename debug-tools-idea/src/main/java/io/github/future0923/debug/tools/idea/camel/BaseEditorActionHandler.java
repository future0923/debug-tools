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
package io.github.future0923.debug.tools.idea.camel;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.editor.actions.EditorActionUtil;
import com.intellij.openapi.project.Project;
import com.intellij.util.ThrowableRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author future0923
 */
public abstract class BaseEditorActionHandler extends EditorActionHandler {

    public BaseEditorActionHandler() {
        super(true);
    }

    /**
     * camelCase
     */
    protected boolean useCamelCase() {
        return false;
    }

    /**
     * snake_case
     */
    protected boolean useLowerSnakeCase() {
        return false;
    }

    /**
     * CamelCase
     */
    protected boolean usePascalCase() {
        return false;
    }

    /**
     * SNAKE_CASE
     */
    protected boolean useUpperSnakeCase() {
        return false;
    }

    /**
     * kebab-case
     */
    protected boolean useKebabCase() {
        return false;
    }

    /**
     * space case
     */
    protected boolean useSpaceCase() {
        return false;
    }

    /**
     * Camel Case
     */
    protected boolean usePascalSpaceCase() {
        return false;
    }

    @Override
    protected final void doExecute(@NotNull final Editor editor, @Nullable final Caret caret, final DataContext dataContext) {
        beforeWriteAction(editor);
        new EditorWriteActionHandler(false) {
            @Override
            public void executeWriteAction(@NotNull Editor editor1, @Nullable Caret caret1, DataContext dataContext1) {
            }
        }.doExecute(editor, caret, dataContext);
    }

    private void beforeWriteAction(Editor editor) {
        String text = editor.getSelectionModel().getSelectedText();
        if (text == null || text.isEmpty()) {
            editor.getSelectionModel().selectWordAtCaret(true);
            boolean moveLeft = true;
            boolean moveRight = true;
            int start = editor.getSelectionModel().getSelectionStart();
            int end = editor.getSelectionModel().getSelectionEnd();
            Pattern p = Pattern.compile("[^A-z0-9.\\-]");

            // move caret left
            while (moveLeft && start != 0) {
                start--;
                EditorActionUtil.moveCaret(editor.getCaretModel().getCurrentCaret(), start, true);
                Matcher m = p.matcher(Objects.requireNonNull(editor.getSelectionModel().getSelectedText()));
                if (m.find()) {
                    start++;
                    moveLeft = false;
                }
            }

            editor.getSelectionModel().setSelection(end - 1, end);

            // move caret right
            while (moveRight && end != editor.getDocument().getTextLength()) {
                end++;
                EditorActionUtil.moveCaret(editor.getCaretModel().getCurrentCaret(), end, true);
                Matcher m = p.matcher(Objects.requireNonNull(editor.getSelectionModel().getSelectedText()));
                if (m.find()) {
                    end--;
                    moveRight = false;
                }
            }
            editor.getSelectionModel().setSelection(start, end);

            text = editor.getSelectionModel().getSelectedText();
        }
        Project project = editor.getProject();

        String newText = this.runWithoutConfig(text);
        final Editor fEditor = editor;
        final String fReplacement = newText;
        Runnable runnable = () -> BaseEditorActionHandler.replaceText(fEditor, fReplacement);
        ApplicationManager.getApplication().runWriteAction(getRunnableWrapper(fEditor.getProject(), runnable));
    }

    private static void replaceText(final Editor editor, final String replacement) {
        try {
            WriteAction.run((ThrowableRunnable<Throwable>) () -> {
                int start = editor.getSelectionModel().getSelectionStart();
                EditorModificationUtil.insertStringAtCaret(editor, replacement);
                editor.getSelectionModel().setSelection(start, start + replacement.length());
            });
        } catch (Throwable ignored) {

        }
    }

    private String runWithoutConfig(String text) {
        return Conversion.transform(text,
                usePascalSpaceCase(),
                useSpaceCase(),
                useKebabCase(),
                useUpperSnakeCase(),
                usePascalCase(),
                useCamelCase(),
                useLowerSnakeCase());
    }


    private Runnable getRunnableWrapper(final Project project, final Runnable runnable) {
        return () -> CommandProcessor.getInstance().executeCommand(project, runnable, "CamelCase", ActionGroup.EMPTY_GROUP);
    }
}
