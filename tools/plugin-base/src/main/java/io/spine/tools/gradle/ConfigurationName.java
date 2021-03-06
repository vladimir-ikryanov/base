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

package io.spine.tools.gradle;

import com.google.common.base.MoreObjects;

/**
 * The names of Gradle configurations used by the Spine model compiler plugin.
 *
 * <p>See <a href="https://docs.gradle.org/current/userguide/managing_dependency_configurations.html">
 * the Gradle doc</a> on dependency configurations for more info.
 *
 * @author Dmytro Dashenkov
 */
public enum ConfigurationName {

    /**
     * The {@code classpath} configuration.
     */
    CLASSPATH("classpath"),

    /**
     * The {@code runtime} configuration.
     */
    RUNTIME("runtime"),

    /**
     * The {@code testRuntime} configuration.
     */
    TEST_RUNTIME("testRuntime");

    private final String value;

    ConfigurationName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @SuppressWarnings("DuplicateStringLiteralInspection")
        // `value` is used in other contexts.
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("value", value)
                          .toString();
    }
}
