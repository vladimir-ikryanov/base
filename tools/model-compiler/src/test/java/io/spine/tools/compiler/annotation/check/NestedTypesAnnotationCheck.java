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

package io.spine.tools.compiler.annotation.check;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jboss.forge.roaster.model.impl.AbstractJavaSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.tools.compiler.annotation.check.Annotations.findInternalAnnotation;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NestedTypesAnnotationCheck implements SourceCheck {

    private final boolean shouldBeAnnotated;

    public NestedTypesAnnotationCheck(boolean shouldBeAnnotated) {
        this.shouldBeAnnotated = shouldBeAnnotated;
    }

    @Override
    public void accept(@Nullable AbstractJavaSource<JavaClassSource> outerClass) {
        checkNotNull(outerClass);
        for (JavaSource<?> nestedType : outerClass.getNestedTypes()) {
            Optional<?> annotation = findInternalAnnotation(nestedType);
            if (shouldBeAnnotated) {
                assertTrue(annotation.isPresent(),
                           format("%s is not annotated.", nestedType.getQualifiedName()));
            } else {
                assertFalse(annotation.isPresent(),
                            format("%s is annotated.", nestedType.getQualifiedName()));
            }
        }
    }
}
