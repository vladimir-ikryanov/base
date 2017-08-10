/*
 * Copyright 2017, TeamDev Ltd. All rights reserved.
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
package io.spine.tools.protodoc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A formatting action, which handles {@code <pre>} tags.
 *
 * <p>The action will remove all occurrences of the {@linkplain #PATTERN_PRE_TAG tags}.
 *
 * @author Dmytro Grankin
 */
class PreTagFormatting implements FormattingAction {

    /**
     * A pattern to match a {@code <pre>} or {@code </pre>} tag.
     */
    private static final Pattern PATTERN_PRE_TAG = Pattern.compile("<pre>|<\\/pre>");

    /**
     * Obtains the formatted representation of the specified text.
     *
     * @param text the text to format
     * @return the text without {@code <pre>} tags
     */
    @Override
    public String execute(String text) {
        final Matcher matcher = PATTERN_PRE_TAG.matcher(text);
        final String textWithoutTags = matcher.replaceAll("");
        return textWithoutTags;
    }
}
