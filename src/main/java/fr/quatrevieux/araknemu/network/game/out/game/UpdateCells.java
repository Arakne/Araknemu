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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.network.game.out.game;

import fr.arakne.utils.maps.constant.CellMovement;
import fr.arakne.utils.maps.serializer.CellData;
import fr.arakne.utils.maps.serializer.CellLayerData;
import fr.arakne.utils.maps.serializer.DefaultMapDataSerializer;
import fr.arakne.utils.maps.serializer.GroundCellData;
import fr.arakne.utils.maps.serializer.InteractiveObjectData;
import fr.arakne.utils.maps.serializer.MapDataSerializer;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Update map cells
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L974
 */
public final class UpdateCells {
    /**
     * @see CellData#layer2()
     * @see InteractiveObjectData#interactive()
     */
    public static final Property<Boolean> LAYER_2_OBJECT_INTERACTIVE = new Property<>(1, false);
    /**
     * @see CellData#layer2()
     * @see InteractiveObjectData#flip()
     */
    public static final Property<Boolean> LAYER_2_OBJECT_FLIP = new Property<>(2, false);
    /**
     * @see CellData#layer2()
     * @see InteractiveObjectData#number()
     */
    public static final Property<Integer> LAYER_2_OBJECT_NUMBER = new Property<>(4, 0);
    /**
     * @see CellData#layer1()
     * @see CellLayerData#rotation()
     */
    public static final Property<Integer> LAYER_1_OBJECT_ROTATION = new Property<>(8, 0);
    /**
     * @see CellData#layer1()
     * @see CellLayerData#flip()
     */
    public static final Property<Boolean> LAYER_1_OBJECT_FLIP = new Property<>(16, false);
    /**
     * @see CellData#layer1()
     * @see CellLayerData#number()
     */
    public static final Property<Integer> LAYER_1_OBJECT_NUMBER = new Property<>(32, 0);
    /**
     * @see CellData#ground()
     * @see GroundCellData#rotation()
     */
    public static final Property<Integer> GROUND_OBJECT_ROTATION = new Property<>(64, 0);
    /**
     * @see CellData#ground()
     * @see GroundCellData#flip()
     */
    public static final Property<Boolean> GROUND_OBJECT_FLIP = new Property<>(128, false);
    /**
     * @see CellData#ground()
     * @see GroundCellData#number()
     */
    public static final Property<Integer> GROUND_OBJECT_NUMBER = new Property<>(256, 0);
    /**
     * @see CellData#ground()
     * @see GroundCellData#slope()
     */
    public static final Property<Integer> GROUND_OBJECT_SLOPE = new Property<>(512, 0);
    /**
     * @see CellData#ground()
     * @see GroundCellData#level()
     */
    public static final Property<Integer> GROUND_OBJECT_LEVEL = new Property<>(1024, 0);
    /**
     * @see CellData#movement()
     */
    public static final Property<CellMovement> MOVEMENT = new Property<>(2048, CellMovement.NOT_WALKABLE);
    /**
     * @see CellData#lineOfSight()
     */
    public static final Property<Boolean> LINE_OF_SIGHT = new Property<>(4096, false);
    /**
     * @see CellData#active()
     */
    public static final Property<Boolean> ACTIVE = new Property<>(8192, false);

    /** Not handled by this packet */
    public static final int EXTERNAL_OBJECT = 16384;
    /** Not handled by this packet */
    public static final int EXTERNAL_OBJECT_INTERACTIVE = 32768;
    /** Not handled by this packet */
    public static final int EXTERNAL_OBJECT_AUTOSIZE = 65536;

    private static final MapDataSerializer SERIALIZER = new DefaultMapDataSerializer();

    private final Data[] cells;

    /**
     * Update client map cells
     *
     * Usage:
     * <code>
     *     // Cell 123 will be transformed to a non-walkable cell with no line of sight
     *     // Cell 421 will simply be updated to have an object number of 15
     *     new UpdateCells(
     *         UpdateCells.Data.fromProperties(
     *             124,
     *             true,
     *             UpdateCells.LINE_OF_SIGHT.set(false),
     *             UpdateCells.MOVEMENT.set(CellMovement.NOT_WALKABLE),
     *             UpdateCells.LAYER_1_OBJECT_NUMBER.set(123),
     *         ),
     *         UpdateCells.Data.fromProperties(
     *             421,
     *             true,
     *             UpdateCells.LAYER_2_OBJECT_NUMBER.set(15),
     *         ),
     *     );
     * </code>
     */
    public UpdateCells(Data... cells) {
        this.cells = cells;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(32 * cells.length);

        sb.append("GDC");

        boolean first = true;

        for (Data cell : cells) {
            if (first) {
                first = false;
            } else {
                sb.append('|');
            }

            sb.append(cell.id);

            if (cell.data != null) {
                sb
                    .append(';')
                    .append(SERIALIZER.serialize(new CellData[]{ cell.data }))
                    .append(Integer.toHexString(cell.mask)).append(';')
                    .append(cell.backup ? '1' : '0')
                ;
            }
        }

        return sb.toString();
    }

    public static class Data {
        private final int id;
        private final @Nullable CellData data;
        private final int mask;
        private final boolean backup;

        public Data(int id, @Nullable CellData data, int mask, boolean backup) {
            this.id = id;
            this.data = data;
            this.mask = mask;
            this.backup = backup;
        }

        public static Data reset(int id) {
            return new Data(id, null, 0, false);
        }

        /**
         * Create a cell data from a list of properties
         *
         * @param id The cell id
         * @param backup If true, the client will backup the cell data
         * @param values The properties to set
         */
        public static Data fromProperties(int id, boolean backup, PropertyValue<?>... values) {
            final Map<Integer, Object> valuesMap = new HashMap<>();
            int mask = 0;

            for (PropertyValue<?> value : values) {
                mask |= value.mask();
                valuesMap.put(value.mask(), value.value);
            }

            final CellData data = new MapCellData(valuesMap);

            return new Data(id, data, mask, backup);
        }
    }

    public static final class Property<T> {
        private final int mask;
        private final T defaultValue;

        private Property(int mask, T defaultValue) {
            this.mask = mask;
            this.defaultValue = defaultValue;
        }

        public int mask() {
            return mask;
        }

        public PropertyValue<T> set(T value) {
            return new PropertyValue<>(mask, value);
        }

        @SuppressWarnings("unchecked")
        public T get(Map<Integer, Object> map) {
            return (T) map.getOrDefault(mask, defaultValue);
        }
    }

    public static final class PropertyValue<T> {
        private final int mask;
        private final T value;

        private PropertyValue(int mask, T value) {
            this.mask = mask;
            this.value = value;
        }

        public int mask() {
            return mask;
        }

        public T value() {
            return value;
        }
    }

    private static final class MapCellData implements CellData {
        private final Map<Integer, Object> values;

        public MapCellData(Map<Integer, Object> values) {
            this.values = values;
        }

        @Override
        public boolean lineOfSight() {
            return LINE_OF_SIGHT.get(values);
        }

        @Override
        public CellMovement movement() {
            return MOVEMENT.get(values);
        }

        @Override
        public boolean active() {
            return ACTIVE.get(values);
        }

        @Override
        public GroundCellData ground() {
            return new GroundCellData() {
                @Override
                public int level() {
                    return GROUND_OBJECT_LEVEL.get(values);
                }

                @Override
                public int slope() {
                    return GROUND_OBJECT_SLOPE.get(values);
                }

                @Override
                public int number() {
                    return GROUND_OBJECT_NUMBER.get(values);
                }

                @Override
                public boolean flip() {
                    return GROUND_OBJECT_FLIP.get(values);
                }

                @Override
                public int rotation() {
                    return GROUND_OBJECT_ROTATION.get(values);
                }
            };
        }

        @Override
        public CellLayerData layer1() {
            return new CellLayerData() {
                @Override
                public int number() {
                    return LAYER_1_OBJECT_NUMBER.get(values);
                }

                @Override
                public boolean flip() {
                    return LAYER_1_OBJECT_FLIP.get(values);
                }

                @Override
                public int rotation() {
                    return LAYER_1_OBJECT_ROTATION.get(values);
                }
            };
        }

        @Override
        public InteractiveObjectData layer2() {
            return new InteractiveObjectData() {
                @Override
                public int number() {
                    return LAYER_2_OBJECT_NUMBER.get(values);
                }

                @Override
                public boolean flip() {
                    return LAYER_2_OBJECT_FLIP.get(values);
                }

                @Override
                public boolean interactive() {
                    return LAYER_2_OBJECT_INTERACTIVE.get(values);
                }
            };
        }
    }
}
