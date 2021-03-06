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

package io.spine.tools.compiler.annotation;

import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.spine.code.java.ClassName;
import org.checkerframework.checker.regex.qual.Regex;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

/**
 * A source code annotation facade.
 */
public final class ModuleAnnotator {

    private final AnnotatorFactory annotatorFactory;
    private final ImmutableSet<Job> jobs;

    private ModuleAnnotator(Builder builder) {
        this.annotatorFactory = builder.annotatorFactory;
        this.jobs = ImmutableSet.copyOf(builder.jobs);
    }

    /**
     * Executes the {@linkplain Job annotation jobs}.
     */
    public void annotate() {
        jobs.forEach(job -> job.execute(annotatorFactory));
    }

    /**
     * Creates a new {@link JobBuilder}.
     *
     * <p>Start constructing a {@link Job} from this method.
     */
    public static JobBuilder translate(ApiOption option) {
        checkNotNull(option);
        return new JobBuilder(option);
    }

    /**
     * A job of the annotator.
     *
     * <p>Typically, represents a piece of routine source code annotation work to perform.
     */
    public interface Job {

        /**
         * Executes this job.
         *
         * @param factory
         *         a factory of {@link Annotator} instances to use to create annotators suitable for
         *         the job
         */
        void execute(AnnotatorFactory factory);
    }

    /**
     * An annotation {@link Job} which covers Java sources generated from Protobuf marked with
     * a certain {@link ApiOption}.
     */
    private static final class OptionJob implements Job {

        private final ApiOption protobufOption;
        private final ClassName javaAnnotation;

        private OptionJob(ApiOption protobufOption, ClassName javaAnnotation) {
            this.protobufOption = protobufOption;
            this.javaAnnotation = javaAnnotation;
        }

        @Override
        public void execute(AnnotatorFactory factory) {
            ClassName annotation = javaAnnotation;
            ApiOption option = protobufOption;
            factory.createFileAnnotator(annotation, option)
                   .annotate();
            factory.createMessageAnnotator(annotation, option)
                   .annotate();
            if (option.supportsServices()) {
                factory.createServiceAnnotator(annotation, option)
                       .annotate();
            }
            if (option.supportsFields()) {
                factory.createFieldAnnotator(annotation, option)
                       .annotate();
            }
        }
    }

    /**
     * An annotation {@link Job} which covers generated Java classes which have a certain naming.
     *
     * <p>For example, all classes ending with {@code OrBuilder}.
     */
    private static final class PatternJob implements Job {

        private final ClassNamePattern pattern;
        private final ClassName javaAnnotation;

        private PatternJob(ClassNamePattern pattern, ClassName annotation) {
            this.javaAnnotation = annotation;
            this.pattern = pattern;
        }

        @Override
        public void execute(AnnotatorFactory factory) {
            factory.createPatternAnnotator(javaAnnotation, pattern)
                   .annotate();
        }
    }

    /**
     * A builder of {@link Job} instances.
     *
     * <p>To receive an instance of the builder, call {@code ModuleAnnotator.translate(...)}.
     * The builder completes the {@code Job} construction DSL with the {@link #as(ClassName)}
     * method.
     */
    public static final class JobBuilder {

        private final ApiOption targetOption;

        private JobBuilder(ApiOption targetOption) {
            this.targetOption = targetOption;
        }

        /**
         * Builds an instance of {@code Job}.
         */
        public Job as(ClassName annotation) {
            checkNotNull(annotation);
            return new OptionJob(targetOption, annotation);
        }
    }

    /**
     * Creates a new instance of {@code Builder} for {@code ModuleAnnotator} instances.
     *
     * @return new instance of {@code Builder}
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * A builder for the {@code ModuleAnnotator} instances.
     */
    public static final class Builder {

        private final Set<Job> jobs;
        private AnnotatorFactory annotatorFactory;
        private ImmutableSet<@Regex String> internalPatterns;
        private ClassName internalAnnotation;

        /**
         * Prevents direct instantiation.
         */
        private Builder() {
            this.jobs = newHashSet();
        }

        public Builder setAnnotatorFactory(AnnotatorFactory annotatorFactory) {
            this.annotatorFactory = checkNotNull(annotatorFactory);
            this.internalPatterns = ImmutableSet.of();
            return this;
        }

        /**
         * Adds a {@link Job} to execute.
         *
         * @see #translate(ApiOption) the {@code Job} construction DSL
         */
        @CanIgnoreReturnValue
        public Builder add(Job job) {
            checkNotNull(job);
            this.jobs.add(job);
            return this;
        }

        /**
         * Adds patters for Java classes to be annotated as {@code internal}.
         *
         * <p>The patterns are {@linkplain java.util.regex.Pattern#compile(String) compiled} with
         * no additional flags.
         *
         * <p>All the classes, fully qualified canonical names of which match at least one of
         * the given patterns, should be marked as internal by the resulting annotator.
         *
         * @param patterns
         *         class name patterns
         * @see #setInternalAnnotation
         */
        public Builder setInternalPatterns(ImmutableSet<@Regex String> patterns) {
            checkNotNull(patterns);
            this.internalPatterns = patterns;
            return this;
        }

        /**
         * Specifies the {@code internal} annotation class name.
         *
         * <p>This annotation will be used to mark classes matching
         * {@linkplain #setInternalPatterns internal patterns}.
         *
         * @param internalAnnotation
         *         annotation class name
         */
        public Builder setInternalAnnotation(ClassName internalAnnotation) {
            this.internalAnnotation = internalAnnotation;
            return this;
        }

        /**
         * Creates a new instance of {@code ModuleAnnotator}.
         *
         * @return new instance of {@code ModuleAnnotator}
         */
        public ModuleAnnotator build() {
            checkNotNull(annotatorFactory);
            checkNotNull(internalAnnotation);
            internalPatterns.stream()
                            .map(ClassNamePattern::compile)
                            .map(pattern -> new PatternJob(pattern, internalAnnotation))
                            .forEach(this::add);
            return new ModuleAnnotator(this);
        }
    }
}
