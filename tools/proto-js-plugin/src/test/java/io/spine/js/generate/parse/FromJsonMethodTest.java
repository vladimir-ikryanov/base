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

package io.spine.js.generate.parse;

import com.google.common.testing.NullPointerTester;
import com.google.protobuf.Descriptors.Descriptor;
import io.spine.code.js.TypeName;
import io.spine.js.generate.output.CodeLines;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.js.generate.given.Generators.assertContains;
import static io.spine.js.generate.given.Given.message;
import static io.spine.js.generate.parse.FromJsonMethod.FROM_JSON;
import static io.spine.js.generate.parse.FromJsonMethod.FROM_JSON_ARG;
import static io.spine.js.generate.parse.FromJsonMethod.FROM_OBJECT;
import static io.spine.js.generate.parse.FromJsonMethod.FROM_OBJECT_ARG;
import static io.spine.testing.DisplayNames.NOT_ACCEPT_NULLS;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SuppressWarnings("DuplicateStringLiteralInspection")
// Duplication to check the code generated by main class.
@DisplayName("MessageGenerator should")
class FromJsonMethodTest {

    private Descriptor message;
    private FromJsonMethod generator;

    @BeforeEach
    void setUp() {
        message = message();
        generator = FromJsonMethod.createFor(message);
    }

    @Test
    @DisplayName(NOT_ACCEPT_NULLS)
    void passNullToleranceCheck() {
        new NullPointerTester().setDefault(Descriptor.class, message)
                               .testAllPublicStaticMethods(FromJsonMethod.class);
    }

    @Test
    @DisplayName("generate `fromJson` method for message")
    void generateFromJson() {
        CodeLines snippet = generator.generateFromJsonMethod();
        String methodDeclaration = TypeName.from(message) + "." + FROM_JSON;
        assertContains(snippet, methodDeclaration);
    }

    @Test
    @DisplayName("parse JSON into JS object in `fromJson` method")
    void parseJsonIntoObject() {
        CodeLines snippet = generator.generateFromJsonMethod();
        String parseStatement = "JSON.parse(" + FROM_JSON_ARG + ')';
        assertContains(snippet, parseStatement);
    }

    @Test
    @DisplayName("generate `fromObject` method for message")
    void generateFromObject() {
        CodeLines snippet = generator.generateFromObjectMethod();
        String methodDeclaration = TypeName.from(message) + "." + FROM_OBJECT;
        assertContains(snippet, methodDeclaration);
    }

    @Test
    @DisplayName("check parsed object for null in `fromObject` method")
    void checkJsObjectForNull() {
        CodeLines snippet = generator.generateFromObjectMethod();
        String check = "if (" + FROM_OBJECT_ARG + " === null) {";
        assertContains(snippet, check);
    }

    @SuppressWarnings("AccessStaticViaInstance") // For the testing purpose.
    @Test
    @DisplayName("handle message fields in `fromObject` method")
    void handleMessageFields() {
        FromJsonMethod generator = spy(this.generator);
        CodeLines snippet = generator.generateFromObjectMethod();
        verify(generator, times(1))
                .handleMessageFields(new CodeLines(), message);
        assertNotNull(snippet);
    }
}
