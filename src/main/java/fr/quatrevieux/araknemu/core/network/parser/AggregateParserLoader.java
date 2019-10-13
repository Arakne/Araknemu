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
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.core.network.parser;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Aggregate of parser loaders
 */
final public class AggregateParserLoader implements ParserLoader {
    final private ParserLoader[] loaders;

    public AggregateParserLoader(ParserLoader[] loaders) {
        this.loaders = loaders;
    }

    @Override
    public Collection<SinglePacketParser> load() {
        Collection<SinglePacketParser> parsers = new ArrayList<>();

        for (ParserLoader loader : loaders) {
            parsers.addAll(loader.load());
        }

        return parsers;
    }
}
