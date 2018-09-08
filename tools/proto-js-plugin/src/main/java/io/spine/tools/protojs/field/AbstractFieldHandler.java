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

import com.google.protobuf.Descriptors.FieldDescriptor;
import io.spine.tools.protojs.code.JsWriter;
import io.spine.tools.protojs.field.checker.FieldValueChecker;
import io.spine.tools.protojs.field.parser.FieldValueParser;

import static io.spine.tools.protojs.message.MessageHandler.FROM_OBJECT_ARG;
import static java.lang.String.format;

abstract class AbstractFieldHandler implements FieldHandler {

    private final FieldDescriptor fieldDescriptor;
    private final FieldValueChecker fieldValueChecker;
    private final FieldValueParser fieldValueParser;
    private final JsWriter jsWriter;

    AbstractFieldHandler(FieldDescriptor fieldDescriptor,
                         FieldValueChecker fieldValueChecker,
                         FieldValueParser fieldValueParser,
                         JsWriter jsWriter) {
        this.fieldDescriptor = fieldDescriptor;
        this.fieldValueChecker = fieldValueChecker;
        this.fieldValueParser = fieldValueParser;
        this.jsWriter = jsWriter;
    }

    String acquireJsObject() {
        String fieldJsonName = fieldDescriptor.getJsonName();
        String jsObject = FROM_OBJECT_ARG + '.' + fieldJsonName;
        return jsObject;
    }

    void setValue(String value) {
        fieldValueChecker.performNullCheck(value, setterFormat());
        String fieldValue = "fieldValue";
        fieldValueParser.parseFieldValue(value, fieldValue);
        callSetter(fieldValue);
        fieldValueChecker.exitNullCheck();
    }

    FieldDescriptor fieldDescriptor() {
        return fieldDescriptor;
    }

    JsWriter jsWriter() {
        return jsWriter;
    }

    private void callSetter(String value) {
        String addToMapFormat = setterFormat();
        String addToMap = format(addToMapFormat, value);
        jsWriter.addLine(addToMap);
    }

    abstract String setterFormat();
}
