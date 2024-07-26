package io.github.future0923.debug.power.idea.navigation;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseListener;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.JBColor;
import io.github.future0923.debug.power.idea.client.ApplicationClientHolder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author future0923
 */
public class ClassNameHighlighter {
    public static final Pattern CLASS_NAME_PATTERN = Pattern.compile("\\b([a-zA-Z0-9_$.]+)\\.java:(\\d+)\\b", Pattern.UNICODE_CHARACTER_CLASS);

    private static final String CLASS_INFO_PATTERN = "(?<fullClassName>[a-zA-Z0-9_$.]+)\\.(?<methodName>[a-zA-Z0-9_]+)\\(%s\\.java:%s\\)";

    public static void highlightClassNames(EditorTextField editorTextField) {
        SwingUtilities.invokeLater(() -> {
            String text = editorTextField.getText();
            Matcher matcher = CLASS_NAME_PATTERN.matcher(text);
            Editor editor = editorTextField.getEditor();
            if (editor == null) {
                return;
            }
            MarkupModel markupModel = editor.getMarkupModel();

            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                TextAttributes attributes = new TextAttributes();
                attributes.setForegroundColor(JBColor.BLUE);
                attributes.setEffectType(EffectType.LINE_UNDERSCORE);
                attributes.setEffectColor(JBColor.BLUE);

                markupModel.addRangeHighlighter(
                        start,
                        end,
                        HighlighterLayer.SYNTAX,
                        attributes,
                        HighlighterTargetArea.EXACT_RANGE
                );

            }

            editor.addEditorMouseListener(new EditorMouseListener() {
                @Override
                public void mouseClicked(@NotNull EditorMouseEvent event) {
                    Editor editor = event.getEditor();
                    int offset = editor.getCaretModel().getOffset();
                    RangeHighlighter[] highlighters = markupModel.getAllHighlighters();
                    for (RangeHighlighter highlighter : highlighters) {
                        if (highlighter.getStartOffset() <= offset && offset <= highlighter.getEndOffset()) {
                            String highlighterText = text.substring(highlighter.getStartOffset(), highlighter.getEndOffset());
                            String[] split = highlighterText.replace(".java", "").split(":");
                            String className = split[0];
                            int lineNumber = Integer.parseInt(split[1]);
                            Pattern classInfoPattern = Pattern.compile(String.format(CLASS_INFO_PATTERN, className, lineNumber), Pattern.UNICODE_CHARACTER_CLASS);
                            Matcher matcher = classInfoPattern.matcher(text);
                            if (matcher.find()) {
                                String fullClassName = matcher.group("fullClassName");
                                String methodName = matcher.group("methodName");
                                jumpToClass(className, lineNumber, fullClassName, methodName);
                            }
                            break;
                        }
                    }
                }
            });
        });
    }

    private static void jumpToClass(String className, int lineNumber, String fullClassName, String methodName) {
        Project project = ApplicationClientHolder.PROJECT;
        PsiClass[] classes = JavaPsiFacade.getInstance(project).findClasses(fullClassName, GlobalSearchScope.allScope(project));
        //PsiClass[] classes = PsiShortNamesCache.getInstance(project).getClassesByName(className, GlobalSearchScope.allScope(project));
        if (classes.length > 0) {
            PsiClass psiClass = classes[0];
            VirtualFile file = psiClass.getContainingFile().getVirtualFile();
            OpenFileDescriptor descriptor = new OpenFileDescriptor(project, file, lineNumber - 1, 0);
            FileEditorManager.getInstance(project).openTextEditor(descriptor, true);
        } else {
            Messages.showErrorDialog("Class not found: " + className, "Error");
        }
    }
}
