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

package io.spine.js.generate.parse;

import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.Descriptors.FileDescriptor;
import io.spine.code.js.Directory;
import io.spine.code.proto.FileSet;
import io.spine.js.generate.GenerationTask;
import io.spine.js.generate.Snippet;
import io.spine.js.generate.output.FileWriter;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.code.js.LibraryFile.KNOWN_TYPES;
import static io.spine.code.proto.ProtoPackage.GOOGLE_PROTOBUF_PACKAGE;

/**
 * This class writes the JavaScript code necessary to parse messages generated by Protobuf JS
 * compiler from JSON.
 *
 * <p>More specifically, the class:
 * <ol>
 *     <li>Writes all known types to the {@code known_types.js} file in generated JS code root. The
 *         types are stored in a global {@code Map} in the
 *         "{@linkplain io.spine.type.TypeUrl type-url}-to-JS-type" format.
 *     <li>Appends {@code fromJson(json)} method to all files generated from Protobuf, one for each
 *         message stored in a file.
 * </ol>
 */
public final class GenerateKnownTypeParsers extends GenerationTask {

    private GenerateKnownTypeParsers(Directory generatedRoot) {
        super(generatedRoot);
    }

    public static GenerateKnownTypeParsers createFor(Directory generatedRoot) {
        checkNotNull(generatedRoot);
        return new GenerateKnownTypeParsers(generatedRoot);
    }

    /**
     * Generates and writes the JS code necessary to parse proto messages from the JSON format.
     */
    @Override
    protected void generateFor(FileSet fileSet) {
        writeKnownTypes(fileSet);
        writeParseMethods(fileSet);
    }

    /**
     * Generates global JS known types map and records it to the {@code known_types.js} file.
     *
     * <p>The types in map are stored in the
     * "{@linkplain io.spine.type.TypeUrl type-url}-to-JS-type" format.
     *
     * <p>The file is written to the root of the generated messages location.
     */
    @VisibleForTesting
    void writeKnownTypes(FileSet fileSet) {
        KnownTypesMap generator = new KnownTypesMap(fileSet);
        FileWriter writer = FileWriter.createFor(generatedRoot(), KNOWN_TYPES);
        writer.write(generator.value());
    }

    /**
     * Appends the {@code fromJson(json)} methods for all known types in the corresponding files.
     *
     * <p>The standard Protobuf types are skipped.
     */
    @VisibleForTesting
    void writeParseMethods(FileSet fileSet) {
        for (FileDescriptor file : fileSet.files()) {
            writeParseMethods(file);
        }
    }

    private void writeParseMethods(FileDescriptor file) {
        if (shouldSkip(file)) {
            return;
        }
        Snippet parseMethodsSnippet = new ParseMethodsSnippet(file);
        FileWriter writer = FileWriter.createFor(generatedRoot(), file);
        writer.append(parseMethodsSnippet.value());
    }

    /**
     * Checks if the writer should skip generating JSON-parsing code for messages in a file.
     */
    @VisibleForTesting
    static boolean shouldSkip(FileDescriptor file) {
        boolean isStandardType = file.getPackage()
                                     .startsWith(GOOGLE_PROTOBUF_PACKAGE.packageName());
        return isStandardType;
    }
}
