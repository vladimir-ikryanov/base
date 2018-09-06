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

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import io.spine.tools.fromjson.js.JsWriter;

class FromJsonGenerator {

    private final FileDescriptor fileDescriptor;
    private final JsWriter jsWriter;

    FromJsonGenerator(FileDescriptor fileDescriptor, JsWriter jsWriter) {
        this.fileDescriptor = fileDescriptor;
        this.jsWriter = jsWriter;
    }

    void generateJs() {
        generateComment();
        generateImports();
        for (Descriptor messageDescriptor : fileDescriptor.getMessageTypes()) {
            MessageHandler handler = new MessageHandler(messageDescriptor, jsWriter);
            handler.generateJs();
        }
    }

    private void generateComment() {
        // todo generate some comment explaining generated code
    }

    private void generateImports() {
        jsWriter.addEmptyLine();
        String fileName = fileDescriptor.getFullName();
        jsWriter.addImportStatements(fileName, "known_types.js", "known_type_parsers.js");
    }
}
