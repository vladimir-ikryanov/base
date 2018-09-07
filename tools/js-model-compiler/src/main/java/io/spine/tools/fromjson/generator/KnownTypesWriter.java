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

package io.spine.tools.fromjson.generator;

import io.spine.code.proto.FileSet;
import io.spine.tools.fromjson.js.JsOutput;
import io.spine.tools.fromjson.js.JsWriter;
import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class KnownTypesWriter {

    // todo create common class with file names
    private static final String FILE_NAME = "known_types";
    static final String JS_FILE_NAME = FILE_NAME + ".js";

    private final Path filePath;
    private final FileSet protoJsFiles;

    private KnownTypesWriter(Path filePath, FileSet protoJsFiles) {
        this.filePath = filePath;
        this.protoJsFiles = protoJsFiles;
    }

    public static KnownTypesWriter createFor(Project project, FileSet protoJsFiles) {
        Path path = composeFilePath(project);
        return new KnownTypesWriter(path, protoJsFiles);
    }

    public void writeFile() {
        int indent = 4;
        JsWriter jsWriter = new JsWriter(indent);
        KnownTypesGenerator generator = new KnownTypesGenerator(protoJsFiles, jsWriter);
        generator.generateKnownTypes();
        JsOutput generatedCode = jsWriter.getGeneratedCode();
        writeToFile(filePath, generatedCode);
    }

    private static Path composeFilePath(Project project) {
        File projectDir = project.getProjectDir();
        String absolutePath = projectDir.getAbsolutePath();
        Path path = Paths.get(absolutePath, "proto", "test", "js", JS_FILE_NAME);
        return path;
    }

    // todo remove copypaste
    private static void writeToFile(Path path, JsOutput output) {
        try {
            String content = output.toString();
            Files.write(path, content.getBytes(), CREATE, TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
