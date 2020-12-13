/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.util;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for escape string
 */
final public class Escape {
    // Replacement pairs
    // The two firsts pairs are used to ignore substrings :
    // the client already escape < and > when sending message, so ignore &lt; and &gt; to ensure that sending ">" will not be displayed as "&gt;"
    final static private String[] TO_ESCAPE = new String[] {"&lt;", "&gt;", "<", ">", "&", "|"};
    final static private String[] REPLACEMENT = new String[] {"&lt;", "&gt;", "&lt;", "&gt;", "&amp;", ""};

    /**
     * Escape HTML chars
     * Note: "&lt;" and "&gt;" sequences are ignored due to client side encoding
     *
     * @param value Value to escape
     *
     * @return Escaped (safe) value
     */
    static public String html(String value) {
        return StringUtils.replaceEach(value, TO_ESCAPE, REPLACEMENT);
    }

    /**
     * Perform URL encode
     *
     * @param value Value to encode
     *
     * @return Encoded value
     */
    static public String url(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
