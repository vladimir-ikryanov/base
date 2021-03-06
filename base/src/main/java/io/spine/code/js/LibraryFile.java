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

package io.spine.code.js;

/**
 * The enumeration of project files provided by the Spine framework.
 */
public enum LibraryFile {

    /**
     * The file declaring Spine Options.
     */
    SPINE_OPTIONS("spine/options_pb.js"),

    /**
     * The file containing map of all known types with their {@linkplain io.spine.type.TypeUrl URL}.
     */
    KNOWN_TYPES("known_types.js"),

    /**
     * The file containing the number of predefined JSON parsers for various types.
     */
    KNOWN_TYPE_PARSERS("known_type_parsers.js");

    private final FileName fileName;

    LibraryFile(String fileName) {
        this.fileName = FileName.of(fileName);
    }

    public FileName fileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return fileName.value();
    }
}
