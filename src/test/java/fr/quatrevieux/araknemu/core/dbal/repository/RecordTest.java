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

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.core.config.DefaultConfiguration;
import fr.quatrevieux.araknemu.core.config.IniDriver;
import fr.quatrevieux.araknemu.core.dbal.DatabaseConfiguration;
import fr.quatrevieux.araknemu.core.dbal.DefaultDatabaseHandler;
import fr.quatrevieux.araknemu.core.dbal.executor.ConnectionPoolExecutor;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.transformer.TransformerException;
import org.apache.logging.log4j.LogManager;
import org.checkerframework.checker.nullness.qual.PolyNull;
import org.ini4j.Ini;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecordTest extends TestCase {
    private ConnectionPoolExecutor pool;

    @BeforeEach
    void setUp() throws IOException, SQLException {
        pool = new ConnectionPoolExecutor(
            new DefaultDatabaseHandler(
                new DefaultConfiguration(
                    new IniDriver(
                        new Ini(new File("src/test/test_config.ini"))
                    )
                ).module(DatabaseConfiguration.MODULE),
                LogManager.getLogger()
            ).get("realm")
        );

        pool.query("CREATE TABLE TEST (ID INTEGER PRIMARY KEY AUTOINCREMENT, VALUE TEXT)");
    }

    @AfterEach
    void tearDown() throws SQLException {
        pool.query("DROP TABLE TEST");
    }

    @Test
    void getString() throws SQLException {
        push(1, null);
        push(2, "");
        push(3, "foo");

        assertThrows(SQLException.class, () -> get(1, record -> record.getString("NOT_FOUND")));
        assertThrowsWithMessage(RecordException.class, "The column VALUE cannot be null", () -> get(1, record -> record.getString("VALUE")));
        assertEquals("", get(2, record -> record.getString("VALUE")));
        assertEquals("foo", get(3, record -> record.getString("VALUE")));
    }

    @Test
    void getNullableString() throws SQLException {
        push(1, null);
        push(2, "");
        push(3, "foo");

        assertThrows(SQLException.class, () -> get(1, record -> record.getNullableString("NOT_FOUND")));
        assertNull(get(1, record -> record.getNullableString("VALUE")));
        assertEquals("", get(2, record -> record.getNullableString("VALUE")));
        assertEquals("foo", get(3, record -> record.getNullableString("VALUE")));
    }

    @Test
    void unserialize() throws SQLException {
        Transformer<String> transformer = new Transformer<String>() {
            @Override
            public @PolyNull String serialize(@PolyNull String value) {
                return null;
            }

            @Override
            public @PolyNull String unserialize(@PolyNull String serialize) throws TransformerException {
                if ("error".equals(serialize)) {
                    throw new TransformerException("error");
                }

                return serialize == null ? null : "~" + serialize.toUpperCase() + "~";
            }
        };

        push(1, null);
        push(2, "");
        push(3, "foo");
        push(4, "error");

        assertThrows(SQLException.class, () -> get(1, record -> record.unserialize("NOT_FOUND", transformer)));
        assertThrowsWithMessage(RecordException.class, "The column VALUE cannot be null", () -> get(1, record -> record.unserialize("VALUE", transformer)));
        assertEquals("~~", get(2, record -> record.unserialize("VALUE", transformer)));
        assertEquals("~FOO~", get(3, record -> record.unserialize("VALUE", transformer)));
        assertThrowsWithMessage(RecordException.class, "Invalid value error for column VALUE", () -> get(4, record -> record.unserialize("VALUE", transformer)));
    }

    @Test
    void nullableUnserialize() throws SQLException {
        Transformer<String> transformer = new Transformer<String>() {
            @Override
            public @PolyNull String serialize(@PolyNull String value) {
                return null;
            }

            @Override
            public @PolyNull String unserialize(@PolyNull String serialize) throws TransformerException {
                if ("error".equals(serialize)) {
                    throw new TransformerException("error");
                }

                return serialize == null ? null : "~" + serialize.toUpperCase() + "~";
            }
        };

        push(1, null);
        push(2, "");
        push(3, "foo");
        push(4, "error");

        assertThrows(SQLException.class, () -> get(1, record -> record.nullableUnserialize("NOT_FOUND", transformer)));
        assertNull(get(1, record -> record.nullableUnserialize("VALUE", transformer)));
        assertEquals("~~", get(2, record -> record.nullableUnserialize("VALUE", transformer)));
        assertEquals("~FOO~", get(3, record -> record.nullableUnserialize("VALUE", transformer)));
        assertThrowsWithMessage(RecordException.class, "Invalid value error for column VALUE", () -> get(4, record -> record.nullableUnserialize("VALUE", transformer)));
    }

    @Test
    void getInt() throws SQLException {
        push(1, null);
        push(2, "");
        push(3, "123");
        push(4, "-5");

        assertThrows(SQLException.class, () -> get(1, record -> record.getInt("NOT_FOUND")));
        assertEquals(0, (int) get(1, record -> record.getInt("VALUE")));
        assertEquals(0, (int) get(2, record -> record.getInt("VALUE")));
        assertEquals(123, (int) get(3, record -> record.getInt("VALUE")));
        assertEquals(-5, (int) get(4, record -> record.getInt("VALUE")));
    }

    @Test
    void getOptionalInt() throws SQLException {
        push(1, null);
        push(2, "");
        push(3, "123");
        push(4, "-5");

        assertThrows(SQLException.class, () -> get(1, record -> record.getOptionalInt("NOT_FOUND")));
        assertEquals(OptionalInt.empty(), get(1, record -> record.getOptionalInt("VALUE")));
        assertEquals(OptionalInt.of(0), get(2, record -> record.getOptionalInt("VALUE")));
        assertEquals(OptionalInt.of(123), get(3, record -> record.getOptionalInt("VALUE")));
        assertEquals(OptionalInt.of(-5), get(4, record -> record.getOptionalInt("VALUE")));
    }

    @Test
    void getGTENegativeOneInt() throws SQLException {
        push(1, null);
        push(2, "");
        push(3, "123");
        push(4, "-5");
        push(5, "-1");
        push(6, "0");

        assertThrows(SQLException.class, () -> get(1, record -> record.getGTENegativeOneInt("NOT_FOUND")));
        assertEquals(0, (int) get(1, record -> record.getGTENegativeOneInt("VALUE")));
        assertEquals(0, (int) get(2, record -> record.getGTENegativeOneInt("VALUE")));
        assertEquals(123, (int) get(3, record -> record.getGTENegativeOneInt("VALUE")));
        assertThrowsWithMessage(RecordException.class, "Column VALUE must be higher than or equal -1", () -> get(4, record -> record.getGTENegativeOneInt("VALUE")));
        assertEquals(-1, (int) get(5, record -> record.getGTENegativeOneInt("VALUE")));
        assertEquals(0, (int) get(6, record -> record.getGTENegativeOneInt("VALUE")));
    }

    @Test
    void getNonNegativeInt() throws SQLException {
        push(1, null);
        push(2, "");
        push(3, "123");
        push(4, "-5");
        push(5, "-1");
        push(6, "0");

        assertThrows(SQLException.class, () -> get(1, record -> record.getNonNegativeInt("NOT_FOUND")));
        assertEquals(0, (int) get(1, record -> record.getNonNegativeInt("VALUE")));
        assertEquals(0, (int) get(2, record -> record.getNonNegativeInt("VALUE")));
        assertEquals(123, (int) get(3, record -> record.getNonNegativeInt("VALUE")));
        assertThrowsWithMessage(RecordException.class, "Column VALUE do not accept negative value", () -> get(4, record -> record.getNonNegativeInt("VALUE")));
        assertThrowsWithMessage(RecordException.class, "Column VALUE do not accept negative value", () -> get(5, record -> record.getNonNegativeInt("VALUE")));
        assertEquals(0, (int) get(6, record -> record.getNonNegativeInt("VALUE")));
    }

    @Test
    void getPositiveInt() throws SQLException {
        push(1, null);
        push(2, "");
        push(3, "123");
        push(4, "-5");
        push(5, "0");
        push(6, "1");

        assertThrows(SQLException.class, () -> get(1, record -> record.getPositiveInt("NOT_FOUND")));
        assertThrowsWithMessage(RecordException.class, "Column VALUE expect a positive value", () -> get(1, record -> record.getPositiveInt("VALUE")));
        assertThrowsWithMessage(RecordException.class, "Column VALUE expect a positive value", () -> get(2, record -> record.getPositiveInt("VALUE")));
        assertEquals(123, (int) get(3, record -> record.getPositiveInt("VALUE")));
        assertThrowsWithMessage(RecordException.class, "Column VALUE expect a positive value", () -> get(4, record -> record.getPositiveInt("VALUE")));
        assertThrowsWithMessage(RecordException.class, "Column VALUE expect a positive value", () -> get(5, record -> record.getPositiveInt("VALUE")));
        assertEquals(1, (int) get(6, record -> record.getPositiveInt("VALUE")));
    }

    @Test
    void getLong() throws SQLException {
        push(1, null);
        push(2, "");
        push(3, "123");
        push(4, "-5");

        assertThrows(SQLException.class, () -> get(1, record -> record.getLong("NOT_FOUND")));
        assertEquals(0L, (long) get(1, record -> record.getLong("VALUE")));
        assertEquals(0L, (long) get(2, record -> record.getLong("VALUE")));
        assertEquals(123L, (long) get(3, record -> record.getLong("VALUE")));
        assertEquals(-5L, (long) get(4, record -> record.getLong("VALUE")));
    }

    @Test
    void getNonNegativeLong() throws SQLException {
        push(1, null);
        push(2, "");
        push(3, "123");
        push(4, "-5");
        push(5, "-1");
        push(6, "0");

        assertThrows(SQLException.class, () -> get(1, record -> record.getNonNegativeLong("NOT_FOUND")));
        assertEquals(0L, (long) get(1, record -> record.getNonNegativeLong("VALUE")));
        assertEquals(0L, (long) get(2, record -> record.getNonNegativeLong("VALUE")));
        assertEquals(123L, (long) get(3, record -> record.getNonNegativeLong("VALUE")));
        assertThrowsWithMessage(RecordException.class, "Column VALUE do not accept negative value", () -> get(4, record -> record.getNonNegativeLong("VALUE")));
        assertThrowsWithMessage(RecordException.class, "Column VALUE do not accept negative value", () -> get(5, record -> record.getNonNegativeLong("VALUE")));
        assertEquals(0L, (long) get(6, record -> record.getNonNegativeLong("VALUE")));
    }

    @Test
    void getArrayValue() throws SQLException {
        Object[] arr = new Object[] {new Object(), new Object(), new Object()};

        push(1, null);
        push(2, "");
        push(3, "2");
        push(4, "-1");
        push(5, "3");
        push(6, "5");

        assertThrows(SQLException.class, () -> get(1, record -> record.getArrayValue("NOT_FOUND", arr)));
        assertSame(arr[0], get(1, record -> record.getArrayValue("VALUE", arr)));
        assertSame(arr[0], get(2, record -> record.getArrayValue("VALUE", arr)));
        assertSame(arr[2], get(3, record -> record.getArrayValue("VALUE", arr)));
        assertThrowsWithMessage(RecordException.class, "Column VALUE must be an index of array of length 3", () -> get(4, record -> record.getArrayValue("VALUE", arr)));
        assertThrowsWithMessage(RecordException.class, "Column VALUE must be an index of array of length 3", () -> get(5, record -> record.getArrayValue("VALUE", arr)));
        assertThrowsWithMessage(RecordException.class, "Column VALUE must be an index of array of length 3", () -> get(6, record -> record.getArrayValue("VALUE", arr)));
    }

    @Test
    void getBoolean() throws SQLException {
        push(1, null);
        push(2, "");
        push(3, "1");
        push(4, "0");

        assertThrows(SQLException.class, () -> get(1, record -> record.getBoolean("NOT_FOUND")));
        assertFalse((boolean) get(1, record -> record.getBoolean("VALUE")));
        assertFalse((boolean) get(2, record -> record.getBoolean("VALUE")));
        assertTrue((boolean) get(3, record -> record.getBoolean("VALUE")));
        assertFalse((boolean) get(4, record -> record.getBoolean("VALUE")));
    }

    @Test
    void getDouble() throws SQLException {
        push(1, null);
        push(2, "");
        push(3, "12.3");

        assertThrows(SQLException.class, () -> get(1, record -> record.getDouble("NOT_FOUND")));
        assertEquals(.0, (double) get(1, record -> record.getDouble("VALUE")));
        assertEquals(.0, (double) get(2, record -> record.getDouble("VALUE")));
        assertEquals(12.3, (double) get(3, record -> record.getDouble("VALUE")));
    }

    @Test
    void getCsvArray() throws SQLException {
        push(1, null);
        push(2, "");
        push(3, "foo");
        push(4, "foo,bar,baz");

        assertThrows(SQLException.class, () -> get(1, record -> record.getCsvArray("NOT_FOUND", ',')));
        assertThrowsWithMessage(RecordException.class, "The column VALUE cannot be null", () -> get(1, record -> record.getCsvArray("VALUE", ',')));
        assertArrayEquals(new String[0], get(2, record -> record.getCsvArray("VALUE", ',')));
        assertArrayEquals(new String[] {"foo"}, get(3, record -> record.getCsvArray("VALUE", ',')));
        assertArrayEquals(new String[] {"foo", "bar", "baz"}, get(4, record -> record.getCsvArray("VALUE", ',')));
    }

    @Test
    void getIntArray() throws SQLException {
        push(1, null);
        push(2, "");
        push(3, "12,invalid,34");
        push(4, "15");
        push(5, "15,58,63");

        assertThrows(SQLException.class, () -> get(1, record -> record.getIntArray("NOT_FOUND", ',')));
        assertThrowsWithMessage(RecordException.class, "The column VALUE cannot be null", () -> get(1, record -> record.getIntArray("VALUE", ',')));
        assertArrayEquals(new int[0], get(2, record -> record.getIntArray("VALUE", ',')));
        assertThrowsWithMessage(RecordException.class, "Invalid int value for column VALUE", () -> get(3, record -> record.getIntArray("VALUE", ',')));
        assertArrayEquals(new int[] {15}, get(4, record -> record.getIntArray("VALUE", ',')));
        assertArrayEquals(new int[] {15, 58, 63}, get(5, record -> record.getIntArray("VALUE", ',')));
    }

    @Test
    void getNullableIntArray() throws SQLException {
        push(1, null);
        push(2, "");
        push(3, "12,invalid,34");
        push(4, "15");
        push(5, "15,58,63");

        assertThrows(SQLException.class, () -> get(1, record -> record.getNullableIntArray("NOT_FOUND", ',')));
        assertNull(get(1, record -> record.getNullableIntArray("VALUE", ',')));
        assertArrayEquals(new int[0], get(2, record -> record.getNullableIntArray("VALUE", ',')));
        assertThrowsWithMessage(RecordException.class, "Invalid int value for column VALUE", () -> get(3, record -> record.getNullableIntArray("VALUE", ',')));
        assertArrayEquals(new int[] {15}, get(4, record -> record.getNullableIntArray("VALUE", ',')));
        assertArrayEquals(new int[] {15, 58, 63}, get(5, record -> record.getNullableIntArray("VALUE", ',')));
    }

    @Test
    void toMap() throws SQLException {
        push(1, null);
        push(2, "");
        push(3, "foo");

        assertEquals(new HashMap<String, String> () {{
            put("ID", "1");
            put("VALUE", null);
        }}, get(1, Record::toMap));
        assertEquals(new HashMap<String, String> () {{
            put("ID", "2");
            put("VALUE", "");
        }}, get(2, Record::toMap));
        assertEquals(new HashMap<String, String> () {{
            put("ID", "3");
            put("VALUE", "foo");
        }}, get(3, Record::toMap));

        assertEquals(Arrays.asList("ID", "VALUE"), get(3, record -> new ArrayList<>(record.toMap().keySet())));
    }

    private void push(int id, String value) throws SQLException {
        pool.prepare("INSERT INTO TEST (ID, VALUE) VALUES (?, ?)", statement -> {
            statement.setInt(1, id);
            statement.setString(2, value);

            return statement.executeUpdate();
        });
    }

    private <R> R get(int id, RecordFunction<R> action) throws SQLException {
        return pool.prepare("SELECT * FROM TEST WHERE ID = ?", statement -> {
            statement.setInt(1, id);

            ResultSet rs = statement.executeQuery();
            rs.next();

            return action.apply(new Record(rs));
        });
    }

    @FunctionalInterface
    interface RecordFunction<R> {
        public R apply(Record record) throws SQLException;
    }
}
