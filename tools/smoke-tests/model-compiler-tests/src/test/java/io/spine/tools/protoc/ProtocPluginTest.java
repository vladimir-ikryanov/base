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

package io.spine.tools.protoc;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.protobuf.Message;
import io.spine.base.CommandMessage;
import io.spine.base.EventMessage;
import io.spine.base.Identifier;
import io.spine.base.RejectionMessage;
import io.spine.tools.protoc.test.PIUserEvent;
import io.spine.tools.protoc.test.UserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ProtocPlugin should")
class ProtocPluginTest {

    private static final String EVENT_INTERFACE_FQN =
            "io.spine.tools.protoc.PICustomerEvent";
    private static final String COMMAND_INTERFACE_FQN =
            "io.spine.tools.protoc.PICustomerCommand";
    private static final String USER_COMMAND_FQN =
            "io.spine.tools.protoc.PIUserCommand";

    @Test
    @DisplayName("generate marker interfaces")
    void generateMarkerInterfaces() throws ClassNotFoundException {
        checkMarkerInterface(EVENT_INTERFACE_FQN);
    }

    @Test
    @DisplayName("implement marker interface in the generated messages")
    void implementMarkerInterfacesInGeneratedMessages() {
        assertThat(PICustomerNotified.getDefaultInstance(), instanceOf(PICustomerEvent.class));
        assertThat(PICustomerEmailReceived.getDefaultInstance(), instanceOf(PICustomerEvent.class));
    }

    @Test
    @DisplayName("generate marker interfaces for the separate messages")
    void generateMarkerInterfacesForSeparateMessages() throws ClassNotFoundException {
        checkMarkerInterface(COMMAND_INTERFACE_FQN);
    }

    @Test
    @DisplayName("implement interface in the generated messages with `IS` option")
    void implementInterfaceInGeneratedMessagesWithIsOption() {
        assertThat(PICustomerCreated.getDefaultInstance(), instanceOf(PICustomerEvent.class));
        assertThat(PICreateCustomer.getDefaultInstance(), instanceOf(PICustomerCommand.class));
    }

    @Test
    @DisplayName("use `IS` in priority to `EVERY IS`")
    void useIsInPriorityToEveryIs() {
        assertThat(PIUserCreated.getDefaultInstance(), instanceOf(PIUserEvent.class));
        assertThat(PIUserNameUpdated.getDefaultInstance(), instanceOf(PIUserEvent.class));

        assertThat(UserName.getDefaultInstance(), not(instanceOf(PIUserEvent.class)));
        assertThat(UserName.getDefaultInstance(), instanceOf(UserInfo.class));
    }

    @Test
    @DisplayName("resolve packages from src proto if the packages are not specified")
    void resolvePackagesFromSrcProtoIfNotSpecified() throws ClassNotFoundException {
        Class<?> cls = checkMarkerInterface(USER_COMMAND_FQN);
        assertTrue(cls.isAssignableFrom(PICreateUser.class));
    }

    @Test
    @DisplayName("skip non specified message types")
    void skipNonSpecifiedMessageTypes() {
        Class<?> cls = CustomerName.class;
        Class[] interfaces = cls.getInterfaces();
        assertEquals(1, interfaces.length);
        assertSame(CustomerNameOrBuilder.class, interfaces[0]);
    }

    @Test
    @DisplayName("mark as event messages")
    void markEventMessages() {
        assertThat(UserCreated.getDefaultInstance(), instanceOf(EventMessage.class));
        assertThat(UserCreated.getDefaultInstance(), instanceOf(FirstEvent.class));
        assertThat(UserNotified.getDefaultInstance(), instanceOf(EventMessage.class));
    }

    @Test
    @DisplayName("mark as command messages")
    void markCommandMessages() {
        assertThat(CreateUser.getDefaultInstance(), instanceOf(CommandMessage.class));
        assertThat(NotifyUser.getDefaultInstance(), instanceOf(CommandMessage.class));
    }

    @Test
    @DisplayName("mark as rejection messages")
    void markRejectionMessages() {
        assertThat(Rejections.UserAlreadyExists.getDefaultInstance(),
                   instanceOf(RejectionMessage.class));
        assertThat(Rejections.UserAlreadyExists.getDefaultInstance(),
                   instanceOf(UserRejection.class));
    }

    @Test
    @DisplayName("mark messages with already existing interface types")
    @SuppressWarnings("UnnecessaryLocalVariable") // Compile-time verification.
    void implementHandcraftedInterfaces() {
        assertThat(Rejections.UserAlreadyExists.getDefaultInstance(),
                   instanceOf(UserRejection.class));
        assertFalse(Message.class.isAssignableFrom(UserRejection.class));
        String id = Identifier.newUuid();
        Rejections.UserAlreadyExists message = Rejections.UserAlreadyExists
                .newBuilder()
                .setId(id)
                .build();
        UserRejection rejection = message;
        assertEquals(id, rejection.getId());
    }

    @CanIgnoreReturnValue
    private static Class<?> checkMarkerInterface(String fqn) throws ClassNotFoundException {
        Class<?> cls = Class.forName(fqn);
        assertTrue(cls.isInterface());
        assertTrue(Message.class.isAssignableFrom(cls));

        Method[] declaredMethods = cls.getDeclaredMethods();
        assertEquals(0, declaredMethods.length);
        return cls;
    }
}
