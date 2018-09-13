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

package io.spine.tools.protojs.field;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import io.spine.testing.UtilityClassTest;
import io.spine.type.TypeUrl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import spine.test.protojs.Task.TaskId;

import static com.google.protobuf.Descriptors.FieldDescriptor.Type.INT64;
import static com.google.protobuf.Descriptors.FieldDescriptor.Type.MESSAGE;
import static io.spine.tools.protojs.field.Fields.capitalizedName;
import static io.spine.tools.protojs.field.Fields.keyDescriptor;
import static io.spine.tools.protojs.field.Fields.valueDescriptor;
import static io.spine.tools.protojs.given.Given.mapField;
import static io.spine.tools.protojs.given.Given.messageField;
import static io.spine.tools.protojs.given.Given.primitiveField;
import static io.spine.tools.protojs.given.Given.repeatedField;
import static io.spine.tools.protojs.given.Given.timestampField;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Dmytro Kuzmin
 */
@SuppressWarnings("InnerClassMayBeStatic") // JUnit nested classes cannot be static.
@DisplayName("Fields utility should")
class FieldsTest extends UtilityClassTest<Fields> {

    FieldsTest() {
        super(Fields.class);
    }

    @Nested
    @DisplayName("check if field")
    class CheckIfField {

        @Test
        @DisplayName("is message")
        void isMessage() {
            assertTrue(Fields.isMessage(messageField()));
            assertFalse(Fields.isMessage(primitiveField()));
            assertTrue(Fields.isMessage(repeatedField()));
        }

        @Test
        @DisplayName("is standard type with known parser")
        void isWellKnownType() {
            assertTrue(Fields.isWellKnownType(timestampField()));
            assertFalse(Fields.isWellKnownType(messageField()));
            assertFalse(Fields.isWellKnownType(primitiveField()));
        }

        @Test
        @DisplayName("is repeated")
        void isRepeated() {
            assertTrue(Fields.isRepeated(repeatedField()));
        }

        @Test
        @DisplayName("is map")
        void isMap() {
            assertTrue(Fields.isMap(mapField()));
        }
    }

    @Test
    @DisplayName("not mark map field as repeated")
    void notMarkMapAsRepeated() {
        assertFalse(Fields.isRepeated(mapField()));
    }

    @Test
    @DisplayName("get key descriptor for map field")
    void getKeyDescriptor() {
        FieldDescriptor key = keyDescriptor(mapField());
        assertEquals(INT64, key.getType());
    }

    @Test
    @DisplayName("get value descriptor for map field")
    void getValueDescriptor() {
        FieldDescriptor value = valueDescriptor(mapField());
        assertEquals(MESSAGE, value.getType());
        Descriptor messageType = value.getMessageType();
        TypeUrl typeUrl = TypeUrl.from(messageType);
        TypeUrl expected = TypeUrl.from(TaskId.getDescriptor());
        assertEquals(expected, typeUrl);
    }

    @SuppressWarnings({"CheckReturnValue", "ResultOfMethodCallIgnored"})
    // Calling methods to throw exception.
    @Nested
    @DisplayName("throw ISE if")
    class ThrowIse {

        @Test
        @DisplayName("getting key descriptor from non-map field")
        void getKeyForNonMap() {
            assertThrows(IllegalStateException.class, () -> keyDescriptor(repeatedField()));
        }

        @Test
        @DisplayName("getting value descriptor from non-map field")
        void getValueForNonMap() {
            assertThrows(IllegalStateException.class, () -> valueDescriptor(repeatedField()));
        }
    }

    @Test
    @DisplayName("return capitalized field name")
    void getCapitalizedName() {
        String capitalizedName = capitalizedName(messageField());
        String expected = "MessageField";
        assertEquals(expected, capitalizedName);
    }
}
