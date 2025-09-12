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
package io.github.future0923.debug.tools.idea.listener.idea;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInsight.hint.HintManagerImpl;
import com.intellij.ide.TooltipEvent;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorKind;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseEventArea;
import com.intellij.openapi.editor.event.EditorMouseMotionListener;
import com.intellij.openapi.editor.ex.EditorGutterComponentEx;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.HintHint;
import com.intellij.ui.LightweightHint;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.util.ui.JBDimension;
import com.intellij.util.ui.JBUI;
import io.github.future0923.debug.tools.base.hutool.core.util.BooleanUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.ReflectUtil;
import io.github.future0923.debug.tools.idea.action.MouseQuickDebugAction;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.lang.ref.WeakReference;

/**
 * @author future0923
 */
public class QuickDebugEditorMouseMotionListener implements EditorMouseMotionListener {

    private static final int NEGATIVE_INLAY_PANEL_SHIFT = -3;
    private static final int ACTION_BUTTON_SIZE = 22;
    private WeakReference<RunToCursorHint> currentHint = new WeakReference<>(null);
    private WeakReference<Editor> currentEditor = new WeakReference<>(null);
    private int currentLineNumber = -1;
    private final DefaultActionGroup defaultActionGroup;
    private final MouseQuickDebugAction mouseQuickDebugAction;
    private final JComponent callMethodButton;

    public QuickDebugEditorMouseMotionListener() {
        this.mouseQuickDebugAction = new MouseQuickDebugAction();
        this.defaultActionGroup = new DefaultActionGroup();
        this.defaultActionGroup.add(mouseQuickDebugAction);
        this.callMethodButton = this.createRunMethodComponent();
    }

    private JComponent createRunMethodComponent() {
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("button@RunMethodEditorMouseMotionListener", this.defaultActionGroup, true);
        actionToolbar.setReservePlaceAutoPopupIcon(false);
        actionToolbar.getComponent().setOpaque(false);
        actionToolbar.getComponent().setBorder(null);
        ReflectUtil.invoke(actionToolbar, "setActionButtonBorder", JBUI.Borders.empty());
        NonOpaquePanel justPane = new NonOpaquePanel();
        justPane.setPreferredSize(new JBDimension(ACTION_BUTTON_SIZE * this.defaultActionGroup.getChildrenCount(), ACTION_BUTTON_SIZE));
        JComponent component = actionToolbar.getComponent();
        component.setBorder(null);
        justPane.add(actionToolbar.getComponent());
        return justPane;
    }

    private int getPopupTriggerThresholdX(Editor editor) {
        return EditorUtil.getSpaceWidth(0, editor) * 4;
    }

    private void clearHint() {
        currentEditor.clear();
        currentLineNumber = -1;
        RunToCursorHint hint = currentHint.get();
        if (hint != null) {
            hint.hide();
        }
    }

    @Override
    public void mouseMoved(@NotNull EditorMouseEvent event) {
        Editor editor = event.getEditor();
        if (!(editor instanceof EditorImpl)) {
            return;
        }
        Project project = editor.getProject();
        if (project == null) {
            return;
        }

        if (!BooleanUtil.isTrue(DebugToolsSettingState.getInstance(project).getLineMarkerVisible())) {
            return;
        }

        if (!EditorKind.MAIN_EDITOR.equals(editor.getEditorKind())) {
            return;
        }
        if (!EditorMouseEventArea.EDITING_AREA.equals(event.getArea())) {
            return;
        }
        int mouseX = event.getMouseEvent().getX();
        if (mouseX - editor.getScrollingModel().getHorizontalScrollOffset() > this.getPopupTriggerThresholdX(editor)) {
            clearHint();
            return;
        }
        LogicalPosition logicalPosition = editor.xyToLogicalPosition(event.getMouseEvent().getPoint());
        int offset = editor.logicalPositionToOffset(logicalPosition);
        Document document = editor.getDocument();
        PsiElement psiElement = this.getElementAtCaret(project, document, offset);
        if (!(psiElement instanceof PsiMethod method)) {
            return;
        }
        if (method.isConstructor()) {
            return;
        }
        if (method.isConstructor()) {
            return;
        }
        mouseQuickDebugAction.setPsiMethod(method);
        TextRange textRange = method.getNameIdentifier() != null
                ? method.getNameIdentifier().getTextRange()
                : (method.getBody() != null ? method.getBody().getTextRange() : method.getTextRange());
        int xPosition = JBUI.scale(NEGATIVE_INLAY_PANEL_SHIFT);
        int lineNumber = document.getLineNumber(textRange.getStartOffset());
        if (editor.equals(currentEditor.get()) && lineNumber == currentLineNumber && currentHint.get() != null) {
            return;
        }
        this.currentEditor = new WeakReference<>(editor);
        this.currentLineNumber = lineNumber;
        int lineY = editor.logicalPositionToXY(new LogicalPosition(lineNumber, 0)).y;
        JRootPane rootPane = editor.getComponent().getRootPane();
        Point p = SwingUtilities.convertPoint(
                editor.getContentComponent(),
                new Point(xPosition + editor.getScrollingModel().getHorizontalScrollOffset(),
                        lineY + (editor.getLineHeight() - JBUI.scale(ACTION_BUTTON_SIZE)) / 2),
                rootPane.getLayeredPane());
        EditorGutterComponentEx editorGutterComponentEx = (EditorGutterComponentEx) editor.getGutter();
        if (this.isOutOfVisibleEditor(rootPane, p.x, p.y, JBUI.scale(ACTION_BUTTON_SIZE), editorGutterComponentEx)) {
            return;
        }

        if (this.currentHint.get() != null) {
            this.currentHint.get().hide();
        }

        RunToCursorHint hint = new RunToCursorHint(this.callMethodButton, this);
        hint.setForceShowAsPopup(false);
        hint.setCancelOnOtherWindowOpen(true);
        hint.setCancelOnClickOutside(false);
        HintHint hintHint = HintManagerImpl.createHintHint(editor, p, hint, HintManager.RIGHT).setAwtTooltip(false);
        HintManagerImpl.getInstanceImpl().showEditorHint(
                hint,
                editor,
                p,
                HintManager.HIDE_BY_ANY_KEY | HintManager.HIDE_BY_TEXT_CHANGE | HintManager.HIDE_IF_OUT_OF_EDITOR | HintManager.UPDATE_BY_SCROLLING | HintManager.DONT_CONSUME_ESCAPE,
                0,
                false,
                hintHint);
    }

    private PsiElement getElementAtCaret(Project project, Document document, int offset) {
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if (psiFile == null) {
            return null;
        } else {
            PsiElement elementAt = psiFile.findElementAt(offset);
            return PsiTreeUtil.getParentOfType(elementAt, PsiMethod.class);
        }
    }

    private boolean isOutOfVisibleEditor(JRootPane rootPane, int x, int y, int h, JComponent editorContentComponent) {
        return SwingUtilities.getDeepestComponentAt(rootPane.getLayeredPane(), x, y) != editorContentComponent || SwingUtilities.getDeepestComponentAt(rootPane.getLayeredPane(), x, y + h) != editorContentComponent;
    }

    public static class RunToCursorHint extends LightweightHint {
        private final QuickDebugEditorMouseMotionListener listener;

        public RunToCursorHint(JComponent component, QuickDebugEditorMouseMotionListener listener) {
            super(component);
            this.listener = listener;
        }

        @Override
        public boolean vetoesHiding() {
            return false;
        }

        @Override
        public void show(@NotNull JComponent parentComponent, int x, int y, JComponent focusBackComponent, HintHint hintHint) {
            this.listener.currentHint = new WeakReference<>(this);
            super.show(parentComponent, x, y, focusBackComponent, new HintHint(parentComponent, new Point(x, y)));
        }

        @Override
        protected boolean canAutoHideOn(TooltipEvent event) {
            return super.canAutoHideOn(event);
        }

        @Override
        public void hide(boolean ok) {
            this.listener.currentHint.clear();
            super.hide(ok);
        }
    }
}
