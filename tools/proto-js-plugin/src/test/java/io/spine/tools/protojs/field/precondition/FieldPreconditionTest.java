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

package io.spine.tools.protojs.field.precondition;

import io.spine.base.generate.JsOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.tools.protojs.field.precondition.FieldPreconditions.checkerFor;
import static io.spine.tools.protojs.given.Generators.assertContains;
import static io.spine.tools.protojs.given.Given.messageField;
import static io.spine.tools.protojs.given.Given.primitiveField;
import static java.lang.String.format;

/**
 * @author Dmytro Kuzmin
 */
@SuppressWarnings("DuplicateStringLiteralInspection")
// Generated code duplication needed to check main class.
@DisplayName("FieldPrecondition should")
class FieldPreconditionTest {

    private static final String FIELD_VALUE = "value";
    private static final String SETTER_FORMAT = "set(%s)";

    private JsOutput jsOutput;

    @BeforeEach
    void setUp() {
        jsOutput = new JsOutput();
    }

    @Test
    @DisplayName("generate code to enter non-null check for primitive")
    void enterPrimitiveCheck() {
        FieldPrecondition checker = checkerFor(primitiveField(), jsOutput);
        checker.performNullCheck(FIELD_VALUE, SETTER_FORMAT);
        String check = "if (" + FIELD_VALUE + " !== null)";
        assertContains(jsOutput, check);
    }

    @Test
    @DisplayName("generate code to enter null check for message")
    void enterMessageCheck() {
        FieldPrecondition checker = checkerFor(messageField(), jsOutput);
        checker.performNullCheck(FIELD_VALUE, SETTER_FORMAT);
        String check = "if (" + FIELD_VALUE + " === null)";
        assertContains(jsOutput, check);
    }

    @Test
    @DisplayName("set field value to null in case of message")
    void setMessageToNull() {
        FieldPrecondition checker = checkerFor(messageField(), jsOutput);
        checker.performNullCheck(FIELD_VALUE, SETTER_FORMAT);
        String setNull = format(SETTER_FORMAT, "null");
        assertContains(jsOutput, setNull);
    }
}
