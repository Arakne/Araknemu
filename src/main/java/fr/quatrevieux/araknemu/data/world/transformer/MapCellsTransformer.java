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

package fr.quatrevieux.araknemu.data.world.transformer;

import fr.arakne.utils.maps.serializer.CellData;
import fr.arakne.utils.maps.serializer.DefaultMapDataSerializer;
import fr.arakne.utils.maps.serializer.MapDataSerializer;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import org.checkerframework.checker.nullness.qual.PolyNull;

/**
 * Transformer for map cell
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/ank/battlefield/utils/Compressor.as#L54
 */
public final class MapCellsTransformer implements Transformer<CellData[]> {
    private final MapDataSerializer serializer;

    public MapCellsTransformer() {
        final DefaultMapDataSerializer serializer = new DefaultMapDataSerializer();

        serializer.enableCache();

        this.serializer = serializer;
    }

    @Override
    public @PolyNull String serialize(CellData @PolyNull[] value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CellData @PolyNull[] unserialize(@PolyNull String serialize) {
        return serialize == null ? null : serializer.deserialize(serialize);
    }
}
