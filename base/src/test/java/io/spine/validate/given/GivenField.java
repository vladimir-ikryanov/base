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

package io.spine.validate.given;

import com.google.protobuf.Descriptors.FieldDescriptor;
import io.spine.test.validate.field.Message;
import io.spine.validate.FieldContext;

public class GivenField {

    /** Prevents instantiation of this utility class. */
    private GivenField() {
    }

    public static FieldContext mapContext() {
        FieldDescriptor mapField = Message.getDescriptor()
                                          .findFieldByName("map");
        return FieldContext.create(mapField);
    }

    public static FieldContext repeatedContext() {
        FieldDescriptor repeatedField = Message.getDescriptor()
                                               .findFieldByName("repeated");
        return FieldContext.create(repeatedField);
    }

    public static FieldContext scalarContext() {
        FieldDescriptor scalarField = Message.getDescriptor()
                                             .findFieldByName("scalar");
        return FieldContext.create(scalarField);
    }
}
