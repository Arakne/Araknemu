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

package fr.quatrevieux.araknemu.game.admin.server;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.ShutdownService;
import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

import static org.junit.jupiter.api.Assertions.*;

class ShutdownTest extends CommandTestCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        command = new Shutdown(container.get(ShutdownService.class));
    }

    @AfterEach
    @Override
    public void tearDown() throws ContainerException {
        container.get(ShutdownService.class).cancel();

        super.tearDown();
    }

    @Test
    void executeInvalidAction() {
        assertThrows(CommandException.class, () -> execute("shutdown", "invalid"));
        assertThrows(CommandException.class, () -> execute("shutdown"));
    }

    @Test
    void executeNow() throws SQLException, AdminException, InterruptedException {
        execute("shutdown", "now");
        Thread.sleep(10);

        assertFalse(app.started());
    }

    @Test
    void executeIn() throws SQLException, AdminException {
        execute("shutdown", "in", "5m");

        assertBetween(4, 5, container.get(ShutdownService.class).delay().get().toMinutes());
        performer.logs.get(0).message.startsWith("Shutdown scheduled at");
    }

    @Test
    void executeInMissingDelay() {
        assertThrows(CommandException.class, () -> execute("shutdown", "in"));
    }

    @Test
    void executeAt() throws SQLException, AdminException {
        execute("shutdown", "at", "22:45");

        LocalDateTime time = LocalDateTime.now().plus(container.get(ShutdownService.class).delay().get());

        assertEquals(22, time.getHour());
        assertBetween(44, 45, time.getMinute());
        performer.logs.get(0).message.startsWith("Shutdown scheduled at");
    }

    @Test
    void executeAtBeforeNowShouldBeScheduledNextDay() throws SQLException, AdminException {
        execute("shutdown", "at", "00:01");

        LocalDateTime time = LocalDateTime.now().plus(container.get(ShutdownService.class).delay().get());

        assertEquals(0, time.getHour());
        assertBetween(0, 1, time.getMinute());
        assertEquals(LocalDateTime.now().plus(1, ChronoUnit.DAYS).getDayOfMonth(), time.getDayOfMonth());
        performer.logs.get(0).message.startsWith("Shutdown scheduled at");
    }

    @Test
    void executeAtMissingTime() {
        assertThrows(CommandException.class, () -> execute("shutdown", "at"));
    }

    @Test
    void showNotScheduled() throws SQLException, AdminException {
        execute("shutdown", "show");

        assertError("No scheduled shutdown");
    }

    @Test
    void showSuccess() throws SQLException, AdminException {
        container.get(ShutdownService.class).schedule(Duration.ofSeconds(10));
        execute("shutdown", "show");

        performer.logs.get(0).message.startsWith("Shutdown scheduled at");
    }

    @Test
    void cancelSuccess() throws SQLException, AdminException {
        container.get(ShutdownService.class).schedule(Duration.ofSeconds(10));
        execute("shutdown", "cancel");

        assertFalse(container.get(ShutdownService.class).delay().isPresent());
        assertSuccess("Shutdown has been cancelled");
    }

    @Test
    void cancelNotScheduled() throws SQLException, AdminException {
        execute("shutdown", "cancel");

        assertError("No shutdown has been scheduled");
    }

    @Test
    void help() {
        assertTrue(command.help().contains("shutdown [action] [parameter]"));
    }
}
