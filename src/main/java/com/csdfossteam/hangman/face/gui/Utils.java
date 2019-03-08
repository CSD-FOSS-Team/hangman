package com.csdfossteam.hangman.face.gui;

/*

 * Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved.

 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.

 *

 * This code is free software; you can redistribute it and/or modify it

 * under the terms of the GNU General Public License version 2 only, as

 * published by the Free Software Foundation.  Oracle designates this

 * particular file as subject to the "Classpath" exception as provided

 * by Oracle in the LICENSE file that accompanied this code.

 *

 * This code is distributed in the hope that it will be useful, but WITHOUT

 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or

 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License

 * version 2 for more details (a copy is included in the LICENSE file that

 * accompanied this code).

 *

 * You should have received a copy of the GNU General Public License version

 * 2 along with this work; if not, write to the Free Software Foundation,

 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.

 *

 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA

 * or visit www.oracle.com if you need additional information or have any

 * questions.

 */

/*

 * To change this template, choose Tools | Templates

 * and open the template in the editor.

 */


import static javafx.scene.control.OverrunStyle.*;

import javafx.scene.control.OverrunStyle;

import javafx.scene.text.Font;

import javafx.scene.text.Text;

public class Utils {


    static Text helper = new Text();


    static double computeTextWidth(Font font, String text, double wrappingWidth) {

        helper.setText(text);

        helper.setFont(font);

        // Note that the wrapping width needs to be set to zero before

        // getting the text's real preferred width.

        helper.setWrappingWidth(0);

        double w = Math.min(helper.prefWidth(-1), wrappingWidth);

        helper.setWrappingWidth((int) Math.ceil(w));

        return Math.ceil(helper.getLayoutBounds().getWidth());

    }


    static double computeTextHeight(Font font, String text, double wrappingWidth) {

        helper.setText(text);

        helper.setFont(font);

        helper.setWrappingWidth((int) wrappingWidth);

        return helper.getLayoutBounds().getHeight();

    }


    static String computeClippedText(Font font, String text, double width,

                                     OverrunStyle type, String ellipsisString) {

        if (font == null) {

            throw new IllegalArgumentException("Must specify a font");

        }

        OverrunStyle style = (type == null || OverrunStyle.CLIP.equals(type)) ? (OverrunStyle.ELLIPSIS) : (type);

        String ellipsis = (style == CLIP) ? "" : ellipsisString;

        // if the text is empty or null or no ellipsis, then it always fits

        if (text == null || "".equals(text)) {

            return text;

        }

        // if the string width is < the available width, then it fits and

        // doesn't need to be clipped.  We use a double point comparison

        // of 0.001 (1/1000th of a pixel) to account for any numerical

        // discrepancies introduced when the available width was calculated.

        // MenuItemSkinBase.doLayout, for example, does a number of double

        // point operations when computing the available width.

        final double stringWidth = computeTextWidth(font, text, 0);

        if (stringWidth - width < 0.0010F) {

            return text;

        }

        // the width used by the ellipsis string

        final double ellipsisWidth = computeTextWidth(font, ellipsis, 0);

        // the available maximum width to fit chars into. This is essentially

        // the width minus the space required for the E ellipsis string

        final double availableWidth = width - ellipsisWidth;


        if (width < ellipsisWidth) {

            // The ellipsis doesn't fit.

            return "";

        }


        // if we got here, then we must clip the text with an ellipsis.

        // this can be pretty expensive depending on whether "complex" text

        // layout needs to be taken into account. So each ellipsis option has

        // to take into account two code paths: the easy way and the correct

        // way. This is flagged by the "complexLayout" boolean

        // TODO make sure this function call takes into account ligatures, kerning,

        // and such as that will change the layout characteristics of the text

        // and will require a full complex layout

        // TODO since we don't have all the stuff available in FX to determine

        // complex text, I'm going to for now assume complex text is always false.

        final boolean complexLayout = false;

        //requiresComplexLayout(font, text);


        // generally all we want to do is count characters and add their widths.

        // For ellipses which break on words, we do NOT want to include any

        // hanging whitespace.

        if (style.equals(OverrunStyle.ELLIPSIS) ||

                style.equals(OverrunStyle.WORD_ELLIPSIS) ||

                style.equals(OverrunStyle.LEADING_ELLIPSIS) ||

                style.equals(OverrunStyle.LEADING_WORD_ELLIPSIS)) {


            final boolean wordTrim = OverrunStyle.WORD_ELLIPSIS.equals(style) || OverrunStyle.LEADING_WORD_ELLIPSIS.equals(style);

            String substring;

            if (complexLayout) {

            } else //            AttributedString a = new AttributedString(text);

            //            LineBreakMeasurer m = new LineBreakMeasurer(a.getIterator(), frc);

            //            substring = text.substring(0, m.nextOffset((double)availableWidth));

            {

                // simply total up the widths of all chars to determine how many

                // will fit in the available space. Remember the last whitespace

                // encountered so that if we're breaking on words we can trim

                // and omit it.

                double total = 0.0F;

                int whitespaceIndex = -1;

                // at the termination of the loop, index will be one past the

                // end of the substring

                int index = 0;

                int start = (style.equals(OverrunStyle.LEADING_ELLIPSIS) || style.equals(OverrunStyle.LEADING_WORD_ELLIPSIS)) ? (text.length() - 1) : (0);

                int end = (start == 0) ? (text.length() - 1) : (0);

                int stepValue = (start == 0) ? (1) : (-1);

                boolean done = start == 0 ? start > end : start < end;

                for (int i = start; !done; i += stepValue) {

                    index = i;

                    char c = text.charAt(index);

                    total = computeTextWidth(font,

                            (start == 0) ? text.substring(0, i + 1)

                                    : text.substring(i, start + 1),

                            0);

                    if (Character.isWhitespace(c)) {

                        whitespaceIndex = index;

                    }

                    if (total > availableWidth) {

                        break;

                    }

                    done = start == 0 ? i >= end : i <= end;

                }

                final boolean fullTrim = !wordTrim || whitespaceIndex == -1;

                substring = (start == 0) ?

                        (text.substring(0, (fullTrim) ? (index) : (whitespaceIndex))) :

                        (text.substring(((fullTrim) ? (index) : (whitespaceIndex)) + 1));


            }

            if (OverrunStyle.ELLIPSIS.equals(style) || OverrunStyle.WORD_ELLIPSIS.equals(style)) {

                return substring + ellipsis;

            } else {

                //style == LEADING_ELLIPSIS or style == LEADING_WORD_ELLIPSIS

                return ellipsis + substring;

            }

        } else {

            // these two indexes are INCLUSIVE not exclusive

            int leadingIndex = 0;

            int trailingIndex = 0;

            int leadingWhitespace = -1;

            int trailingWhitespace = -1;

            // The complex case is going to be killer. What I have to do is

            // read all the chars from the left up to the leadingIndex,

            // and all the chars from the right up to the trailingIndex,

            // and sum those together to get my total. That is, I cannot have

            // a running total but must retotal the cummulative chars each time

            if (complexLayout) {

            } else /*            double leadingTotal = 0;

               double trailingTotal = 0;

               for (int i=0; i<text.length(); i++) {

               double total = computeStringWidth(metrics, text.substring(0, i));

               if (total + trailingTotal > availableWidth) break;

               leadingIndex = i;

               leadingTotal = total;

               if (Character.isWhitespace(text.charAt(i))) leadingWhitespace = leadingIndex;


               int index = text.length() - (i + 1);

               total = computeStringWidth(metrics, text.substring(index - 1));

               if (total + leadingTotal > availableWidth) break;

               trailingIndex = index;

               trailingTotal = total;

               if (Character.isWhitespace(text.charAt(index))) trailingWhitespace = trailingIndex;

               }*/ {

                // either CENTER_ELLIPSIS or CENTER_WORD_ELLIPSIS

                // for this case I read one char on the left, then one on the end

                // then second on the left, then second from the end, etc until

                // I have used up all the availableWidth. At that point, I trim

                // the string twice: once from the start to firstIndex, and

                // once from secondIndex to the end. I then insert the ellipsis

                // between the two.

                leadingIndex = -1;

                trailingIndex = -1;

                double total = 0.0F;

                for (int i = 0; i <= text.length() - 1; i++) {

                    char c = text.charAt(i);

                    //total += metrics.charWidth(c);

                    total += computeTextWidth(font, "" + c, 0);

                    if (total > availableWidth) {

                        break;

                    }

                    leadingIndex = i;

                    if (Character.isWhitespace(c)) {

                        leadingWhitespace = leadingIndex;

                    }

                    int index = text.length() - 1 - i;

                    c = text.charAt(index);

                    //total += metrics.charWidth(c);

                    total += computeTextWidth(font, "" + c, 0);

                    if (total > availableWidth) {

                        break;

                    }

                    trailingIndex = index;

                    if (Character.isWhitespace(c)) {

                        trailingWhitespace = trailingIndex;

                    }

                }

            }

            if (leadingIndex < 0) {

                return ellipsis;

            }

            if (OverrunStyle.CENTER_ELLIPSIS.equals(style)) {

                if (trailingIndex < 0) {

                    return text.substring(0, leadingIndex + 1) + ellipsis;

                }

                return text.substring(0, leadingIndex + 1) + ellipsis + text.substring(trailingIndex);

            } else {

                boolean leadingIndexIsLastLetterInWord =

                        Character.isWhitespace(text.charAt(leadingIndex + 1));

                int index = (leadingWhitespace == -1 || leadingIndexIsLastLetterInWord) ? (leadingIndex + 1) : (leadingWhitespace);

                String leading = text.substring(0, index);

                if (trailingIndex < 0) {

                    return leading + ellipsis;

                }

                boolean trailingIndexIsFirstLetterInWord =

                        Character.isWhitespace(text.charAt(trailingIndex - 1));

                index = (trailingWhitespace == -1 || trailingIndexIsFirstLetterInWord) ? (trailingIndex) : (trailingWhitespace + 1);

                String trailing = text.substring(index);

                return leading + ellipsis + trailing;

            }

        }

    }
}

