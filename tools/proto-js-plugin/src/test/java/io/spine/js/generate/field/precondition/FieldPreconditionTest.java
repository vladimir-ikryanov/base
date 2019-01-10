/*
 * Copyright 2019, TeamDev. All rights reserved.
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

package io.spine.js.generate.field.precondition;

import io.spine.js.generate.output.CodeLines;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.js.generate.field.given.Given.messageField;
import static io.spine.js.generate.field.given.Given.primitiveField;
import static io.spine.js.generate.field.precondition.FieldPreconditions.preconditionFor;
import static io.spine.js.generate.given.Generators.assertContains;
import static java.lang.String.format;

@SuppressWarnings("DuplicateStringLiteralInspection")
// Generated code duplication needed to check main class.
@DisplayName("FieldPrecondition should")
class FieldPreconditionTest {

    private static final String FIELD_VALUE = "value";
    private static final String SETTER_FORMAT = "set(%s)";

    private CodeLines jsOutput;

    @BeforeEach
    void setUp() {
        jsOutput = new CodeLines();
    }

    @Test
    @DisplayName("generate code to enter non-null check for primitive")
    void enterPrimitiveCheck() {
        FieldPrecondition precondition = preconditionFor(primitiveField(), jsOutput);
        precondition.performNullCheck(FIELD_VALUE, SETTER_FORMAT);
        String check = "if (" + FIELD_VALUE + " !== null)";
        assertContains(jsOutput, check);
    }

    @Test
    @DisplayName("generate code to enter null check for message")
    void enterMessageCheck() {
        FieldPrecondition precondition = preconditionFor(messageField(), jsOutput);
        precondition.performNullCheck(FIELD_VALUE, SETTER_FORMAT);
        String check = "if (" + FIELD_VALUE + " === null)";
        assertContains(jsOutput, check);
    }

    @Test
    @DisplayName("set field value to null in case of message")
    void setMessageToNull() {
        FieldPrecondition precondition = preconditionFor(messageField(), jsOutput);
        precondition.performNullCheck(FIELD_VALUE, SETTER_FORMAT);
        String setNull = format(SETTER_FORMAT, "null");
        assertContains(jsOutput, setNull);
    }
}
