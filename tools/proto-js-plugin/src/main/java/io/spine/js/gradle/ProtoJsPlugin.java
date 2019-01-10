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

package io.spine.js.gradle;

import com.google.common.collect.ImmutableList;
import io.spine.code.js.DefaultJsProject;
import io.spine.code.js.Directory;
import io.spine.code.proto.FileSet;
import io.spine.js.generate.AppendTypeUrlGetter;
import io.spine.js.generate.GenerationTask;
import io.spine.js.generate.index.GenerateIndexFile;
import io.spine.js.generate.parse.GenerateKnownTypeParsers;
import io.spine.tools.gradle.GradleTask;
import io.spine.tools.gradle.SpinePlugin;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;

import java.io.File;
import java.util.List;

import static io.spine.code.proto.FileSet.parseOrEmpty;
import static io.spine.tools.gradle.TaskName.BUILD;
import static io.spine.tools.gradle.TaskName.GENERATE_JSON_PARSERS;

/**
 * The Gradle plugin which performs additional code generation for Protobuf types.
 *
 * <p>To run the plugin, add the {@code "io.spine.tools:spine-proto-js-plugin:$spineBaseVersion"}
 * to the {@code classpath} configuration and add
 * {@code apply plugin: 'io.spine.tools.proto-js-plugin'} to the module generating JS messages.
 *
 * <p>In particular, the plugin:
 * <ul>
 *     <li>Generates a {@linkplain AppendTypeUrlGetter getter}
 *         to obtain a {@code TypeUrl} for each type.
 *     <li>Generates {@linkplain GenerateKnownTypeParsers parsers} for types
 *         with standard JSON mapping.
 *     <li>{@linkplain GenerateIndexFile Exposes} all the messages and enums
 *         as well as generated parsers (to be used by the Spine Web).
 * </ul>
 *
 * <p>The main plugin action may be retrieved and configured as necessary via the
 * {@linkplain Extension "protoJs" extension}. By default, the action is a dependency of the
 * {@linkplain io.spine.tools.gradle.TaskName#BUILD build} task.
 *
 * <p>This plugin currently relies on the set of the hard-coded Gradle settings which have to be
 * set to the required values in a project willing to use the plugin. These settings are:
 * <ol>
 *     <li>The root of the JS code generated by Protobuf JS compiler set to
 *         {@code ${projectDir}/proto/${sourceSet}/js"};
 *
 *     <li>descriptor set file stored under the
 *         {@code "${projectDir}/build/descriptors/${task.sourceSet.name}/known_types.desc"};
 *
 *     <li>CommonJS import style for all generated code:
 *         {@code js {option "import_style=commonjs"}};
 * </ol>
 *
 * <p>Also, the generated code depends on the {@code spine-web-client} module published
 * by the Spine Web. So, the users of the generated code should set up the dependency.
 *
 * <p>In general, it is how the <a href="https://github.com/SpineEventEngine/web">Spine Web</a>
 * builds its Protobuf definitions to JS and the plugin relies on this behaviour.
 *
 * <p>The {@code build.gradle} file located under the {@code test/resources} folder of this module
 * can be used as an example of the required project configuration.
 */
public class ProtoJsPlugin extends SpinePlugin {

    private static final String EXTENSION_NAME = "protoJs";

    @Override
    public void apply(Project project) {
        Extension extension = project.getExtensions()
                                     .create(EXTENSION_NAME, Extension.class);

        Action<Task> action = newAction(project);
        GradleTask newTask = newTask(GENERATE_JSON_PARSERS, action)
                .insertBeforeTask(BUILD)
                .applyNowTo(project);

        Task task = newTask.getTask();
        extension.setGenerateParsersTask(task);
    }

    /**
     * Creates an {@code Action} to perform the additional generation of code
     * for working with Protobuf types.
     *
     * <p>The action handles both main and test scopes.
     *
     * <p>The paths to the generated JS messages location, as well as to the descriptor set file,
     * are currently hard-coded.
     *
     * <p>See {@link DefaultJsProject} for the expected configuration.
     */
    private static Action<Task> newAction(Project project) {
        return task -> generateJsonParsers(project);
    }

    private static void generateJsonParsers(Project project) {
        generateForMain(project);
        generateForTest(project);
    }

    private static void generateForMain(Project project) {
        DefaultJsProject jsProject = DefaultJsProject.at(project.getProjectDir());
        Directory generatedRoot = jsProject.proto()
                                           .mainJs();
        File descriptors = jsProject.mainDescriptors();
        generateCode(generatedRoot, descriptors);
    }

    private static void generateForTest(Project project) {
        DefaultJsProject jsProject = DefaultJsProject.at(project.getProjectDir());
        Directory generatedRoot = jsProject.proto()
                                           .testJs();
        File descriptors = jsProject.testDescriptors();
        generateCode(generatedRoot, descriptors);
    }

    private static void generateCode(Directory generatedRoot, File descriptors) {
        List<GenerationTask> tasks = ImmutableList.of(
                GenerateKnownTypeParsers.createFor(generatedRoot),
                new AppendTypeUrlGetter(generatedRoot),
                new GenerateIndexFile(generatedRoot)
        );
        FileSet fileSet = parseOrEmpty(descriptors);
        for (GenerationTask task : tasks) {
            task.performFor(fileSet);
        }
    }
}
