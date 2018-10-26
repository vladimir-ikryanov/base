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

package io.spine.code.proto;

import io.spine.testing.UtilityClassTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.code.proto.given.Given.enumField;
import static io.spine.code.proto.given.Given.mapField;
import static io.spine.code.proto.given.Given.messageField;
import static io.spine.code.proto.given.Given.primitiveField;
import static io.spine.code.proto.given.Given.repeatedField;
import static io.spine.code.proto.given.Given.singularField;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("FieldTypes utility class should")
class FieldTypesTest extends UtilityClassTest<FieldTypes> {

    FieldTypesTest() {
        super(FieldTypes.class);
    }

    @Nested
    @DisplayName("check if field")
    class CheckIfField {

        @Test
        @DisplayName("is message")
        void isMessage() {
            assertTrue(FieldTypes.isMessage(messageField().toProto()));
            assertFalse(FieldTypes.isMessage(primitiveField().toProto()));
            assertFalse(FieldTypes.isMessage(enumField().toProto()));
        }

        @Test
        @DisplayName("is repeated")
        void isRepeated() {
            assertTrue(FieldTypes.isRepeated(repeatedField().toProto()));
            assertFalse(FieldTypes.isRepeated(mapField().toProto()));
            assertFalse(FieldTypes.isRepeated(singularField().toProto()));
        }

        @Test
        @DisplayName("is map")
        void isMap() {
            assertTrue(FieldTypes.isMap(mapField().toProto()));
            assertFalse(FieldTypes.isMap(singularField().toProto()));
        }
    }

    @Test
    @DisplayName("obtain a map entry name")
    void obtainEntryName() {
        assertEquals("MapFieldEntry", FieldTypes.getEntryNameFor(mapField().toProto()));
    }
}
