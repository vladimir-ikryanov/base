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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The precondition for the proto fields of primitive types.
 *
 * <p>All the proto fields which are not of the {@code message} type are handled by this
 * precondition. This includes the {@code enum} type fields which obey the same rules as primitives
 * in this case.
 */
final class PrimitivePrecondition implements FieldPrecondition {

    private final CodeLines jsOutput;

    /**
     * Creates a new {@code PrimitivePrecondition}.
     *
     * @param jsOutput
     *         the {@code JsOutput} which accumulates all the generated code
     */
    PrimitivePrecondition(CodeLines jsOutput) {
        this.jsOutput = jsOutput;
    }

    /**
     * {@inheritDoc}
     *
     * <p>In case of the primitive field, the {@code null} values are simply not allowed.
     */
    @Override
    public void performNullCheck(String value, String mergeFieldFormat) {
        checkNotNull(value);
        checkNotNull(mergeFieldFormat);
        jsOutput.ifNotNull(value);
    }

    @Override
    public void exitNullCheck() {
        jsOutput.exitBlock();
    }
}
