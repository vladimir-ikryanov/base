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

package io.spine.protobuf;

import com.google.protobuf.Empty;
import com.google.protobuf.Message;
import com.google.protobuf.StringValue;
import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.google.protobuf.Descriptors.FieldDescriptor;
import static com.google.protobuf.Descriptors.FieldDescriptor.JavaType;
import static io.spine.base.Identifier.newUuid;
import static io.spine.protobuf.MessageField.getFieldCount;
import static io.spine.protobuf.MessageField.getFieldDescriptor;
import static io.spine.protobuf.MessageField.getFieldName;
import static io.spine.protobuf.MessageField.toAccessorMethodName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("MessageField should")
class MessageFieldTest {

    private static final int STR_VALUE_FIELD_INDEX = 0;

    @SuppressWarnings("DuplicateStringLiteralInspection")
    private static final String STR_VALUE_FIELD_NAME = "value";

    private final StringValue stringValue = StringValue.of(newUuid());

    @Test
    @DisplayName("accept positive index")
    void accept_positive_index() {
        int index = 5;

        MessageField field = new TestMessageField(index);

        assertEquals(index, field.getIndex());
    }

    @Test
    @DisplayName("accept zero index")
    void accept_zero_index() {
        int index = 0;

        MessageField field = new TestMessageField(index);

        assertEquals(index, field.getIndex());
    }

    @Test
    @DisplayName("not accept negative index")
    void throw_exception_if_field_index_is_negative() {
        assertThrows(IllegalArgumentException.class,
                     () -> new TestMessageField(-5));
    }

    @Test
    @DisplayName("throw if field is not available")
    void throw_exception_if_field_is_not_available() {
        TestMessageField field = new TestMessageField(STR_VALUE_FIELD_INDEX);
        field.setIsFieldAvailable(false);
        assertThrows(MessageFieldException.class,
                     () -> field.getValue(stringValue));
    }

    @Test
    @DisplayName("throw if there is not field for index")
    void throw_exception_if_no_field_by_given_index() {
        TestMessageField field = new TestMessageField(Integer.MAX_VALUE);
        assertThrows(ArrayIndexOutOfBoundsException.class,
                     () -> field.getValue(stringValue));
    }

    @Test
    @DisplayName("return field value")
    void return_field_value() {
        TestMessageField field = new TestMessageField(STR_VALUE_FIELD_INDEX);

        Object value = field.getValue(stringValue);

        assertEquals(stringValue.getValue(), value);
    }

    @Test
    @DisplayName("return field descriptor")
    void return_field_descriptor() {
        FieldDescriptor descriptor = getFieldDescriptor(stringValue, STR_VALUE_FIELD_INDEX);

        assertEquals(JavaType.STRING, descriptor.getJavaType());
    }

    @Test
    @DisplayName("return field name")
    void return_field_name() {
        String fieldName = getFieldName(stringValue, STR_VALUE_FIELD_INDEX);

        assertEquals(STR_VALUE_FIELD_NAME, fieldName);
    }

    @Test
    @DisplayName("convert field name to method name")
    void convert_field_name_to_method_name() {
        assertEquals("getUserId", toAccessorMethodName("user_id"));
        assertEquals("getId", toAccessorMethodName("id"));
        assertEquals("getAggregateRootId", toAccessorMethodName("aggregate_root_id"));
    }

    @Test
    @DisplayName("obtain number of fields")
    void obtain_number_of_fields() {
        assertEquals(0, getFieldCount(Empty.getDefaultInstance()));
        assertEquals(1, getFieldCount(StringValue.getDefaultInstance()));
        assertEquals(2, getFieldCount(Timestamp.getDefaultInstance()));
    }

    private static class TestMessageField extends MessageField {

        private static final long serialVersionUID = 0L;
        private boolean isFieldAvailable = true;

        private TestMessageField(int index) {
            super(index);
        }

        @SuppressWarnings("SameParameterValue")
        private // Now is used only once to overwrite default value.
        void setIsFieldAvailable(boolean isFieldAvailable) {
            this.isFieldAvailable = isFieldAvailable;
        }

        @Override
        protected MessageFieldException createUnavailableFieldException(Message message) {
            return new MessageFieldException(message);
        }

        @Override
        protected boolean isFieldAvailable(Message message) {
            return isFieldAvailable;
        }
    }
}
