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

package org.apache.poi.hslf.record;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.junit.jupiter.api.Test;

/**
 * Tests that {@link HeadersFootersAtom} works properly
 */
public final class TestAnimationInfoAtom {
    // From a real file
    /*
     <AnimationInfoAtom info="1" type="4081" size="28" offset="4015" header="01 00 F1 0F 1C 00 00 00 ">
       00 00 00 07 04 05 00 00 00 00 00 00 00 00 00 00 02 00 00 00 00 00 00 00 00
       00 00 00
     </AnimationInfoAtom>
     */
    private final byte[] data = {
         0x01, 0x00, (byte)0xF1, 0x0F, 0x1C, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x07, 0x04, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    };

    @Test
    void testRead() {
        AnimationInfoAtom record = new AnimationInfoAtom(data, 0, data.length);
        assertEquals(RecordTypes.AnimationInfoAtom.typeID, record.getRecordType());
        assertTrue(record.getFlag(AnimationInfoAtom.Automatic));
        assertTrue(record.getFlag(AnimationInfoAtom.Play));
        assertTrue(record.getFlag(AnimationInfoAtom.Synchronous));
        assertFalse(record.getFlag(AnimationInfoAtom.Reverse));
        assertFalse(record.getFlag(AnimationInfoAtom.Sound));
        assertFalse(record.getFlag(AnimationInfoAtom.StopSound));
        assertFalse(record.getFlag(AnimationInfoAtom.Hide));
        assertFalse(record.getFlag(AnimationInfoAtom.AnimateBg));
        assertEquals(0x07000000, record.getDimColor());
        assertEquals(0, record.getSoundIdRef());
        assertEquals(0, record.getDelayTime());
        assertEquals(2, record.getOrderID());
        assertEquals(0, record.getSlideCount());
    }

    @Test
    void testWrite() throws Exception {
        AnimationInfoAtom record = new AnimationInfoAtom(data, 0, data.length);
        UnsynchronizedByteArrayOutputStream baos = UnsynchronizedByteArrayOutputStream.builder().get();
        record.writeOut(baos);
        byte[] b = baos.toByteArray();

        assertArrayEquals(data, b);
    }

    @Test
    void testNewRecord() throws Exception {
        AnimationInfoAtom record = new AnimationInfoAtom();
        record.setDimColor(0x07000000);
        record.setOrderID(2);
        record.setFlag(AnimationInfoAtom.Automatic, true);
        record.setFlag(AnimationInfoAtom.Play, true);
        record.setFlag(AnimationInfoAtom.Synchronous, true);

        UnsynchronizedByteArrayOutputStream baos = UnsynchronizedByteArrayOutputStream.builder().get();
        record.writeOut(baos);
        byte[] b = baos.toByteArray();

        assertArrayEquals(data, b);
    }
}
