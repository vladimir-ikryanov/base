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

package io.spine.tools.compiler.field;

import com.google.common.base.Objects;
import com.google.errorprone.annotations.Immutable;
import io.spine.annotation.Internal;
import io.spine.code.java.FieldName;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A template of an accessor method generated by the Protobuf compiler for a Protobuf field in Java.
 *
 * <p>An accessor always has a prefix (e.g. {@code get...}) and may have a postfix
 * (e.g. {@code ...Count}).
 */
@Immutable
@Internal
public final class AccessorTemplate implements Serializable {

    private static final long serialVersionUID = 0L;

    private static final String RAW_INFIX = "Raw";

    private final String prefix;
    private final String postfix;

    private AccessorTemplate(String prefix, String postfix) {
        this.prefix = prefix;
        this.postfix = postfix;
    }

    /**
     * Creates a new template with the given prefix and an empty postfix.
     */
    public static AccessorTemplate prefix(String prefix) {
        checkNotNull(prefix);
        return new AccessorTemplate(prefix, "");
    }

    /**
     * Creates a new template with the given prefix and postfix.
     */
    public static AccessorTemplate prefixAndPostfix(String prefix, String suffix) {
        checkNotNull(prefix);
        checkNotNull(suffix);
        return new AccessorTemplate(prefix, suffix);
    }

    /**
     * Formats an accessor method name based on this template and the given field name.
     *
     * @param field
     *         the name of the field to access
     * @return the method name
     */
    public String format(FieldName field) {
        String name = String.format(template(), field.capitalize());
        return name;
    }

    private String template() {
        return prefix + "%s" + postfix;
    }

    /**
     * Obtains the same template but with the {@code Raw} infix right after the prefix.
     *
     * <p>The original template is not changed.
     *
     * <p>This operation is NOT idempotent, i.e. when calling {@code toRaw()} on a result of
     * {@code toRaw()}, the infix is added once more.
     *
     * @return template with the {@code Raw} infix
     */
    public AccessorTemplate toRaw() {
        String prefix = this.prefix + RAW_INFIX;
        return new AccessorTemplate(prefix, postfix);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AccessorTemplate template = (AccessorTemplate) o;
        return Objects.equal(template(), template.template());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(prefix, postfix);
    }

    @Override
    public String toString() {
        return template();
    }
}
