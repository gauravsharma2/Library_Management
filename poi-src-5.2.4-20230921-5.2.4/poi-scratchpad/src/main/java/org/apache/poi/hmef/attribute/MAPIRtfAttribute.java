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

package org.apache.poi.hmef.attribute;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;
import org.apache.poi.hmef.Attachment;
import org.apache.poi.hmef.CompressedRTF;
import org.apache.poi.hmef.HMEFMessage;
import org.apache.poi.hsmf.datatypes.MAPIProperty;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.StringUtil;

/**
 * A pure-MAPI attribute holding RTF (compressed or not), which applies
 *  to a {@link HMEFMessage} or one of its {@link Attachment}s.
 */
public final class MAPIRtfAttribute extends MAPIAttribute {

   //arbitrarily selected; may need to increase
   private static final int MAX_RECORD_LENGTH = 50_000_000;

   private final byte[] decompressed;
   private final String data;

   public MAPIRtfAttribute(MAPIProperty property, int type, byte[] data) throws IOException {
      super(property, type, data);

      // Decompress it, removing any trailing padding as needed
      CompressedRTF rtf = new CompressedRTF();
      byte[] tmp;
      try (InputStream is = UnsynchronizedByteArrayInputStream.builder().setByteArray(data).get()) {
         tmp = rtf.decompress(is);
      }

      if(tmp.length > rtf.getDeCompressedSize()) {
         this.decompressed = IOUtils.safelyClone(tmp, 0, rtf.getDeCompressedSize(), MAX_RECORD_LENGTH);
      } else {
         this.decompressed = tmp;
      }

      // Turn the RTF data into a more useful string
      this.data = StringUtil.getFromCompressedUnicode(decompressed, 0, decompressed.length);
   }

   /**
    * Returns the original, compressed RTF
    */
   public byte[] getRawData() {
      return super.getData();
   }

   /**
    * Returns the raw uncompressed RTF data
    */
   public byte[] getData() {
      return decompressed;
   }

   /**
    * Returns the uncompressed RTF as a string
    */
   public String getDataString() {
      return data;
   }

   public String toString() {
      return getProperty() + " " + data;
   }
}
