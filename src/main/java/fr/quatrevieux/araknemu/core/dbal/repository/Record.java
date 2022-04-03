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
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.core.dbal.repository;

import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.util.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.index.qual.GTENegativeOne;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.OptionalInt;

/**
 * Wrap {@link java.sql.ResultSet} instance for provide helpers methods
 */
public final class Record {
    private final ResultSet resultSet;

    public Record(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    /**
     * Get column value as string
     * Unlike ResultSet's method, null values are not allowed
     *
     * @param column The column name
     *
     * @throws SQLException If the columnLabel is not valid; if a database access error occurs or this method is called on a closed result set
     * @throws RecordException If the value is null
     *
     * @see ResultSet#getString(String) The called method
     */
    public String getString(String column) throws SQLException {
        final String value = resultSet.getString(column);

        if (value == null) {
            throw new RecordException("The column " + column + " cannot be null");
        }

        return value;
    }

    /**
     * Get column value as string
     * The value can be null
     *
     * @param column The column name
     *
     * @throws SQLException If the columnLabel is not valid; if a database access error occurs or this method is called on a closed result set
     *
     * @see ResultSet#getString(String) The called method
     */
    public @Nullable String getNullableString(String column) throws SQLException {
        return resultSet.getString(column);
    }

    /**
     * Parse column value using a transformer
     * Null values are not allowed
     *
     * @param column The column name
     * @param transformer Transformer to call
     *
     * @param <T> The result type
     *
     * @return The unserialized value
     *
     * @throws SQLException If the columnLabel is not valid; if a database access error occurs or this method is called on a closed result set
     * @throws RecordException When the transformer failed to parse the value, or if the value is null
     */
    public <T> @NonNull T unserialize(String column, Transformer<T> transformer) throws SQLException {
        final String value = getString(column);

        try {
            return transformer.unserialize(value);
        } catch (IllegalArgumentException e) {
            throw new RecordException("Invalid value " + value + " for column " + column, e);
        }
    }

    /**
     * Parse column value using a transformer
     * Null values are allowed
     *
     * @param column The column name
     * @param transformer Transformer to call
     *
     * @param <T> The result type
     *
     * @return The unserialized value
     *
     * @throws SQLException If the columnLabel is not valid; if a database access error occurs or this method is called on a closed result set
     * @throws RecordException When the transformer failed to parse the value
     */
    public <T> @Nullable T nullableUnserialize(String column, Transformer<T> transformer) throws SQLException {
        final String value = resultSet.getString(column);

        try {
            return transformer.unserialize(value);
        } catch (IllegalArgumentException e) {
            throw new RecordException("Invalid value " + value + " for column " + column, e);
        }
    }

    /**
     * Get column value as integer
     *
     * @param column The column name
     *
     * @throws SQLException If the columnLabel is not valid; if a database access error occurs or this method is called on a closed result set
     *
     * @see ResultSet#getInt(String) The called method
     */
    public int getInt(String column) throws SQLException {
        return resultSet.getInt(column);
    }

    /**
     * Get column value as integer wrapped into an optional
     * When a null value is found, an empty optional is returned, otherwise the value is wrapped into an optional
     *
     * @param column The column name
     *
     * @throws SQLException If the columnLabel is not valid; if a database access error occurs or this method is called on a closed result set
     *
     * @see ResultSet#getInt(String) The called method
     */
    public OptionalInt getOptionalInt(String column) throws SQLException {
        final int value = resultSet.getInt(column);

        return resultSet.wasNull() ? OptionalInt.empty() : OptionalInt.of(value);
    }

    /**
     * Get column value as non negative integer or -1
     *
     * @param column The column name
     *
     * @throws SQLException If the columnLabel is not valid; if a database access error occurs or this method is called on a closed result set
     * @throws RecordException When an invalid value is found
     *
     * @see ResultSet#getInt(String) The called method
     */
    public @GTENegativeOne int getGTENegativeOneInt(String column) throws SQLException {
        final int value = resultSet.getInt(column);

        if (value < -1) {
            throw new RecordException("Column " + column + " must be higher than or equal -1");
        }

        return value;
    }

    /**
     * Get column value as non negative integer
     *
     * @param column The column name
     *
     * @throws SQLException If the columnLabel is not valid; if a database access error occurs or this method is called on a closed result set
     * @throws RecordException When a negative value is found
     *
     * @see ResultSet#getInt(String) The called method
     */
    public @NonNegative int getNonNegativeInt(String column) throws SQLException {
        final int value = resultSet.getInt(column);

        if (value < 0) {
            throw new RecordException("Column " + column + " do not accept negative value");
        }

        return value;
    }

    /**
     * Get column value as positive (i.e. >= 1) integer
     *
     * @param column The column name
     *
     * @throws SQLException If the columnLabel is not valid; if a database access error occurs or this method is called on a closed result set
     * @throws RecordException When a negative value or zero is found
     *
     * @see ResultSet#getInt(String) The called method
     */
    public @Positive int getPositiveInt(String column) throws SQLException {
        final int value = resultSet.getInt(column);

        if (value < 1) {
            throw new RecordException("Column " + column + " expect a positive value");
        }

        return value;
    }

    /**
     * Get column value as long
     *
     * @param column The column name
     *
     * @throws SQLException If the columnLabel is not valid; if a database access error occurs or this method is called on a closed result set
     *
     * @see ResultSet#getLong(String) The called method
     */
    public long getLong(String column) throws SQLException {
        return resultSet.getLong(column);
    }

    /**
     * Get column value as non negative long
     *
     * @param column The column name
     *
     * @throws SQLException If the columnLabel is not valid; if a database access error occurs or this method is called on a closed result set
     * @throws RecordException When a negative value is found
     *
     * @see ResultSet#getLong(String) The called method
     */
    public @NonNegative long getNonNegativeLong(String column) throws SQLException {
        final long value = resultSet.getLong(column);

        if (value < 0) {
            throw new RecordException("Column " + column + " do not accept negative value");
        }

        return value;
    }

    /**
     * Parse column value as array index, and return the corresponding value from the array
     *
     * @param column The column name
     * @param array Array to extract from
     *
     * @throws SQLException If the columnLabel is not valid; if a database access error occurs or this method is called on a closed result set
     * @throws RecordException When an invalid index value is found
     *
     * @see ResultSet#getInt(String) The called method
     */
    public <T> T getArrayValue(String column, T[] array) throws SQLException {
        final int index = resultSet.getInt(column);

        if (index < 0 || index >= array.length) {
            throw new RecordException("Column " + column + " must be an index of array of length " + array.length);
        }

        return array[index];
    }

    /**
     * Get column value as boolean
     *
     * @param column The column name
     *
     * @throws SQLException If the columnLabel is not valid; if a database access error occurs or this method is called on a closed result set
     *
     * @see ResultSet#getBoolean(String) The called method
     */
    public boolean getBoolean(String column) throws SQLException {
        return resultSet.getBoolean(column);
    }

    /**
     * Get column value as double
     *
     * @param column The column name
     *
     * @throws SQLException If the columnLabel is not valid; if a database access error occurs or this method is called on a closed result set
     *
     * @see ResultSet#getDouble(String) The called method
     */
    public double getDouble(String column) throws SQLException {
        return resultSet.getDouble(column);
    }

    /**
     * Get column value as string and split using a separator
     * Null values are not allowed
     *
     * @param column The column name
     * @param separator The values separator
     *
     * @throws SQLException If the columnLabel is not valid; if a database access error occurs or this method is called on a closed result set
     * @throws RecordException If the value is null
     *
     * @see ResultSet#getString(String) The called method
     */
    public String[] getCsvArray(String column, char separator) throws SQLException {
        return StringUtils.splitPreserveAllTokens(getString(column), separator);
    }

    /**
     * Parse column value as array of int, split using a separator
     * Do not allow null values
     *
     * @param column The column name
     * @param separator The values separator
     *
     * @return Parsed array of ints
     *
     * @throws SQLException If the columnLabel is not valid; if a database access error occurs or this method is called on a closed result set
     * @throws RecordException If the value is null, or contains invalid integers
     */
    public int[] getIntArray(String column, char separator) throws SQLException {
        final String csv = getString(column);

        // Optimisation: do not instantiate splitter if value is empty
        if (csv.isEmpty()) {
            return new int[0];
        }

        try {
            return new Splitter(csv, separator).toIntArray();
        } catch (IllegalArgumentException e) {
            throw new RecordException("Invalid int value for column " + column);
        }
    }

    /**
     * Parse column value as array of int, split using a separator
     * Do allow null values
     *
     * @param column The column name
     * @param separator The values separator
     *
     * @return Parsed array of ints
     *
     * @throws SQLException If the columnLabel is not valid; if a database access error occurs or this method is called on a closed result set
     * @throws RecordException If the value contains invalid integers
     */
    public int @Nullable [] getNullableIntArray(String column, char separator) throws SQLException {
        final String csv = resultSet.getString(column);

        if (csv == null) {
            return null;
        }

        // Optimisation: do not instantiate splitter if value is empty
        if (csv.isEmpty()) {
            return new int[0];
        }

        try {
            return new Splitter(csv, separator).toIntArray();
        } catch (IllegalArgumentException e) {
            throw new RecordException("Invalid int value for column " + column);
        }
    }
}
