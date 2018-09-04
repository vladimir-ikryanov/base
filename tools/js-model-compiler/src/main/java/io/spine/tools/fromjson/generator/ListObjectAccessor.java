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

import io.spine.tools.fromjson.js.JsWriter;

final class ListObjectAccessor implements JsObjectAccessor {

    private static final String LIST_ITEM_VAR = "listItem";

    private final JsWriter jsWriter;

    ListObjectAccessor(JsWriter jsWriter) {
        this.jsWriter = jsWriter;
    }

    @Override
    public String extractOrIterateValue(String jsObject) {
        jsWriter.enterIfBlock(jsObject + " !== undefined && " + jsObject + " !== null");
        jsWriter.addLine(jsObject + ".forEach(");
        jsWriter.increaseDepth();
        jsWriter.enterBlock('(' + LIST_ITEM_VAR + ", index, array) =>");
        return LIST_ITEM_VAR;
    }

    @Override
    public void exitToTopLevel() {
        jsWriter.exitBlock();
        jsWriter.decreaseDepth();
        jsWriter.addLine(");");
        jsWriter.exitBlock();
    }
}
