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
package io.github.future0923.debug.tools.idea.file;

import com.intellij.lang.Language;
import com.intellij.lexer.Lexer;
import com.intellij.lexer.LexerBase;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * 注释文本变灰色
 *
 * @author future0923
 */
public class DebugConfSyntaxHighlighter implements SyntaxHighlighter {

    public static final TextAttributesKey COMMENT =
            TextAttributesKey.createTextAttributesKey(
                    "DEBUG_CONF_COMMENT",
                    new TextAttributes(
                            DefaultLanguageHighlighterColors.LINE_COMMENT
                                    .getDefaultAttributes()
                                    .getForegroundColor(),
                            null,
                            null,
                            null,
                            Font.ITALIC
                    )
            );


    @Override
    public @NotNull Lexer getHighlightingLexer() {
        return new LexerBase() {

            private CharSequence buffer;
            private int start;
            private int end;

            @Override
            public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
                this.buffer = buffer;
                this.start = startOffset;
                this.end = endOffset;
            }

            @Override
            public int getState() {
                return 0;
            }

            @Override
            public @Nullable IElementType getTokenType() {
                if (start >= end) {
                    return null;
                }

                // ⭐ 关键判断：是否是「行首 #」
                if (isLineStart(start) && buffer.charAt(start) == '#') {
                    return DebugConfTokenTypes.COMMENT;
                }

                return TokenType.WHITE_SPACE;
            }

            @Override
            public int getTokenStart() {
                return start;
            }

            @Override
            public int getTokenEnd() {
                if (start >= end) {
                    return start;
                }

                // 行首注释：吃掉整行
                if (isLineStart(start) && buffer.charAt(start) == '#') {
                    int i = start;
                    while (i < end && buffer.charAt(i) != '\n') {
                        i++;
                    }
                    return i;
                }

                // 其他情况：只吃 1 个字符（保证推进）
                return start + 1;
            }

            @Override
            public void advance() {
                int tokenEnd = getTokenEnd();
                if (tokenEnd <= start) {
                    start++;
                } else {
                    start = tokenEnd;
                }
            }

            private boolean isLineStart(int offset) {
                return offset == 0 || buffer.charAt(offset - 1) == '\n';
            }

            @Override
            public @NotNull CharSequence getBufferSequence() {
                return buffer;
            }

            @Override
            public int getBufferEnd() {
                return end;
            }
        };
    }


    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        if (tokenType == DebugConfTokenTypes.COMMENT) {
            return new TextAttributesKey[]{COMMENT};
        }
        return TextAttributesKey.EMPTY_ARRAY;
    }

    /**
     * @author future0923
     */
    public interface DebugConfTokenTypes {

        IElementType COMMENT = new IElementType("DEBUG_CONF_COMMENT", Language.ANY);
    }

}
