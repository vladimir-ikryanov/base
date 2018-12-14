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

package io.spine.js.generate.typeurl;

import com.google.protobuf.Descriptors.FileDescriptor;
import io.spine.code.js.Directory;
import io.spine.code.proto.Type;
import io.spine.code.proto.TypeSet;
import io.spine.js.generate.JsCodeGenerator;
import io.spine.js.generate.JsFile;
import io.spine.js.generate.JsOutput;

/**
 * For each type in a file generates a method to obtain the type URL.
 */
final class TypeUrlsInFile extends JsCodeGenerator {

    private final FileDescriptor file;
    private final Directory generatedRoot;

    TypeUrlsInFile(FileDescriptor file, Directory generatedRoot) {
        this(new JsOutput(), file, generatedRoot);
    }

    TypeUrlsInFile(JsOutput output, FileDescriptor file, Directory generatedRoot) {
        super(output);
        this.file = file;
        this.generatedRoot = generatedRoot;
    }

    @Override
    public void generate() {
        TypeSet types = TypeSet.messagesAndEnums(file);
        for (Type<?, ?> type : types.types()) {
            TypeUrlMethod method = new TypeUrlMethod(type, jsOutput());
            jsOutput().addEmptyLine();
            method.generate();
        }
    }

    /**
     * Generates the code and appends it to the file.
     */
    void generateAndAppend() {
        generate();
        JsFile jsFile = JsFile.createFor(generatedRoot, file);
        jsFile.append(jsOutput());
    }
}
