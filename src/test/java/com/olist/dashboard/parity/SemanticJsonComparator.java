package com.olist.dashboard.parity;

import java.util.ArrayList;
import java.util.List;

import tools.jackson.databind.JsonNode;

/**
 * Compares successful API JSON using the frozen FastAPI contract's exact-shape and floating-point
 * rules.
 */
public final class SemanticJsonComparator {

    private static final double ABSOLUTE_FLOAT_TOLERANCE = 1e-9;
    private static final double RELATIVE_FLOAT_TOLERANCE = 1e-12;

    public List<ParityMismatch> compare(JsonNode expected, JsonNode actual) {
        var mismatches = new ArrayList<ParityMismatch>();
        compareAt("$", expected, actual, mismatches);
        return List.copyOf(mismatches);
    }

    private void compareAt(String path, JsonNode expected, JsonNode actual, List<ParityMismatch> mismatches) {
        if (expected.getNodeType() != actual.getNodeType()) {
            mismatches.add(new ParityMismatch(
                    path,
                    "node type differs: expected " + expected.getNodeType() + " but was " + actual.getNodeType()));
            return;
        }

        if (expected.isObject()) {
            compareObject(path, expected, actual, mismatches);
            return;
        }
        if (expected.isArray()) {
            compareArray(path, expected, actual, mismatches);
            return;
        }
        if (expected.isNumber()) {
            compareNumber(path, expected, actual, mismatches);
            return;
        }
        if (!expected.equals(actual)) {
            mismatches.add(new ParityMismatch(path, "scalar value differs"));
        }
    }

    private void compareObject(String path, JsonNode expected, JsonNode actual, List<ParityMismatch> mismatches) {
        List<String> expectedNames = new ArrayList<>(expected.propertyNames());
        List<String> actualNames = new ArrayList<>(actual.propertyNames());
        if (!expectedNames.equals(actualNames)) {
            mismatches.add(new ParityMismatch(
                    path,
                    "object field names or order differ: expected " + expectedNames + " but was " + actualNames));
        }
        for (String fieldName : expectedNames) {
            JsonNode actualChild = actual.get(fieldName);
            if (actualChild != null) {
                compareAt(path + "." + fieldName, expected.get(fieldName), actualChild, mismatches);
            }
        }
    }

    private void compareArray(String path, JsonNode expected, JsonNode actual, List<ParityMismatch> mismatches) {
        if (expected.size() != actual.size()) {
            mismatches.add(new ParityMismatch(
                    path,
                    "array length differs: expected " + expected.size() + " but was " + actual.size()));
        }
        int sharedLength = Math.min(expected.size(), actual.size());
        for (int index = 0; index < sharedLength; index++) {
            compareAt(path + "[" + index + "]", expected.get(index), actual.get(index), mismatches);
        }
    }

    private void compareNumber(String path, JsonNode expected, JsonNode actual, List<ParityMismatch> mismatches) {
        if (expected.isIntegralNumber() != actual.isIntegralNumber()) {
            mismatches.add(new ParityMismatch(path, "integer-versus-floating JSON number kind differs"));
            return;
        }
        if (expected.isIntegralNumber()) {
            if (!expected.bigIntegerValue().equals(actual.bigIntegerValue())) {
                mismatches.add(new ParityMismatch(
                        path,
                        "integer value differs: expected " + expected.bigIntegerValue()
                                + " but was " + actual.bigIntegerValue()));
            }
            return;
        }

        double expectedValue = expected.doubleValue();
        double actualValue = actual.doubleValue();
        if (!Double.isFinite(expectedValue) || !Double.isFinite(actualValue)) {
            mismatches.add(new ParityMismatch(path, "non-finite JSON number encountered"));
            return;
        }
        double tolerance = Math.max(
                ABSOLUTE_FLOAT_TOLERANCE,
                RELATIVE_FLOAT_TOLERANCE * Math.max(1.0, Math.abs(expectedValue)));
        if (Math.abs(expectedValue - actualValue) > tolerance) {
            mismatches.add(new ParityMismatch(
                    path,
                    "floating value differs beyond tolerance " + tolerance + ": expected "
                            + expectedValue + " but was " + actualValue));
        }
    }
}
