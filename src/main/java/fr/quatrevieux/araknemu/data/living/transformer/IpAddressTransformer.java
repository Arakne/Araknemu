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

package fr.quatrevieux.araknemu.data.living.transformer;

import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.transformer.TransformerException;
import inet.ipaddr.IPAddressString;

/**
 * Parse IP address string
 * Handle IPv4, IPv6 and net mask
 */
public final class IpAddressTransformer implements Transformer<IPAddressString> {
    @Override
    public String serialize(IPAddressString value) {
        return value.toNormalizedString();
    }

    @Override
    public IPAddressString unserialize(String serialize) throws TransformerException {
        return new IPAddressString(serialize);
    }
}
