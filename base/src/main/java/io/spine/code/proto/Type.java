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

package io.spine.code.proto;

import com.google.common.base.Objects;
import com.google.errorprone.annotations.Immutable;
import com.google.protobuf.Descriptors.GenericDescriptor;
import com.google.protobuf.Message;
import io.spine.annotation.Internal;
import io.spine.code.java.ClassName;
import io.spine.code.java.PackageName;
import io.spine.code.java.SimpleClassName;
import io.spine.type.TypeName;
import io.spine.type.TypeUrl;
import io.spine.type.UnknownTypeException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A Protobuf type, such as a message or an enum.
 *
 * @param <T> the type of the type descriptor
 * @param <P> the type of the proto message of the descriptor
 */
@Immutable(containerOf = {"T", "P"})
@Internal
public abstract class Type<T extends GenericDescriptor, P extends Message> {

    private final T descriptor;
    private final boolean supportsBuilders;

    protected Type(T descriptor, boolean supportsBuilders) {
        this.descriptor = checkNotNull(descriptor);
        this.supportsBuilders = supportsBuilders;
    }

    /**
     * Obtains the descriptor of the type.
     */
    public T descriptor() {
        return this.descriptor;
    }

    /**
     * Obtains the proto message of the type descriptor.
     */
    public abstract P toProto();

    /**
     * Obtains the {@linkplain TypeName name} of this type.
     */
    public TypeName name() {
        return url().toName();
    }

    /**
     * Obtains the {@link TypeUrl} of this type.
     */
    public abstract TypeUrl url();

    /**
     * Loads the Java class representing this Protobuf type.
     */
    public Class<?> javaClass() {
        String clsName = javaClassName().value();
        try {
            return Class.forName(clsName);
        } catch (ClassNotFoundException e) {
            throw new UnknownTypeException(descriptor.getName(), e);
        }
    }

    /**
     * Obtains the name of the Java class representing this Protobuf type.
     */
    public abstract ClassName javaClassName();

    /**
     * Obtains package for the corresponding Java type.
     */
    public PackageName javaPackage() {
        PackageName result = PackageName.resolve(descriptor.getFile()
                                                           .toProto());
        return result;
    }

    /**
     * Obtains simple class name for corresponding Java type.
     */
    public abstract SimpleClassName simpleJavaClassName();

    /**
     * Defines whether or not the Java class generated from this type has a builder.
     *
     * @return {@code true} if the generated Java class has corresponding
     *         {@link com.google.protobuf.Message.Builder} and
     *         {@link com.google.protobuf.MessageOrBuilder}
     */
    public final boolean supportsBuilders() {
        return supportsBuilders;
    }

    /**
     * Returns a fully-qualified name of the proto type.
     */
    @Override
    public String toString() {
        return descriptor.getFullName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Type)) {
            return false;
        }
        Type<?, ?> type = (Type<?, ?>) o;
        return Objects.equal(descriptor, type.descriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(descriptor);
    }
}
