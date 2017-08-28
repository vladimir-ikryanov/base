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

package io.spine.gradle.compiler.model;

import com.google.common.base.Function;
import io.spine.gradle.ProjectHierarchy;
import io.spine.server.aggregate.Aggregate;
import io.spine.server.command.CommandHandler;
import io.spine.server.model.Model;
import io.spine.server.procman.ProcessManager;
import io.spine.tools.model.SpineModel;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.tasks.compile.JavaCompile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Lists.newLinkedList;
import static io.spine.util.Exceptions.newIllegalArgumentException;
import static java.lang.String.format;
import static java.util.Arrays.deepToString;

/**
 * The factory for the {@link ProcessingStage} instances.
 *
 * @author Dmytro Dashenkov
 */
final class ProcessingStages {

    private static final Model model = Model.getInstance();

    private ProcessingStages() {
        // Prevent utility class instantiation.
    }

    /**
     * Retrieves the {@link ProcessingStage} which validates the given {@link SpineModel}.
     *
     * <p>The validation includes the command handler checks.
     *
     * @param project the Gradle project to get the compiled sources from
     */
    static ProcessingStage validate(Project project) {
        return new ValidatingProcessingStage(project);
    }

    /**
     * The validating {@link ProcessingStage}.
     *
     * @see ProcessingStages#validate(Project)
     */
    private static class ValidatingProcessingStage implements ProcessingStage {

        private static final URL[] EMPTY_URL_ARRAY = new URL[0];

        private final URLClassLoader projectClassLoader;

        private ValidatingProcessingStage(Project project) {
            this.projectClassLoader = createClassLoaderForProject(project);
        }

        @SuppressWarnings("IfStatementWithTooManyBranches") // OK in this case.
        @Override
        public void process(SpineModel rawModel) {
            for (String commandHandlingClass : rawModel.getCommandHandlingTypesList()) {
                final Class<?> cls;
                try {
                    log().debug("Trying to load class \'{}\'", commandHandlingClass);
                    cls = getModelClass(commandHandlingClass);
                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException(e);
                }
                if (Aggregate.class.isAssignableFrom(cls)) {
                    @SuppressWarnings("unchecked") final Class<? extends Aggregate> aggregateClass =
                            (Class<? extends Aggregate>) cls;
                    model.asAggregateClass(aggregateClass);
                    log().debug("\'{}\' classified as Aggregate type.");
                } else if (ProcessManager.class.isAssignableFrom(cls)) {
                    @SuppressWarnings("unchecked") final Class<? extends ProcessManager> aggregateClass =
                            (Class<? extends ProcessManager>) cls;
                    model.asProcessManagerClass(aggregateClass);
                    log().debug("\'{}\' classified as ProcessManages type.");
                } else if (CommandHandler.class.isAssignableFrom(cls)) {
                    @SuppressWarnings("unchecked") final Class<? extends CommandHandler> aggregateClass =
                            (Class<? extends CommandHandler>) cls;
                    model.asCommandHandlerClass(aggregateClass);
                    log().debug("\'{}\' classified as CommandHandler type.");
                } else {
                    throw newIllegalArgumentException("Class %s is not a command handling type.",
                                                      cls.getName());
                }
            }
        }

        private Class<?> getModelClass(String fqn) throws ClassNotFoundException {
            return Class.forName(fqn, false, projectClassLoader);
        }

        private static URLClassLoader createClassLoaderForProject(Project project) {
            final Collection<JavaCompile> tasks = allJavaCompile(project);
            final URL[] compiledCodePath = extractDestinationDirs(tasks);
            log().debug("Initializing ClassLoader for URLs: {}", deepToString(compiledCodePath));
            try {
                @SuppressWarnings("ClassLoaderInstantiation") // Caught exception.
                final URLClassLoader result =
                        new URLClassLoader(compiledCodePath,
                                           ValidatingProcessingStage.class.getClassLoader());
                return result;
            } catch (SecurityException e) {
                throw new IllegalStateException("Cannot analyze project source code.", e);
            }
        }

        private static Collection<JavaCompile> allJavaCompile(Project project) {
            final Collection<JavaCompile> tasks = newLinkedList();
            ProjectHierarchy.applyToAll(project.getRootProject(), new Action<Project>() {
                @Override
                public void execute(Project project) {
                    tasks.addAll(javaCompile(project));
                }
            });
            return tasks;
        }

        private static Collection<JavaCompile> javaCompile(Project project) {
            return project.getTasks().withType(JavaCompile.class);
        }

        private static URL[] extractDestinationDirs(Collection<JavaCompile> tasks) {
            final Collection<URL> urls = transform(tasks, GetDestinationDir.FUNCTION);
            final URL[] result = urls.toArray(EMPTY_URL_ARRAY);
            return result;
        }
    }

    /**
     * A function which retrieves the output directory from the passed Gradle task.
     */
    private enum GetDestinationDir implements Function<JavaCompile, URL> {
        FUNCTION;

        @Nullable
        @Override
        public URL apply(@Nullable JavaCompile task) {
            checkNotNull(task);
            final File destDir = task.getDestinationDir();
            if (destDir == null) {
                return null;
            }
            final URI destUri = destDir.toURI();
            try {
                final URL destUrl = destUri.toURL();
                return destUrl;
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(format(
                        "Could not retrieve destination directory for task `%s`.",
                        task.getName()), e);
            }
        }
    }

    private static Logger log() {
        return LogSingleton.INSTANCE.value;
    }

    private enum LogSingleton {
        INSTANCE;
        @SuppressWarnings("NonSerializableFieldInSerializableClass")
        private final Logger value = LoggerFactory.getLogger(ProcessingStages.class);
    }
}
