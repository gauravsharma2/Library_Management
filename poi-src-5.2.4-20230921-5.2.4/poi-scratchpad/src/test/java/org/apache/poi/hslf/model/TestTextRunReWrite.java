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

package org.apache.poi.hslf.model;

import static org.apache.poi.POIDataSamples.writeOutAndReadBack;
import static org.apache.poi.hslf.HSLFTestDataSamples.getSlideShow;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;

import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.hslf.usermodel.HSLFTextRun;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.junit.jupiter.api.Test;

/**
 * Tests that if we load something up, get a TextRun, set the text
 *  to be the same as it was before, and write it all back out again,
 *  that we don't break anything in the process.
 */
public final class TestTextRunReWrite {
    @Test
    void testWritesOutTheSameNonRich() throws IOException {
        try (HSLFSlideShow ppt1 = getSlideShow("Single_Coloured_Page_With_Fonts_and_Alignments.ppt")) {
            // Ensure the text lengths are as we'd expect to start with
            assertEquals(1, ppt1.getSlides().size());
            assertEquals(2, ppt1.getSlides().get(0).getTextParagraphs().size());

            // Grab the first text run on the first sheet
            List<HSLFTextParagraph> tr1 = ppt1.getSlides().get(0).getTextParagraphs().get(0);
            List<HSLFTextParagraph> tr2 = ppt1.getSlides().get(0).getTextParagraphs().get(1);


            assertEquals(30, HSLFTextParagraph.getRawText(tr1).length());
            assertEquals(179, HSLFTextParagraph.getRawText(tr2).length());

            assertEquals(1, tr1.size());
            assertEquals(30, HSLFTextParagraph.getText(tr1).length());
            assertEquals(30, HSLFTextParagraph.getRawText(tr1).length());
            assertEquals(31, tr1.get(0).getTextRuns().get(0).getCharacterStyle().getCharactersCovered());
            assertEquals(31, tr1.get(0).getParagraphStyle().getCharactersCovered());

            // Set the text to be as it is now
            HSLFTextParagraph.setText(tr1, HSLFTextParagraph.getRawText(tr1));
            tr1 = ppt1.getSlides().get(0).getTextParagraphs().get(0);

            // Check the text lengths are still right
            assertEquals(30, HSLFTextParagraph.getRawText(tr1).length());
            assertEquals(179, HSLFTextParagraph.getRawText(tr2).length());

            assertEquals(1, tr1.size());
            assertEquals(30, HSLFTextParagraph.getText(tr1).length());
            assertEquals(30, HSLFTextParagraph.getRawText(tr1).length());
            assertEquals(31, tr1.get(0).getTextRuns().get(0).getCharacterStyle().getCharactersCovered());
            assertEquals(31, tr1.get(0).getParagraphStyle().getCharactersCovered());

            // Use POIFS to query that lot
            try (POIFSFileSystem npfs = writeOutAndReadBack(ppt1.getDirectory().getFileSystem())) {
                // Check that the "PowerPoint Document" sections have the same size
                DirectoryNode oDir = ppt1.getSlideShowImpl().getDirectory();

                DocumentEntry oProps = (DocumentEntry) oDir.getEntryCaseInsensitive(HSLFSlideShow.POWERPOINT_DOCUMENT);
                DocumentEntry nProps = (DocumentEntry) npfs.getRoot().getEntryCaseInsensitive(HSLFSlideShow.POWERPOINT_DOCUMENT);
                assertEquals(oProps.getSize(), nProps.getSize());

                // Check that they contain the same data
                byte[] _oData = new byte[oProps.getSize()];
                byte[] _nData = new byte[nProps.getSize()];
                int oLen = oDir.createDocumentInputStream(HSLFSlideShow.POWERPOINT_DOCUMENT).read(_oData);
                int nLen = npfs.createDocumentInputStream(HSLFSlideShow.POWERPOINT_DOCUMENT).read(_nData);
                assertEquals(_oData.length, oLen);
                assertEquals(_nData.length, nLen);
                assertArrayEquals(_oData, _nData);
            }
        }
    }

    @Test
    void testWritesOutTheSameRich() throws IOException {
        try (HSLFSlideShow ppt1 = getSlideShow("Single_Coloured_Page_With_Fonts_and_Alignments.ppt")) {
            // Grab the first text run on the first sheet
            List<HSLFTextParagraph> tr1 = ppt1.getSlides().get(0).getTextParagraphs().get(0);

            // Get the first rich text run
            HSLFTextRun rtr1 = tr1.get(0).getTextRuns().get(0);

            // Check that the text sizes are as expected
            assertEquals(1, tr1.get(0).getTextRuns().size());
            assertEquals(30, HSLFTextParagraph.getRawText(tr1).length());
            assertEquals(30, rtr1.getLength());
            assertEquals(30, rtr1.getRawText().length());
            assertEquals(31, rtr1.getCharacterStyle().getCharactersCovered());
            assertEquals(31, tr1.get(0).getParagraphStyle().getCharactersCovered());

            // Set the text to be as it is now
            rtr1.setText(rtr1.getRawText());
            rtr1 = tr1.get(0).getTextRuns().get(0);

            // Check that the text sizes are still as expected
            assertEquals(1, tr1.get(0).getTextRuns().size());
            assertEquals(30, HSLFTextParagraph.getRawText(tr1).length());
            assertEquals(30, tr1.get(0).getTextRuns().get(0).getRawText().length());
            assertEquals(30, rtr1.getLength());
            assertEquals(30, rtr1.getRawText().length());
            assertEquals(31, rtr1.getCharacterStyle().getCharactersCovered());
            assertEquals(31, tr1.get(0).getParagraphStyle().getCharactersCovered());

            // Use POIFS to query that lot
            try (POIFSFileSystem npfs = writeOutAndReadBack(ppt1.getDirectory().getFileSystem())) {
                // Check that the "PowerPoint Document" sections have the same size
                DirectoryNode oDir = ppt1.getSlideShowImpl().getDirectory();

                DocumentEntry oProps = (DocumentEntry) oDir.getEntryCaseInsensitive(HSLFSlideShow.POWERPOINT_DOCUMENT);
                DocumentEntry nProps = (DocumentEntry) npfs.getRoot().getEntryCaseInsensitive(HSLFSlideShow.POWERPOINT_DOCUMENT);
                assertEquals(oProps.getSize(), nProps.getSize());

                // Check that they contain the same data
                byte[] _oData = new byte[oProps.getSize()];
                byte[] _nData = new byte[nProps.getSize()];

                int oLen = oDir.createDocumentInputStream(HSLFSlideShow.POWERPOINT_DOCUMENT).read(_oData);
                int nLen = npfs.createDocumentInputStream(HSLFSlideShow.POWERPOINT_DOCUMENT).read(_nData);
                assertEquals(_oData.length, oLen);
                assertEquals(_nData.length, nLen);
                assertArrayEquals(_oData, _nData);
            }
        }
    }
}
