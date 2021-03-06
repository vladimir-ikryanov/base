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

package io.spine.js.generate.field.parser;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumDescriptor;
import io.spine.code.js.TypeName;
import io.spine.js.generate.output.CodeLines;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.js.generate.field.given.Given.enumField;
import static io.spine.js.generate.field.given.Given.messageField;
import static io.spine.js.generate.field.given.Given.primitiveField;
import static io.spine.js.generate.field.given.Given.timestampField;
import static io.spine.js.generate.field.parser.FieldParser.parserFor;
import static io.spine.js.generate.given.Generators.assertContains;
import static io.spine.js.generate.parse.FromJsonMethod.FROM_OBJECT;

@SuppressWarnings("DuplicateStringLiteralInspection")
// Generated code duplication needed to check main class.
@DisplayName("FieldParser should")
class FieldParserTest {

    private static final String VALUE = "value";
    private static final String VARIABLE = "variable";

    private CodeLines jsOutput;

    @BeforeEach
    void setUp() {
        jsOutput = new CodeLines();
    }

    @Test
    @DisplayName("parse primitive field via predefined code")
    void parsePrimitive() {
        FieldParser parser = parserFor(primitiveField(), jsOutput);
        parser.parseIntoVariable(VALUE, VARIABLE);
        String parse = "let " + VARIABLE + " = parseInt(" + VALUE + ')';
        assertContains(jsOutput, parse);
    }

    @Test
    @DisplayName("parse enum field via JS enum object attribute")
    void parseEnum() {
        FieldParser parser = parserFor(enumField(), jsOutput);
        parser.parseIntoVariable(VALUE, VARIABLE);
        EnumDescriptor enumType = enumField().getEnumType();
        TypeName typeName = TypeName.from(enumType);
        String parse = "let " + VARIABLE + " = " + typeName + '[' + VALUE + ']';
        assertContains(jsOutput, parse);
    }

    @Test
    @DisplayName("parse message field with custom type via recursive call to `fromObject`")
    void parseMessage() {
        FieldParser parser = parserFor(messageField(), jsOutput);
        parser.parseIntoVariable(VALUE, VARIABLE);
        Descriptor messageType = messageField().getMessageType();
        TypeName typeName = TypeName.from(messageType);
        String parse = "let " + VARIABLE + " = " + typeName + '.' + FROM_OBJECT + '(' + VALUE + ')';
        assertContains(jsOutput, parse);
    }

    @Test
    @DisplayName("parse message field with standard type via known type parser")
    void parseWellKnown() {
        FieldParser parser = parserFor(timestampField(), jsOutput);
        parser.parseIntoVariable(VALUE, VARIABLE);
        String parse = "let " + VARIABLE + " = parser.parse(" + VALUE + ')';
        assertContains(jsOutput, parse);
    }
}
