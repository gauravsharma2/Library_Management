/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package org.apache.poi.poifs.property;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.poifs.storage.RawDataUtil;
import org.apache.poi.util.LocaleUtil;
import org.junit.jupiter.api.Test;

/**
 * Class to test DocumentProperty functionality
 */
final class TestDocumentProperty {

    @Test
    void testConstructor() throws IOException {
        // test with short name, small file
        verifyProperty("foo", 1234);

        // test with just long enough name, small file
        verifyProperty("A.really.long.long.long.name123", 2345);

        // test with longer name, just small enough file
        verifyProperty("A.really.long.long.long.name1234", 4095);

        // test with just long enough file
        verifyProperty("A.really.long.long.long.name123", 4096);
    }

    @Test
    void testReadingConstructor() throws IOException {
        String[] hexData = {
            "52 00 6F 00 6F 00 74 00 20 00 45 00 6E 00 74 00 72 00 79 00 00 00 00 00 00 00 00 00 00 00 00 00",
            "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
            "16 00 05 01 FF FF FF FF FF FF FF FF 02 00 00 00 20 08 02 00 00 00 00 00 C0 00 00 00 00 00 00 46",
            "00 00 00 00 00 00 00 00 00 00 00 00 C0 5C E8 23 9E 6B C1 01 FE FF FF FF 00 00 00 00 00 00 00 00",
            "57 00 6F 00 72 00 6B 00 62 00 6F 00 6F 00 6B 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
            "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
            "12 00 02 01 FF FF FF FF FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
            "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 10 00 00 00 00 00 00",
            "05 00 53 00 75 00 6D 00 6D 00 61 00 72 00 79 00 49 00 6E 00 66 00 6F 00 72 00 6D 00 61 00 74 00",
            "69 00 6F 00 6E 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
            "28 00 02 01 01 00 00 00 03 00 00 00 FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
            "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 08 00 00 00 00 10 00 00 00 00 00 00",
            "05 00 44 00 6F 00 63 00 75 00 6D 00 65 00 6E 00 74 00 53 00 75 00 6D 00 6D 00 61 00 72 00 79 00",
            "49 00 6E 00 66 00 6F 00 72 00 6D 00 61 00 74 00 69 00 6F 00 6E 00 00 00 00 00 00 00 00 00 00 00",
            "38 00 02 01 FF FF FF FF FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
            "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 10 00 00 00 00 10 00 00 00 00 00 00",
        };
        byte[] input = RawDataUtil.decode(hexData);

        verifyReadingProperty(1, input, 128, "Workbook");
        verifyReadingProperty(2, input, 256, SummaryInformation.DEFAULT_STREAM_NAME);
        verifyReadingProperty(3, input, 384, DocumentSummaryInformation.DEFAULT_STREAM_NAME);
    }

    private void verifyReadingProperty(int index, byte[] input, int offset, String name)
            throws IOException {
        DocumentProperty      property = new DocumentProperty(index, input,
                                             offset);
        UnsynchronizedByteArrayOutputStream stream   = UnsynchronizedByteArrayOutputStream.builder().setBufferSize(128).get();
        byte[]                expected = Arrays.copyOfRange(input, offset, offset+128);
        property.writeData(stream);
        byte[] output = stream.toByteArray();

        assertEquals(128, output.length);
        assertArrayEquals(expected, output);
        assertEquals(index, property.getIndex());
        assertEquals(name, property.getName());
    }

    private void verifyProperty(String name, int size) throws IOException {
        DocumentProperty property = new DocumentProperty(name, size);

        if (size >= 4096)
        {
            assertFalse(property.shouldUseSmallBlocks());
        }
        else
        {
            assertTrue(property.shouldUseSmallBlocks());
        }
        byte[] testblock = new byte[ 128 ];
        int    index     = 0;

        for (; index < 0x40; index++)
        {
            testblock[ index ] = ( byte ) 0;
        }
        int limit = Math.min(31, name.length());

        testblock[ index++ ] = ( byte ) (2 * (limit + 1));
        testblock[ index++ ] = ( byte ) 0;
        testblock[ index++ ] = ( byte ) 2;
        testblock[ index++ ] = ( byte ) 1;
        for (; index < 0x50; index++)
        {
            testblock[ index ] = ( byte ) 0xFF;
        }
        for (; index < 0x78; index++)
        {
            testblock[ index ] = ( byte ) 0;
        }
        int sz = size;

        testblock[ index++ ] = ( byte ) sz;
        sz                   /= 256;
        testblock[ index++ ] = ( byte ) sz;
        sz                   /= 256;
        testblock[ index++ ] = ( byte ) sz;
        sz                   /= 256;
        testblock[ index++ ] = ( byte ) sz;
        for (; index < 0x80; index++)
        {
            testblock[ index ] = ( byte ) 0x0;
        }
        byte[] name_bytes = name.getBytes(LocaleUtil.CHARSET_1252);

        for (index = 0; index < limit; index++)
        {
            testblock[ index * 2 ] = name_bytes[ index ];
        }
        UnsynchronizedByteArrayOutputStream stream = UnsynchronizedByteArrayOutputStream.builder().setBufferSize(512).get();

        property.writeData(stream);
        byte[] output = stream.toByteArray();
        assertArrayEquals(testblock, output);
    }
}
