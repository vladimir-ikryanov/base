/*
 * Copyright 2017, TeamDev Ltd. All rights reserved.
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

package io.spine.validate;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Any;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Int32Value;
import io.spine.protobuf.AnyPacker;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Alexander Litus
 */
public class IntegerFieldValidatorShould {

    private static final Integer VALUE = 2;
    private static final Integer NEGATIVE_VALUE = -2;

    private final FieldDescriptor fieldDescriptor = Any.getDescriptor().getFields().get(0);
    private final IntegerFieldValidator validator =
            new IntegerFieldValidator(FieldContext.create(fieldDescriptor),
                                      ImmutableList.of(VALUE));

    @Test
    public void convert_string_to_number() {
        assertEquals(VALUE, validator.toNumber(VALUE.toString()));
    }

    @Test
    public void return_absolute_number_value() {
        assertEquals(VALUE, validator.getAbs(NEGATIVE_VALUE));
    }

    @Test
    public void wrap_to_any() {
        final Any any = validator.wrap(VALUE);
        final Int32Value msg = AnyPacker.unpack(any);
        assertEquals(VALUE, (Integer) msg.getValue());
    }
}
