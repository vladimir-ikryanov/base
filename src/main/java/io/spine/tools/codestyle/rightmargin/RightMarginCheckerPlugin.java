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
package io.spine.tools.codestyle.rightmargin;

import io.spine.tools.codestyle.FileChecker;
import io.spine.tools.codestyle.StepConfiguration;
import io.spine.tools.gradle.SpinePlugin;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;

import static io.spine.tools.codestyle.CodeStyleCheckerPlugin.createStepExtension;
import static io.spine.tools.gradle.TaskName.CHECK_RIGHT_MARGIN_WRAPPING;
import static io.spine.tools.gradle.TaskName.COMPILE_JAVA;
import static io.spine.tools.gradle.TaskName.PROCESS_RESOURCES;

/**
 * The plugin that checks the target project java files for the line that are longer than
 * allowed threshold.
 *
 * @author Alexander Aleksandrov
 */
public class RightMarginCheckerPlugin extends SpinePlugin {

    private static final String EXTENSION_NAME = "rightMarginWrappingChecker";

    @Override
    public void apply(Project project) {
        final StepConfiguration configuration =
                createStepExtension(EXTENSION_NAME, project);
        final FileChecker checker = new FileChecker(new RightMarginCheck(configuration));
        final Action<Task> action = checker.actionFor(project);
        newTask(CHECK_RIGHT_MARGIN_WRAPPING, action)
                .insertAfterTask(COMPILE_JAVA)
                .insertBeforeTask(PROCESS_RESOURCES)
                .applyNowTo(project);
        log().debug("Starting to validate right margin wrapping {}", action);
    }
}
