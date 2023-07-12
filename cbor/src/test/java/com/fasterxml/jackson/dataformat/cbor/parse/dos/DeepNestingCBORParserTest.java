package com.fasterxml.jackson.dataformat.cbor.parse.dos;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.StreamWriteConstraints;
import com.fasterxml.jackson.core.exc.StreamConstraintsException;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.cbor.CBORTestBase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Unit tests for deeply nested JSON
 */
public class DeepNestingCBORParserTest extends CBORTestBase
{
    public void testDeeplyNestedObjects() throws Exception
    {
        final int depth = 1500;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        genDeepDoc(out, depth);
        try (JsonParser jp = cborParser(out)) {
            JsonToken jt;
            while ((jt = jp.nextToken()) != null) {

            }
            fail("expected StreamConstraintsException");
        } catch (StreamConstraintsException e) {
            String exceptionPrefix = String.format("Document nesting depth (%d) exceeds the maximum allowed",
                    StreamReadConstraints.DEFAULT_MAX_DEPTH + 1);
            assertTrue("JsonMappingException message is as expected?",
                    e.getMessage().startsWith(exceptionPrefix));
        }
    }

    public void testDeeplyNestedObjectsWithUnconstrainedMapper() throws Exception
    {
        final int depth = 1500;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        genDeepDoc(out, depth);
        CBORFactory cborFactory = CBORFactory.builder()
                .streamReadConstraints(StreamReadConstraints.builder().maxNestingDepth(Integer.MAX_VALUE).build())
                .build();
        try (JsonParser jp = cborParser(cborFactory, out)) {
            JsonToken jt;
            while ((jt = jp.nextToken()) != null) {

            }
        }
    }

    public void testDeeplyNestedArrays() throws Exception
    {
        final int depth = 750;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        genDeepArrayDoc(out, depth);
        try (JsonParser jp = cborParser(out)) {
            JsonToken jt;
            while ((jt = jp.nextToken()) != null) {

            }
            fail("expected StreamConstraintsException");
        } catch (StreamConstraintsException e) {
            String exceptionPrefix = String.format("Document nesting depth (%d) exceeds the maximum allowed",
                    StreamReadConstraints.DEFAULT_MAX_DEPTH + 1);
            assertTrue("JsonMappingException message is as expected?",
                    e.getMessage().startsWith(exceptionPrefix));
        }
    }

    public void testDeeplyNestedArraysWithUnconstrainedMapper() throws Exception
    {
        final int depth = 750;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        genDeepArrayDoc(out, depth);
        CBORFactory cborFactory = CBORFactory.builder()
                .streamReadConstraints(StreamReadConstraints.builder().maxNestingDepth(Integer.MAX_VALUE).build())
                .build();
        try (JsonParser jp = cborParser(cborFactory, out)) {
            JsonToken jt;
            while ((jt = jp.nextToken()) != null) {

            }
        }
    }

    private void genDeepDoc(final ByteArrayOutputStream out, final int depth) throws IOException {
        CBORFactory cborFactory = cborFactoryBuilder()
                .streamWriteConstraints(StreamWriteConstraints.builder().maxNestingDepth(Integer.MAX_VALUE).build())
                .build();
        try (JsonGenerator gen = cborGenerator(cborFactory, out)) {
            for (int i = 0; i < depth; i++) {
                gen.writeStartObject();
                gen.writeFieldName("a");
            }
            gen.writeString("val");
            for (int i = 0; i < depth; i++) {
                gen.writeEndObject();
            }
        }
    }

    private void genDeepArrayDoc(final ByteArrayOutputStream out, final int depth) throws IOException {
        CBORFactory cborFactory = cborFactoryBuilder()
                .streamWriteConstraints(StreamWriteConstraints.builder().maxNestingDepth(Integer.MAX_VALUE).build())
                .build();
        try (JsonGenerator gen = cborGenerator(cborFactory, out)) {
            for (int i = 0; i < depth; i++) {
                gen.writeStartObject();
                gen.writeFieldName("a");
                gen.writeStartArray();
            }
            gen.writeString("val");
            for (int i = 0; i < depth; i++) {
                gen.writeEndArray();
                gen.writeEndObject();
            }
        }
    }
}
