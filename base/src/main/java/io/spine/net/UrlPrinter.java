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

package io.spine.net;

import io.spine.net.Url.Record.QueryParameter;

import java.util.List;

/**
 * Performs conversion of URLs to String.
 *
 * @author Mikhail Mikhaylov
 */
@SuppressWarnings({"UtilityClass", "TypeMayBeWeakened"})
class UrlPrinter {

    private UrlPrinter() {
    }

    /**
     * Converts {@link Url} to String.
     *
     * <p>Does not perform any validation.
     *
     * @param url already valid {@link Url} instance
     * @return String {@link Url} value
     */
    static String printToString(Url url) {
        if (url.getValueCase() == Url.ValueCase.RAW) {
            return url.getRaw();
        }

        // We don't know the capacity at this point
        @SuppressWarnings("StringBufferWithoutInitialCapacity") StringBuilder sb = new StringBuilder();

        Url.Record record = url.getRecord();
        appendProtocol(record, sb);
        appendAuth(record, sb);
        appendHost(record, sb);
        appendPort(record, sb);
        appendPath(record, sb);
        appendQueries(record, sb);
        appendFragment(record, sb);

        return sb.toString();
    }

    private static void appendProtocol(Url.Record record, StringBuilder sb) {
        if (!record.hasProtocol()) {
            return;
        }

        Url.Record.Protocol protocol = record.getProtocol();
        if (protocol.getProtocolCase() == Url.Record.Protocol.ProtocolCase.NAME) {
            sb.append(protocol.getName())
              .append(UrlParser.PROTOCOL_ENDING);
            return;
        }

        sb.append(Schemas.getLowerCaseName(protocol.getSchema()))
          .append(UrlParser.PROTOCOL_ENDING);
    }

    private static void appendAuth(Url.Record record, StringBuilder sb) {
        if (!record.hasAuth() || record.getAuth()
                                       .equals(Url.Record.Authorization.getDefaultInstance())) {
            return;
        }

        Url.Record.Authorization auth = record.getAuth();
        String userName = auth.getUserName();
        String password = auth.getPassword();

        if (userName.isEmpty()) {
            return;
        }
        sb.append(userName);

        if (!password.isEmpty()) {
            sb.append(UrlParser.CREDENTIALS_SEPARATOR)
              .append(password);
        }

        sb.append(UrlParser.CREDENTIALS_ENDING);
    }

    private static void appendHost(Url.Record record, StringBuilder sb) {
        sb.append(record.getHost());
    }

    private static void appendPort(Url.Record record, StringBuilder sb) {
        String port = record.getPort();
        if (port.isEmpty()) {
            return;
        }

        sb.append(UrlParser.HOST_PORT_SEPARATOR)
          .append(port);
    }

    private static void appendPath(Url.Record record, StringBuilder sb) {
        String path = record.getPath();
        if (path.isEmpty()) {
            return;
        }

        sb.append(UrlParser.HOST_ENDING)
          .append(path);
    }

    private static void appendQueries(Url.Record record, StringBuilder sb) {
        List<QueryParameter> queryList = record.getQueryList();

        if (queryList.isEmpty()) {
            return;
        }

        sb.append(UrlParser.QUERIES_START);

        int queriesSize = queryList.size();
        for (int i = 0; i < queriesSize; i++) {
            String stringQuery = UrlQueryParameters.toString(queryList.get(i));
            sb.append(stringQuery);
            if (i != queriesSize - 1) {
                sb.append(UrlParser.QUERY_SEPARATOR);
            }
        }
    }

    private static void appendFragment(Url.Record record, StringBuilder sb) {
        String fragment = record.getFragment();
        if (fragment.isEmpty()) {
            return;
        }

        sb.append(UrlParser.FRAGMENT_START)
          .append(fragment);
    }
}
