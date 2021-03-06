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

package io.spine.js.generate.parse;

import com.google.common.testing.NullPointerTester;
import com.google.protobuf.Timestamp;
import io.spine.js.generate.output.CodeLines;
import io.spine.type.TypeUrl;
import io.spine.validate.ValidationError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.js.generate.given.Generators.assertContains;
import static io.spine.testing.DisplayNames.NOT_ACCEPT_NULLS;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ExportStandardParsers should")
class ExportStandardParsersTest {

    private final ExportStandardParsers generator = new ExportStandardParsers();

    @Test
    @DisplayName(NOT_ACCEPT_NULLS)
    void passNullToleranceCheck() {
        new NullPointerTester().testAllPublicStaticMethods(ExportStandardParsers.class);
    }

    @Test
    @DisplayName("tell if parser for type URL is present")
    void tellIfHasParser() {
        TypeUrl timestamp = TypeUrl.of(Timestamp.class);
        assertTrue(ExportStandardParsers.hasParser(timestamp));

        TypeUrl validationError = TypeUrl.of(ValidationError.class);
        assertFalse(ExportStandardParsers.hasParser(validationError));
    }

    @Test
    @DisplayName("generate known type parsers map")
    void generateParsersMap() {
        CodeLines snippet = generator.value();
        String mapEntry = "['type.googleapis.com/google.protobuf.Value', new ValueParser()]";
        assertContains(snippet, mapEntry);
    }
}
