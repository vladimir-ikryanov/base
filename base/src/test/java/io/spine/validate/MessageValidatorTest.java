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

package io.spine.validate;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DoubleValue;
import com.google.protobuf.Message;
import com.google.protobuf.ProtocolStringList;
import com.google.protobuf.StringValue;
import com.google.protobuf.Timestamp;
import io.spine.base.FieldPath;
import io.spine.option.OptionsProto;
import io.spine.option.Time;
import io.spine.protobuf.AnyPacker;
import io.spine.test.validate.AggregateState;
import io.spine.test.validate.CustomMessageRequiredByteStringFieldValue;
import io.spine.test.validate.CustomMessageRequiredEnumFieldValue;
import io.spine.test.validate.CustomMessageRequiredMsgFieldValue;
import io.spine.test.validate.CustomMessageRequiredRepeatedMsgFieldValue;
import io.spine.test.validate.CustomMessageRequiredStringFieldValue;
import io.spine.test.validate.CustomMessageWithNoRequiredOption;
import io.spine.test.validate.DecimalMaxIncNumberFieldValue;
import io.spine.test.validate.DecimalMaxNotIncNumberFieldValue;
import io.spine.test.validate.DecimalMinIncNumberFieldValue;
import io.spine.test.validate.DecimalMinNotIncNumberFieldValue;
import io.spine.test.validate.DigitsCountNumberFieldValue;
import io.spine.test.validate.EnclosedMessageFieldValue;
import io.spine.test.validate.EnclosedMessageFieldValueWithCustomInvalidMessage;
import io.spine.test.validate.EnclosedMessageFieldValueWithoutAnnotationFieldValueWithCustomInvalidMessage;
import io.spine.test.validate.EnclosedMessageWithRequiredString;
import io.spine.test.validate.EnclosedMessageWithoutAnnotationFieldValue;
import io.spine.test.validate.MaxNumberFieldValue;
import io.spine.test.validate.MinNumberFieldValue;
import io.spine.test.validate.PatternStringFieldValue;
import io.spine.test.validate.ProjectionState;
import io.spine.test.validate.RepeatedRequiredMsgFieldValue;
import io.spine.test.validate.RequiredBooleanFieldValue;
import io.spine.test.validate.RequiredByteStringFieldValue;
import io.spine.test.validate.RequiredEnumFieldValue;
import io.spine.test.validate.RequiredMsgFieldValue;
import io.spine.test.validate.RequiredStringFieldValue;
import io.spine.test.validate.TimeInFutureFieldValue;
import io.spine.test.validate.TimeInPastFieldValue;
import io.spine.test.validate.TimeWithoutOptsFieldValue;
import io.spine.test.validate.anyfields.AnyContainer;
import io.spine.test.validate.anyfields.UncheckedAnyContainer;
import io.spine.test.validate.command.EntityIdByteStringFieldValue;
import io.spine.test.validate.command.EntityIdDoubleFieldValue;
import io.spine.test.validate.command.EntityIdIntFieldValue;
import io.spine.test.validate.command.EntityIdLongFieldValue;
import io.spine.test.validate.command.EntityIdMsgFieldValue;
import io.spine.test.validate.command.EntityIdRepeatedFieldValue;
import io.spine.test.validate.command.EntityIdStringFieldValue;
import io.spine.test.validate.oneof.EveryOptional;
import io.spine.test.validate.oneof.EveryRequired;
import io.spine.test.validate.oneof.OneRequired;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static com.google.protobuf.util.Timestamps.add;
import static com.google.protobuf.util.Timestamps.subtract;
import static io.spine.base.Identifier.newUuid;
import static io.spine.base.Time.getCurrentTime;
import static io.spine.validate.MessageValidatorTestEnv.FIFTY_NANOSECONDS;
import static io.spine.validate.MessageValidatorTestEnv.SECONDS_IN_5_MINUTES;
import static io.spine.validate.MessageValidatorTestEnv.ZERO_NANOSECONDS;
import static io.spine.validate.MessageValidatorTestEnv.currentTimeWithNanos;
import static io.spine.validate.MessageValidatorTestEnv.freezeTime;
import static io.spine.validate.MessageValidatorTestEnv.timeWithNanos;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings({"ClassWithTooManyMethods", "OverlyCoupledClass"})
@DisplayName("MessageValidator should")
class MessageValidatorTest {

    private static final double EQUAL_MIN = 16.5;
    private static final double GREATER_THAN_MIN = EQUAL_MIN + 5;
    private static final double LESS_THAN_MIN = EQUAL_MIN - 5;

    private static final double EQUAL_MAX = 64.5;
    private static final double GREATER_THAN_MAX = EQUAL_MAX + 5;
    private static final double LESS_THAN_MAX = EQUAL_MAX - 5;

    private static final double INT_DIGIT_COUNT_GREATER_THAN_MAX = 123.5;
    private static final double INT_DIGIT_COUNT_EQUAL_MAX = 12.5;
    private static final double INT_DIGIT_COUNT_LESS_THAN_MAX = 1.5;

    @SuppressWarnings("DuplicateStringLiteralInspection")
    private static final String VALUE = "value";
    private static final String EMAIL = "email";
    private static final String OUTER_MSG_FIELD = "outer_msg_field";

    private static final String NO_VALUE_MSG = "Value must be set.";
    private static final String LESS_THAN_MIN_MSG = "Number must be greater than or equal to 16.5.";
    private static final String GREATER_MAX_MSG = "Number must be less than or equal to 64.5.";
    private static final String MATCH_REGEXP_MSG = "String must match the regular expression '%s'.";

    private List<ConstraintViolation> violations;

    /*
     * Required option tests.
     */

    @Test
    @DisplayName("find out that required Message field is set")
    void find_out_that_required_Message_field_is_set() {
        RequiredMsgFieldValue validMsg = RequiredMsgFieldValue
                .newBuilder()
                .setValue(newStringValue())
                .build();
        assertValid(validMsg);
    }

    @Test
    @DisplayName("find out that required message field is NOT set")
    void find_out_that_required_Message_field_is_NOT_set() {
        RequiredMsgFieldValue invalidMsg = RequiredMsgFieldValue.getDefaultInstance();
        assertNotValid(invalidMsg);
    }

    @Test
    @DisplayName("find out that required String field is set")
    void find_out_that_required_String_field_is_set() {
        RequiredStringFieldValue validMsg = RequiredStringFieldValue.newBuilder()
                                                                    .setValue(newUuid())
                                                                    .build();
        assertValid(validMsg);
    }

    @Test
    @DisplayName("find out that required String field is NOT set")
    void find_out_that_required_String_field_is_NOT_set() {
        RequiredStringFieldValue invalidMsg = RequiredStringFieldValue.getDefaultInstance();
        assertNotValid(invalidMsg);
    }

    @Test
    @DisplayName("find out that required ByteString field is set")
    void find_out_that_required_ByteString_field_is_set() {
        RequiredByteStringFieldValue validMsg =
                RequiredByteStringFieldValue.newBuilder()
                                            .setValue(newByteString())
                                            .build();
        assertValid(validMsg);
    }

    @Test
    @DisplayName("find out that required ByteString field is NOT set")
    void find_out_that_required_ByteString_field_is_NOT_set() {
        RequiredByteStringFieldValue invalidMsg = RequiredByteStringFieldValue.getDefaultInstance();
        assertNotValid(invalidMsg);
    }

    @Test
    @DisplayName("find out that required Enum field is set")
    void find_out_that_required_Enum_field_is_NOT_set() {
        RequiredEnumFieldValue invalidMsg = RequiredEnumFieldValue.getDefaultInstance();
        assertNotValid(invalidMsg);
    }

    @Test
    @DisplayName("find out that required Enum field is NOT set")
    void find_out_that_required_Enum_field_is_set() {
        RequiredEnumFieldValue invalidMsg = RequiredEnumFieldValue.newBuilder()
                                                                  .setValue(Time.FUTURE)
                                                                  .build();
        assertValid(invalidMsg);
    }

    @Test
    @DisplayName("find out that required NOT set Boolean field passes validation")
    void find_out_that_required_NOT_set_Boolean_field_pass_validation() {
        RequiredBooleanFieldValue msg = RequiredBooleanFieldValue.getDefaultInstance();
        assertValid(msg);
    }

    @Test
    @DisplayName("find out that repeated required field has valid values")
    void find_out_that_repeated_required_field_has_valid_values() {
        RepeatedRequiredMsgFieldValue invalidMsg =
                RepeatedRequiredMsgFieldValue.newBuilder()
                                             .addValue(newStringValue())
                                             .addValue(newStringValue())
                                             .build();
        assertValid(invalidMsg);
    }

    @Test
    @DisplayName("find out that repeated required field has not values")
    void find_out_that_repeated_required_field_has_no_values() {
        RepeatedRequiredMsgFieldValue msg = RepeatedRequiredMsgFieldValue.getDefaultInstance();
        assertNotValid(msg);
    }

    @Test
    @DisplayName("ignore repeated required field with an empty value")
    void ignore_repeated_required_field_with_empty_value() {
        RepeatedRequiredMsgFieldValue msg = RepeatedRequiredMsgFieldValue
                .newBuilder()
                .addValue(newStringValue()) // valid value
                .addValue(StringValue.getDefaultInstance()) // empty value
                .build();
        assertValid(msg);
    }

    @Test
    @DisplayName("consider field is valid if no required option set")
    void consider_field_is_valid_if_no_required_option_set() {
        StringValue msg = StringValue.getDefaultInstance();
        assertValid(msg);
    }

    @Test
    @DisplayName("provide one valid violation if required field is NOT set")
    void provide_one_valid_violation_if_required_field_is_NOT_set() {
        RequiredStringFieldValue invalidMsg = RequiredStringFieldValue.getDefaultInstance();
        assertSingleViolation(invalidMsg, NO_VALUE_MSG, VALUE);
    }

    @Test
    @DisplayName("ignore IfMissingOption if field is not marked required")
    void ignore_if_missing_option_if_field_not_marked_required() {
        CustomMessageWithNoRequiredOption invalidMsg =
                CustomMessageWithNoRequiredOption.getDefaultInstance();

        assertValid(invalidMsg);
        assertTrue(violations.isEmpty());
    }

    /*
     * Time option tests.
     */

    @Test
    @DisplayName("find out that time is in future")
    void find_out_that_time_is_in_future() {
        TimeInFutureFieldValue validMsg = TimeInFutureFieldValue.newBuilder()
                                                                .setValue(getFuture())
                                                                .build();
        assertValid(validMsg);
    }

    @Test
    @DisplayName("find out that time is NOT in future")
    void find_out_that_time_is_NOT_in_future() {
        TimeInFutureFieldValue invalidMsg = TimeInFutureFieldValue.newBuilder()
                                                                  .setValue(getPast())
                                                                  .build();
        assertNotValid(invalidMsg);
    }

    @Test
    @DisplayName("find out that time is in past")
    void find_out_that_time_is_in_past() {
        TimeInPastFieldValue validMsg = TimeInPastFieldValue.newBuilder()
                                                            .setValue(getPast())
                                                            .build();
        assertValid(validMsg);
    }

    @Test
    @DisplayName("find out that time is NOT in past")
    void find_out_that_time_is_NOT_in_past() {
        TimeInPastFieldValue invalidMsg = TimeInPastFieldValue.newBuilder()
                                                              .setValue(getFuture())
                                                              .build();
        assertNotValid(invalidMsg);
    }

    @Test
    @DisplayName("find out that time is NOT in the past by nanoseconds")
    void find_out_that_time_is_NOT_in_the_past_by_nanos() {
        Timestamp currentTime = currentTimeWithNanos(ZERO_NANOSECONDS);
        Timestamp timeInFuture = timeWithNanos(currentTime, FIFTY_NANOSECONDS);
        freezeTime(currentTime);
        TimeInPastFieldValue invalidMsg =
                TimeInPastFieldValue.newBuilder()
                                    .setValue(timeInFuture)
                                    .build();
        assertNotValid(invalidMsg);
    }

    @Test
    @DisplayName("find out that time is in the past by nanoseconds")
    void find_out_that_time_is_in_the_past_by_nanos() {
        Timestamp currentTime = currentTimeWithNanos(FIFTY_NANOSECONDS);
        Timestamp timeInPast = timeWithNanos(currentTime, ZERO_NANOSECONDS);
        freezeTime(currentTime);
        TimeInPastFieldValue invalidMsg =
                TimeInPastFieldValue.newBuilder()
                                    .setValue(timeInPast)
                                    .build();
        assertValid(invalidMsg);
    }

    @Test
    @DisplayName("consider Timestamp field valid if no TimeOption set")
    void consider_timestamp_field_is_valid_if_no_time_option_set() {
        TimeWithoutOptsFieldValue msg = TimeWithoutOptsFieldValue.getDefaultInstance();
        assertValid(msg);
    }

    @Test
    @DisplayName("provide one valid violation if time is invalid")
    void provide_one_valid_violation_if_time_is_invalid() {
        TimeInFutureFieldValue invalidMsg = TimeInFutureFieldValue.newBuilder()
                                                                  .setValue(getPast())
                                                                  .build();
        assertSingleViolation(invalidMsg, "Timestamp value must be in the future.", VALUE);
    }

    /*
     * `google.protobuf.Any` field tests.
     */

    @Test
    @DisplayName("consider Any valid if content is valid")
    void consider_Any_valid_if_content_is_valid() {
        RequiredMsgFieldValue value = RequiredMsgFieldValue
                .newBuilder()
                .setValue(newStringValue())
                .build();
        Any content = AnyPacker.pack(value);
        AnyContainer container = AnyContainer
                .newBuilder()
                .setAny(content)
                .build();
        assertValid(container);
    }

    @Test
    @DisplayName("consider Any not valid if content is not valid")
    void consider_Any_not_valid_if_content_is_not_valid() {
        RequiredMsgFieldValue value = RequiredMsgFieldValue.getDefaultInstance();
        Any content = AnyPacker.pack(value);
        AnyContainer container = AnyContainer
                .newBuilder()
                .setAny(content)
                .build();
        assertNotValid(container);
    }

    @Test
    @DisplayName("consider Any valid if validation is not required")
    void consider_Any_valid_if_validation_is_not_required() {
        RequiredMsgFieldValue value = RequiredMsgFieldValue.getDefaultInstance();
        Any content = AnyPacker.pack(value);
        UncheckedAnyContainer container = UncheckedAnyContainer
                .newBuilder()
                .setAny(content)
                .build();
        assertValid(container);
    }

    @Test
    @DisplayName("validate recursive messages")
    void validate_recursive_messages() {
        RequiredMsgFieldValue value = RequiredMsgFieldValue.getDefaultInstance();
        Any internalAny = AnyPacker.pack(value);
        AnyContainer internal = AnyContainer
                .newBuilder()
                .setAny(internalAny)
                .build();
        Any externalAny = AnyPacker.pack(internal);
        AnyContainer external = AnyContainer
                .newBuilder()
                .setAny(externalAny)
                .build();
        assertNotValid(external);
    }

    /*
     * Decimal min option tests.
     */

    @Test
    @DisplayName("Consider number field is valid if no number options set")
    void consider_number_field_is_valid_if_no_number_options_set() {
        Message nonZeroValue = DoubleValue.newBuilder()
                                          .setValue(5)
                                          .build();
        assertValid(nonZeroValue);
    }

    @Test
    @DisplayName("find out that number is greater than decimal min inclusive")
    void find_out_that_number_is_greater_than_decimal_min_inclusive() {
        minDecimalNumberTest(GREATER_THAN_MIN, true, true);
    }

    @Test
    @DisplayName("find out that number is equal to decimal min inclusive")
    void find_out_that_number_is_equal_to_decimal_min_inclusive() {
        minDecimalNumberTest(EQUAL_MIN, true, true);
    }

    @Test
    @DisplayName("find out that number is less than decimal min inclusive")
    void find_out_that_number_is_less_than_decimal_min_inclusive() {
        minDecimalNumberTest(LESS_THAN_MIN, true, false);
    }

    @Test
    @DisplayName("find out that number is grated than decimal min NOT inclusive")
    void find_out_that_number_is_greater_than_decimal_min_NOT_inclusive() {
        minDecimalNumberTest(GREATER_THAN_MIN, false, true);
    }

    @Test
    @DisplayName("find out that number is equal to decimal min NOT inclusive")
    void find_out_that_number_is_equal_to_decimal_min_NOT_inclusive() {
        minDecimalNumberTest(EQUAL_MIN, false, false);
    }

    @Test
    @DisplayName("find out that number is less than decimal min NOT inclusive")
    void find_out_that_number_is_less_than_decimal_min_NOT_inclusive() {
        minDecimalNumberTest(LESS_THAN_MIN, false, false);
    }

    @Test
    @DisplayName("provide one valid violation if number is less than decimal min")
    void provide_one_valid_violation_if_number_is_less_than_decimal_min() {
        minDecimalNumberTest(LESS_THAN_MIN, true, false);
        assertSingleViolation(LESS_THAN_MIN_MSG, VALUE);
    }

    /*
     * Decimal max option tests.
     */

    @Test
    @DisplayName("find out that number is greater than decimal max inclusive")
    void find_out_that_number_is_greater_than_decimal_max_inclusive() {
        maxDecimalNumberTest(GREATER_THAN_MAX, true, false);
    }

    @Test
    @DisplayName("find out that number is equal to decimal max inclusive")
    void find_out_that_number_is_equal_to_decimal_max_inclusive() {
        maxDecimalNumberTest(EQUAL_MAX, true, true);
    }

    @Test
    @DisplayName("find out that number is less than decimal max inclusive")
    void find_out_that_number_is_less_than_decimal_max_inclusive() {
        maxDecimalNumberTest(LESS_THAN_MAX, true, true);
    }

    @Test
    @DisplayName("find out that number is greated than decimal max NOT inclusive")
    void find_out_that_number_is_greater_than_decimal_max_NOT_inclusive() {
        maxDecimalNumberTest(GREATER_THAN_MAX, false, false);
    }

    @Test
    @DisplayName("find out that number is equal to decimal max NOT inclusive")
    void find_out_that_number_is_equal_to_decimal_max_NOT_inclusive() {
        maxDecimalNumberTest(EQUAL_MAX, false, false);
    }

    @Test
    @DisplayName("find out that number is less than decimal max NOT inclusive")
    void find_out_that_number_is_less_than_decimal_max_NOT_inclusive() {
        maxDecimalNumberTest(LESS_THAN_MAX, false, true);
    }

    @Test
    @DisplayName("provide one valid violation if number is greater than decimal max")
    void provide_one_valid_violation_if_number_is_greater_than_decimal_max() {
        maxDecimalNumberTest(GREATER_THAN_MAX, true, false);
        assertSingleViolation(GREATER_MAX_MSG, VALUE);
    }

    /*
     * Min option tests.
     */

    @Test
    @DisplayName("find out that number is greater than min")
    void find_out_that_number_is_greater_than_min() {
        MinNumberFieldValue msg = MinNumberFieldValue.newBuilder()
                                                     .setValue(GREATER_THAN_MIN)
                                                     .build();
        assertValid(msg);
    }

    @Test
    @DisplayName("find out that number is equal to min")
    void find_out_that_number_is_equal_to_min() {
        MinNumberFieldValue msg = MinNumberFieldValue.newBuilder()
                                                     .setValue(EQUAL_MIN)
                                                     .build();
        assertValid(msg);
    }

    @Test
    @DisplayName("find out that number is less than min")
    void find_out_that_number_is_less_than_min() {
        MinNumberFieldValue msg = MinNumberFieldValue.newBuilder()
                                                     .setValue(LESS_THAN_MIN)
                                                     .build();
        assertNotValid(msg);
    }

    @Test
    @DisplayName("provide one valid violation if number is less than min")
    void provide_one_valid_violation_if_number_is_less_than_min() {
        MinNumberFieldValue msg = MinNumberFieldValue.newBuilder()
                                                     .setValue(LESS_THAN_MIN)
                                                     .build();
        assertSingleViolation(msg, LESS_THAN_MIN_MSG, VALUE);
    }

    /*
     * Max option tests.
     */

    @Test
    @DisplayName("find out that number is greater than max inclusive")
    void find_out_that_number_is_greater_than_max_inclusive() {
        MaxNumberFieldValue msg = MaxNumberFieldValue.newBuilder()
                                                     .setValue(GREATER_THAN_MAX)
                                                     .build();
        assertNotValid(msg);
    }

    @Test
    @DisplayName("find out that number is equal to max inclusive")
    void find_out_that_number_is_equal_to_max_inclusive() {
        MaxNumberFieldValue msg = MaxNumberFieldValue.newBuilder()
                                                     .setValue(EQUAL_MAX)
                                                     .build();
        assertValid(msg);
    }

    @Test
    @DisplayName("find out that number is less than max inclusive")
    void find_out_that_number_is_less_than_max_inclusive() {
        MaxNumberFieldValue msg = MaxNumberFieldValue.newBuilder()
                                                     .setValue(LESS_THAN_MAX)
                                                     .build();
        assertValid(msg);
    }

    @Test
    @DisplayName("provide one valid violation if number is greater than max")
    void provide_one_valid_violation_if_number_is_greater_than_max() {
        MaxNumberFieldValue msg = MaxNumberFieldValue.newBuilder()
                                                     .setValue(GREATER_THAN_MAX)
                                                     .build();
        assertSingleViolation(msg, GREATER_MAX_MSG, VALUE);
    }

    /*
     * String pattern option tests.
     */

    @Test
    @DisplayName("find out that string matches to regex pattern")
    void find_out_that_string_matches_to_regex_pattern() {
        PatternStringFieldValue msg = PatternStringFieldValue.newBuilder()
                                                             .setEmail("valid.email@mail.com")
                                                             .build();
        assertValid(msg);
    }

    @Test
    @DisplayName("find out that string does not match to regex pattern")
    void find_out_that_string_does_not_match_to_regex_pattern() {
        PatternStringFieldValue msg = PatternStringFieldValue.newBuilder()
                                                             .setEmail("invalid email")
                                                             .build();
        assertNotValid(msg);
    }

    @Test
    @DisplayName("consider field is valid if PatternOption is not set")
    void consider_field_is_valid_if_no_pattern_option_set() {
        StringValue msg = StringValue.getDefaultInstance();
        assertValid(msg);
    }

    @Test
    @DisplayName("provide one valid violation if string does not match to regex pattern")
    void provide_one_valid_violation_if_string_does_not_match_to_regex_pattern() {
        PatternStringFieldValue msg = PatternStringFieldValue.newBuilder()
                                                             .setEmail("invalid email")
                                                             .build();
        String expectedErrMsg = format(MATCH_REGEXP_MSG,
                                       "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        assertSingleViolation(msg, expectedErrMsg, EMAIL);
    }

    /*
     * Enclosed message field validation option tests.
     */

    @Test
    @DisplayName("find out that enclosed message field is valid")
    void find_out_that_enclosed_message_field_is_valid() {
        PatternStringFieldValue enclosedMsg =
                PatternStringFieldValue.newBuilder()
                                       .setEmail("valid.email@mail.com")
                                       .build();
        EnclosedMessageFieldValue msg = EnclosedMessageFieldValue.newBuilder()
                                                                 .setOuterMsgField(enclosedMsg)
                                                                 .build();
        assertValid(msg);
    }

    @Test
    @DisplayName("find out enclosed message field is NOT valid")
    void find_out_that_enclosed_message_field_is_NOT_valid() {
        PatternStringFieldValue enclosedMsg = PatternStringFieldValue.newBuilder()
                                                                     .setEmail("invalid email")
                                                                     .build();
        EnclosedMessageFieldValue msg = EnclosedMessageFieldValue.newBuilder()
                                                                 .setOuterMsgField(enclosedMsg)
                                                                 .build();
        assertNotValid(msg);
    }

    @Test
    @DisplayName("consider field valid if no valid option is set")
    void consider_field_valid_if_no_valid_option_is_set() {
        PatternStringFieldValue enclosedMsg = PatternStringFieldValue.newBuilder()
                                                                     .setEmail("invalid email")
                                                                     .build();
        EnclosedMessageWithoutAnnotationFieldValue msg =
                EnclosedMessageWithoutAnnotationFieldValue.newBuilder()
                                                          .setOuterMsgField(enclosedMsg)
                                                          .build();
        assertValid(msg);
    }

    @Test
    @DisplayName("consider field valid if it is not set")
    void consider_field_valid_if_it_is_not_set() {
        EnclosedMessageWithRequiredString msg = EnclosedMessageWithRequiredString.newBuilder()
                                                                                 .build();
        assertValid(msg);
    }

    @Test
    @DisplayName("provide valid violations if enclosed message field is not valid")
    void provide_valid_violations_if_enclosed_message_field_is_not_valid() {
        PatternStringFieldValue enclosedMsg = PatternStringFieldValue.newBuilder()
                                                                     .setEmail("invalid email")
                                                                     .build();
        EnclosedMessageFieldValue msg = EnclosedMessageFieldValue.newBuilder()
                                                                 .setOuterMsgField(enclosedMsg)
                                                                 .build();
        validate(msg);

        assertEquals(1, violations.size());
        ConstraintViolation violation = firstViolation();
        assertEquals("Message must have valid properties.", violation.getMsgFormat());
        assertFieldPathIs(violation, OUTER_MSG_FIELD);
        List<ConstraintViolation> innerViolations = violation.getViolationList();
        assertEquals(1, innerViolations.size());

        ConstraintViolation innerViolation = innerViolations.get(0);
        assertEquals(MATCH_REGEXP_MSG, innerViolation.getMsgFormat());
        assertFieldPathIs(innerViolation, OUTER_MSG_FIELD, EMAIL);
        assertTrue(innerViolation.getViolationList()
                                 .isEmpty());
    }

    @Test
    @DisplayName("provide custom invalid field message if specified")
    void provide_custom_invalid_field_message_if_specified() {
        PatternStringFieldValue enclosedMsg = PatternStringFieldValue.newBuilder()
                                                                     .setEmail("invalid email")
                                                                     .build();
        EnclosedMessageFieldValueWithCustomInvalidMessage msg =
                EnclosedMessageFieldValueWithCustomInvalidMessage.newBuilder()
                                                                 .setOuterMsgField(enclosedMsg)
                                                                 .build();
        validate(msg);

        assertThat(violations).hasSize(1);

        ConstraintViolation violation = firstViolation();
        assertEquals("Custom error", violation.getMsgFormat());
    }

    @Test
    @DisplayName("ignore custom invalid field message if validation is disabled")
    void ignore_custom_invalid_field_message_if_validation_is_disabled() {
        Message msg = EnclosedMessageFieldValueWithoutAnnotationFieldValueWithCustomInvalidMessage.getDefaultInstance();
        assertValid(msg);
    }

    @Nested
    @DisplayName("support DigitsOption and")
    class DigitOption {

        private static final double FRACTIONAL_DIGIT_COUNT_GREATER_THAN_MAX = 1.123;
        private static final double FRACTIONAL_DIGIT_COUNT_EQUAL_MAX = 1.12;
        private static final double FRACTIONAL_DIGIT_COUNT_LESS_THAN_MAX = 1.0;

        @Test
        @DisplayName("find out that integral digit count is greater than max")
        void find_out_that_integral_digit_count_is_greater_than_max() {
            Message msg = messageFor(INT_DIGIT_COUNT_GREATER_THAN_MAX);
            assertNotValid(msg);
        }

        @Test
        @DisplayName("find out that integral digits count is equal to max")
        void find_out_that_integral_digits_count_is_equal_to_max() {
            Message msg = messageFor(INT_DIGIT_COUNT_EQUAL_MAX);
            assertValid(msg);
        }

        @Test
        @DisplayName("find out that integral digit count is less than max")
        void find_out_that_integral_digit_count_is_less_than_max() {
            Message msg = messageFor(INT_DIGIT_COUNT_LESS_THAN_MAX);
            assertValid(msg);
        }

        @Test
        @DisplayName("find out that fractional digit count is greater than max")
        void find_out_that_fractional_digit_count_is_greater_than_max() {
            Message msg = messageFor(FRACTIONAL_DIGIT_COUNT_GREATER_THAN_MAX);
            assertNotValid(msg);
        }

        @Test
        @DisplayName("find out that fractional digit count is equal to max")
        void find_out_that_fractional_digit_count_is_equal_to_max() {
            Message msg = messageFor(FRACTIONAL_DIGIT_COUNT_EQUAL_MAX);
            assertValid(msg);
        }

        @Test
        @DisplayName("find out that fractional digit count is less than max")
        void find_out_that_fractional_digit_count_is_less_than_max() {
            Message msg = messageFor(FRACTIONAL_DIGIT_COUNT_LESS_THAN_MAX);
            assertValid(msg);
        }

        @Test
        @DisplayName("provide one valid violation if integral digit count is greater than max")
        void provide_one_valid_violation_if_integral_digit_count_is_greater_than_max() {
            String expectedErrMsg = "Number value is out of bounds, expected: <2 max digits>.<2 max digits>.";
            Message msg = messageFor(INT_DIGIT_COUNT_GREATER_THAN_MAX);
            assertSingleViolation(msg, expectedErrMsg, VALUE);
        }

        private Message messageFor(double value) {
            return DigitsCountNumberFieldValue.newBuilder()
                                              .setValue(value)
                                              .build();
        }
    }

    @Nested
    @DisplayName("propagate proper error message if custom message set and required")
    class ErrorMessage {

        @Test
        @DisplayName("Message field is NOT set")
        void propagate_proper_error_message_if_custom_message_set_and_required_Message_field_is_NOT_set() {
            CustomMessageRequiredMsgFieldValue invalidMsg =
                    CustomMessageRequiredMsgFieldValue.getDefaultInstance();
            assertErrorMessage(invalidMsg);
        }

        @Test
        @DisplayName("String field is NOT set")
        void propagate_proper_error_message_if_custom_message_set_and_required_String_field_is_NOT_set() {
            CustomMessageRequiredStringFieldValue invalidMsg =
                    CustomMessageRequiredStringFieldValue.getDefaultInstance();
            assertErrorMessage(invalidMsg);
        }

        @Test
        @DisplayName("ByteString field is NOT set")
        void propagate_proper_error_message_if_custom_message_set_and_required_ByteString_field_is_NOT_set() {
            CustomMessageRequiredByteStringFieldValue invalidMsg =
                    CustomMessageRequiredByteStringFieldValue.getDefaultInstance();
            assertErrorMessage(invalidMsg);
        }

        @Test
        @DisplayName("repeated field is NOT set")
        void propagate_proper_error_message_if_custom_message_set_and_required_RepeatedMsg_field_is_NOT_set() {
            CustomMessageRequiredRepeatedMsgFieldValue invalidMsg =
                    CustomMessageRequiredRepeatedMsgFieldValue.getDefaultInstance();
            assertErrorMessage(invalidMsg);
        }

        @Test
        @DisplayName("Enum field is NOT set")
        void propagate_proper_error_message_if_custom_message_set_and_required_Enum_field_is_NOT_set() {
            CustomMessageRequiredEnumFieldValue invalidMsg =
                    CustomMessageRequiredEnumFieldValue.getDefaultInstance();
            assertErrorMessage(invalidMsg);
        }

        private void assertErrorMessage(Message message) {
            assertNotValid(message);
            Descriptors.Descriptor descriptor = message.getDescriptorForType();
            String expectedErrorMessage = getCustomErrorMessage(descriptor);
            checkErrorMessage(expectedErrorMessage);
        }

        private void checkErrorMessage(String expectedMessage) {
            ConstraintViolation constraintViolation = firstViolation();
            assertEquals(expectedMessage, constraintViolation.getMsgFormat());
        }

        private String getCustomErrorMessage(Descriptors.Descriptor descriptor) {
            Descriptors.FieldDescriptor firstFieldDescriptor = descriptor.getFields()
                                                                         .get(0);
            return firstFieldDescriptor.getOptions()
                                       .getExtension(OptionsProto.ifMissing)
                                       .getMsgFormat();
        }
    }

    @Nested
    @DisplayName("validate an entity ID")
    class EntityId {

        @Nested
        @DisplayName("in a command file and")
        class InCommandFile {

            @Test
            @DisplayName("find out that Message is valid")
            void find_out_that_Message_entity_id_in_command_is_valid() {
                EntityIdMsgFieldValue msg = EntityIdMsgFieldValue.newBuilder()
                                                                 .setValue(newStringValue())
                                                                 .build();
                assertValid(msg);
            }

            @Test
            @DisplayName("find out that Message is NOT valid")
            void find_out_that_Message_entity_id_in_command_is_NOT_valid() {
                EntityIdMsgFieldValue msg = EntityIdMsgFieldValue.getDefaultInstance();
                assertNotValid(msg);
            }

            @Test
            @DisplayName("find out that String is valid")
            void find_out_that_String_entity_id_in_command_is_valid() {
                EntityIdStringFieldValue msg = EntityIdStringFieldValue.newBuilder()
                                                                       .setValue(newUuid())
                                                                       .build();
                assertValid(msg);
            }

            @Test
            @DisplayName("find out that String is NOT valid")
            void find_out_that_String_entity_id_in_command_is_NOT_valid() {
                EntityIdStringFieldValue msg = EntityIdStringFieldValue.getDefaultInstance();
                assertNotValid(msg);
            }

            @Test
            @DisplayName("find out that Integer is valid")
            void find_out_that_Integer_entity_id_in_command_is_valid() {
                EntityIdIntFieldValue msg = EntityIdIntFieldValue.newBuilder()
                                                                 .setValue(5)
                                                                 .build();
                assertValid(msg);
            }

            @Test
            @DisplayName("find out that Long is valid")
            void find_out_that_Long_entity_id_in_command_is_valid() {
                EntityIdLongFieldValue msg = EntityIdLongFieldValue.newBuilder()
                                                                   .setValue(5)
                                                                   .build();
                assertValid(msg);
            }

            @Test
            @DisplayName("find out that repeated is NOT valid")
            void find_out_that_repeated_entity_id_in_command_is_not_valid() {
                EntityIdRepeatedFieldValue msg = EntityIdRepeatedFieldValue.newBuilder()
                                                                           .addValue(newUuid())
                                                                           .build();
                assertNotValid(msg);
            }

            @Test
            @DisplayName("provide one valid violation if is not valid")
            void provide_one_valid_violation_if_entity_id_in_command_is_not_valid() {
                EntityIdMsgFieldValue msg = EntityIdMsgFieldValue.getDefaultInstance();
                assertSingleViolation(msg, NO_VALUE_MSG, VALUE);
            }
        }

        @Nested
        @DisplayName("in state and")
        class InState {

            @Test
            @DisplayName("consider it required by default")
            void requiredByDefault() {
                AggregateState stateWithDefaultId = AggregateState.getDefaultInstance();
                assertNotValid(stateWithDefaultId);
            }

            @Test
            @DisplayName("match only the first field named `id` or ending with `_id`")
            void onlyFirstField() {
                AggregateState onlyEntityIdSet = AggregateState.newBuilder()
                                                               .setEntityId(newUuid())
                                                               .build();
                assertValid(onlyEntityIdSet);
            }

            @Test
            @DisplayName("not consider it (required) if the option is set explicitly set to false")
            void notRequiredIfOptionIsFalse() {
                ProjectionState stateWithDefaultId = ProjectionState.getDefaultInstance();
                assertValid(stateWithDefaultId);
            }
        }

        @Nested
        @DisplayName("and reject")
        class Reject {

            @Test
            @DisplayName("ByteString")
            void find_out_that_entity_id_in_command_cannot_be_ByteString() {
                EntityIdByteStringFieldValue msg = EntityIdByteStringFieldValue
                        .newBuilder()
                        .setValue(newByteString())
                        .build();
                assertNotValid(msg);
            }

            @Test
            @DisplayName("Float")
            void find_out_that_entity_id_in_command_cannot_be_float_number() {
                EntityIdDoubleFieldValue msg = EntityIdDoubleFieldValue
                        .newBuilder()
                        .setValue(1.1)
                        .build();
                assertNotValid(msg);
            }
        }
    }

    @Nested
    @DisplayName("consider Oneof")
    class Oneof {

        @Test
        @DisplayName("valid if a required field is set to a non-default value")
        void validIfRequireFieldIsNotDefault() {
            EveryRequired requiredIsNotDefault = EveryRequired
                    .newBuilder()
                    .setFirst(newUuid())
                    .build();
            assertValid(requiredIsNotDefault);
        }

        @Test
        @DisplayName("invalid if a required field is set to the default value")
        void invalidIfRequireFieldIsDefault() {
            EveryRequired requiredIsDefault = EveryRequired
                    .newBuilder()
                    .setFirst("")
                    .build();
            assertNotValid(requiredIsDefault);
        }

        @Test
        @DisplayName("valid if a non-required field is set to the default value")
        void validIfOptionalIsDefault() {
            OneRequired optionalIsDefault = OneRequired
                    .newBuilder()
                    .setOptional("")
                    .build();
            assertValid(optionalIsDefault);
        }

        @Test
        @DisplayName("invalid if all fields are optional, but none is set")
        void invalidIfNoneIsSet() {
            EveryOptional noneIsSet = EveryOptional
                    .newBuilder()
                    .build();
            assertNotValid(noneIsSet);
        }
    }

    /*
     * Utility methods.
     */

    private void minDecimalNumberTest(double value, boolean inclusive, boolean isValid) {
        Message msg = inclusive ?
                      DecimalMinIncNumberFieldValue.newBuilder()
                                                   .setValue(value)
                                                   .build() :
                      DecimalMinNotIncNumberFieldValue.newBuilder()
                                                      .setValue(value)
                                                      .build();
        validate(msg);
        assertIsValid(isValid);
    }

    private void maxDecimalNumberTest(double value, boolean inclusive, boolean isValid) {
        Message msg = inclusive ?
                      DecimalMaxIncNumberFieldValue.newBuilder()
                                                   .setValue(value)
                                                   .build() :
                      DecimalMaxNotIncNumberFieldValue.newBuilder()
                                                      .setValue(value)
                                                      .build();
        validate(msg);
        assertIsValid(isValid);
    }

    private void validate(Message msg) {
        MessageValidator validator = MessageValidator.newInstance(msg);
        violations = validator.validate();
    }

    private ConstraintViolation firstViolation() {
        return violations.get(0);
    }

    private void assertValid(Message msg) {
        validate(msg);
        assertIsValid(true);
    }

    private void assertNotValid(Message msg) {
        validate(msg);
        assertIsValid(false);
    }

    private void assertIsValid(boolean isValid) {
        if (isValid) {
            assertTrue(violations.isEmpty(), () -> violations.toString());
        } else {
            assertFalse(violations.isEmpty());
            for (ConstraintViolation violation : violations) {
                String format = violation.getMsgFormat();
                assertTrue(!format.isEmpty());
                boolean noParams = violation.getParamList()
                                            .isEmpty();
                if (format.contains("%s")) {
                    assertFalse(noParams);
                } else {
                    assertTrue(noParams);
                }
                assertFalse(violation.getFieldPath()
                                     .getFieldNameList()
                                     .isEmpty());
            }
        }
    }

    private void assertSingleViolation(Message message,
                                       String expectedErrMsg,
                                       String invalidFieldName) {
        assertNotValid(message);
        assertEquals(1, violations.size());
        assertSingleViolation(expectedErrMsg, invalidFieldName);
    }

    /** Checks that a message is not valid and has a single violation. */
    private void assertSingleViolation(String expectedErrMsg, String invalidFieldName) {
        ConstraintViolation violation = firstViolation();
        String actualErrorMessage = format(violation.getMsgFormat(), violation.getParamList()
                                                                              .toArray());
        assertEquals(expectedErrMsg, actualErrorMessage);
        assertFieldPathIs(violation, invalidFieldName);
        assertTrue(violation.getViolationList()
                            .isEmpty());
    }

    private static void assertFieldPathIs(ConstraintViolation violation, String... expectedFields) {
        FieldPath path = violation.getFieldPath();
        ProtocolStringList actualFields = path.getFieldNameList();
        assertEquals(expectedFields.length, actualFields.size());
        assertEquals(ImmutableList.copyOf(expectedFields), ImmutableList.copyOf(actualFields));
    }

    private static Timestamp getFuture() {
        Timestamp future = add(getCurrentTime(),
                               MessageValidatorTestEnv.newDuration(SECONDS_IN_5_MINUTES));
        return future;
    }

    private static Timestamp getPast() {
        Timestamp past = subtract(getCurrentTime(),
                                  MessageValidatorTestEnv.newDuration(SECONDS_IN_5_MINUTES));
        return past;
    }

    private static StringValue newStringValue() {
        return StringValue.of(newUuid());
    }

    private static ByteString newByteString() {
        ByteString bytes = ByteString.copyFromUtf8(newUuid());
        return bytes;
    }
}
