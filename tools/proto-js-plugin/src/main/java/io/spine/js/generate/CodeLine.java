/*
 * Copyright 2018, TeamDev. All rights reserved.
 *
 * Redistribution and use in source and/or binary forms, with or without
 * modification, must retain the above copyright notice and the following
 * disclaimer.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.spine.js.generate;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 * A line of a JavaScript code.
 *
 * <p>The line is not aware of {@linkplain IndentedLine indentation}.
 */
public class CodeLine {

    private final String content;

    public CodeLine(String content) {
        checkNotNull(content);
        this.content = content;
    }

    public String content() {
        return content;
    }

    /**
     * Obtains the comment from the specified text.
     */
    public static CodeLine comment(String commentText) {
        checkNotNull(commentText);
        return new CodeLine("// " + commentText);
    }

    public static CodeLine mapEntry(String key, Object value) {
        checkNotNull(key);
        checkNotNull(value);
        String raw = format("['%s', %s]", key, value);
        return new CodeLine(raw);
    }

    /**
     * Composes a statement returning a string literal.
     */
    public static CodeLine returnString(String literalValue) {
        checkNotNull(literalValue);
        String literal = format("'%s'", literalValue);
        CodeLine result = returnValue(literal);
        return result;
    }

    /**
     * Composes a statement returning the value.
     */
    public static CodeLine returnValue(Object value) {
        checkNotNull(value);
        String statement = "return " + value + ';';
        return new CodeLine(statement);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CodeLine)) {
            return false;
        }
        CodeLine line = (CodeLine) o;
        return content.equals(line.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }

    @Override
    public String toString() {
        return content;
    }
}
