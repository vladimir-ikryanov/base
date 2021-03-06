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

package io.spine.tools.gradle.compiler;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import io.spine.tools.gradle.SpinePlugin;
import org.gradle.api.Project;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Preconditions.checkState;

/**
 * A Gradle plugin which imports the Spine {@code protoc} plugin into the project.
 *
 * <p>This plugin requires the {@code com.google.protobuf} to be applied to the project.
 */
public class ProtocPluginImporter extends SpinePlugin {

    private static final String PROTOC_CONFIG_FILE_NAME = "spine-protoc.gradle";

    private static final String PROTOBUF_PLUGIN_ID = "com.google.protobuf";

    @Override
    public void apply(Project project) {
        File configFile = generateSpineProtoc();
        project.getPluginManager()
               .withPlugin(PROTOBUF_PLUGIN_ID, appliedPlugin -> {
                   Logger log = log();
                   log.debug("Applying {} ({})",
                             PROTOC_CONFIG_FILE_NAME,
                             configFile.getAbsolutePath());
                   project.apply(ImmutableMap.of("from", configFile.getAbsolutePath()));
                   log.debug("Applied {}", PROTOC_CONFIG_FILE_NAME);
        });
    }

    /**
     * Generates the {@code spine-protoc.gradle} file in a temporary folder.
     *
     * <p>The file is copied from the module resources into a randomly-generated one-time
     * location.
     *
     * @return the generated {@link File}
     * @throws IllegalStateException upon an {@link IOException}
     */
    private static File generateSpineProtoc() throws IllegalStateException {
        File tempFolder = Files.createTempDir();
        File configFile = new File(tempFolder, PROTOC_CONFIG_FILE_NAME);
        try (InputStream in = ProtocPluginImporter.class
                .getClassLoader()
                .getResourceAsStream(PROTOC_CONFIG_FILE_NAME);
             FileOutputStream out = new FileOutputStream(configFile)
        ) {
            checkState(in != null, "Unable to get resource " + PROTOC_CONFIG_FILE_NAME);
            int readByte = in.read();
            while (readByte >= 0) {
                out.write(readByte);
                readByte = in.read();
            }
            return configFile;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
