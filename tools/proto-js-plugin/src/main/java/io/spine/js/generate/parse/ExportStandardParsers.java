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

package io.spine.js.generate.parse;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.protobuf.Any;
import com.google.protobuf.BoolValue;
import com.google.protobuf.BytesValue;
import com.google.protobuf.DoubleValue;
import com.google.protobuf.Duration;
import com.google.protobuf.Empty;
import com.google.protobuf.FieldMask;
import com.google.protobuf.FloatValue;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Int32Value;
import com.google.protobuf.Int64Value;
import com.google.protobuf.ListValue;
import com.google.protobuf.StringValue;
import com.google.protobuf.Timestamp;
import com.google.protobuf.UInt32Value;
import com.google.protobuf.UInt64Value;
import com.google.protobuf.Value;
import io.spine.js.generate.Snippet;
import io.spine.js.generate.output.CodeLines;
import io.spine.js.generate.output.snippet.MapExportSnippet;
import io.spine.type.TypeUrl;

import java.util.List;
import java.util.Map.Entry;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static io.spine.js.generate.output.CodeLine.emptyLine;
import static java.util.stream.Collectors.toList;

/**
 * The generator which stores JSON parsers for the standard Protobuf types to the JS {@code Map}.
 *
 * <p>Parsers are stored in the map in the "{@linkplain TypeUrl type-url}-to-parser" format.
 *
 * <p>The parsers may be used to parse JSON via their {@code parse(value)} method.
 */
@SuppressWarnings("OverlyCoupledClass") // Dependencies for the listed Protobuf types.
public final class ExportStandardParsers implements Snippet {

    /**
     * The exported map name.
     *
     * <p>Has {@code public} visibility so other generators can use the map in their code.
     */
    public static final String MAP_NAME = "parsers";

    /**
     * The hard-coded map of known proto parsers.
     *
     * <p>The map entry's value represents the JS type name of the parser.
     *
     * <p>Before adding the new entry to the map make sure the corresponding parser type is present
     * in the {@code known_type_parsers} resource.
     */
    private static final ImmutableMap<TypeUrl, String> parsers = parsers();

    /**
     * Checks if the JSON parser for the following {@code TypeUrl} is available.
     *
     * @param typeUrl
     *         the type URL to check
     * @return {@code true} if the parser for {@code TypeUrl} is present and {@code false}
     *         otherwise
     */
    public static boolean hasParser(TypeUrl typeUrl) {
        checkNotNull(typeUrl);
        boolean hasParser = parsers.containsKey(typeUrl);
        return hasParser;
    }

    /**
     * Stores parsers {@code Map} to the {@code jsOutput}.
     *
     * <p>The name of the exported map is the {@link #MAP_NAME}.
     */
    @Override
    public CodeLines value() {
        CodeLines out = new CodeLines();
        out.append(emptyLine());
        out.append(exportMap());
        return out;
    }

    private static MapExportSnippet exportMap() {
        return MapExportSnippet
                .newBuilder(MAP_NAME)
                .withEntries(mapEntries())
                .build();
    }

    private static List<Entry<String, String>> mapEntries() {
        ImmutableSet<Entry<TypeUrl, String>> entries = parsers.entrySet();
        List<Entry<String, String>> entryLiterals = entries.stream()
                                                           .map(ExportStandardParsers::mapEntry)
                                                           .collect(toList());
        return entryLiterals;
    }

    /**
     * Converts the {@linkplain Entry Java Map Entry} from the {@link #parsers} to the JS
     * {@code Map} entry.
     */
    private static Entry<String, String> mapEntry(Entry<TypeUrl, String> typeToParser) {
        TypeUrl typeUrl = typeToParser.getKey();
        String parserName = typeToParser.getValue();
        String newParserCall = "new " + parserName + "()";
        return Maps.immutableEntry(typeUrl.value(), newParserCall);
    }

    /**
     * Composes a map from a type to corresponding JS parser class.
     *
     * <p>These parsers support standard
     * <a href="https://developers.google.com/protocol-buffers/docs/proto3#json">
     *     Proto 3 JSON Mapping</a>
     *
     * For the implementation of parsers, please see the resource file at this path:
     * <pre>
     *     proto-js-plugin/src/main/resources/io/spine/tools/protojs/knowntypes/known_types_parsers
     * </pre>
     */
    @SuppressWarnings("OverlyCoupledMethod") // Dependencies for the listed Protobuf types.
    private static ImmutableMap<TypeUrl, String> parsers() {
        ImmutableList<Class<? extends GeneratedMessageV3>> messageClasses =
                ImmutableList.of(
                        BoolValue.class,
                        BytesValue.class,
                        DoubleValue.class,
                        FloatValue.class,
                        StringValue.class,
                        Int32Value.class,
                        Int64Value.class,
                        UInt32Value.class,
                        UInt64Value.class,
                        Value.class,
                        ListValue.class,
                        Empty.class,
                        Timestamp.class,
                        Duration.class,
                        FieldMask.class,
                        Any.class
                );

        ImmutableMap<TypeUrl, String> result =
                messageClasses.stream()
                              .collect(toImmutableMap(TypeUrl::of,
                                                      cls -> cls.getSimpleName() + "Parser"));
        return result;
    }
}
