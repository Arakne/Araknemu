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

import fr.quatrevieux.araknemu.game.ShutdownService;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Command for shutdown the server
 */
final public class Shutdown extends AbstractCommand {
    final private ShutdownService service;

    public Shutdown(ShutdownService service) {
        this.service = service;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .description("Stop the server")
            .help(formatter -> formatter
                .synopsis("shutdown [action] [parameter]")
                .options("now", "Shutdown the server immediately. Do not requires any parameters.")
                .options("in", "Shutdown the server in a given amount of time. Format is <i>[hours]h[minutes]m[seconds]s</i>. All parts are optional.")
                .options("at", "Shutdown the server at a given time. Format is <i>[hours]:[minutes]:[seconds]</i>. Seconds are optional.")
                .options("show", "Show the current scheduled shutdown.")
                .example("${server} shutdown now", "Stop the server immediately.")
                .example("${server} shutdown at 15:00", "Stop the server at 15:00:00.")
                .example("${server} shutdown in 30m", "Stop the server in 30 minutes.")
            )
        ;
    }

    @Override
    public String name() {
        return "shutdown";
    }

    @Override
    public void execute(AdminPerformer performer, List<String> arguments) throws AdminException {
        if (arguments.size() < 2) {
            throw new CommandException(name(), "Missing the action");
        }

        switch (arguments.get(1)) {
            case "now":
                service.now();
                break;

            case "in":
                shutdownIn(performer, arguments);
                break;

            case "at":
                shutdownAt(performer, arguments);
                break;

            case "show":
                show(performer);
                break;

            case "cancel":
                cancel(performer);
                break;

            default:
                throw new CommandException(name(), "The action " + arguments.get(1) + " is not valid");
        }
    }

    private void shutdownIn(AdminPerformer performer, List<String> arguments) throws AdminException {
        if (arguments.size() < 3) {
            throw new CommandException(name(), "Missing the delay");
        }

        String value = arguments.get(2).toUpperCase();

        if (value.charAt(0) != 'P') {
            value = "PT" + value;
        }

        service.schedule(Duration.parse(value));
        show(performer);
    }

    private void shutdownAt(AdminPerformer performer, List<String> arguments) throws AdminException {
        if (arguments.size() < 3) {
            throw new CommandException(name(), "Missing the time");
        }

        service.schedule(
            Duration.between(
                LocalTime.now(),
                LocalTime.from(DateTimeFormatter.ofPattern("HH:mm[:ss]").parse(arguments.get(2)))
            )
        );
        show(performer);
    }

    private void show(AdminPerformer performer) {
        if (!service.delay().isPresent()) {
            performer.error("No scheduled shutdown");
            return;
        }

        final Duration duration = service.delay().get();

        performer.success(
            "Shutdown scheduled at {} (in {})",
            Instant.now().plus(duration),
            DurationFormatUtils.formatDurationHMS(duration.toMillis())
        );
    }

    private void cancel(AdminPerformer performer) {
        if (service.cancel()) {
            performer.success("Shutdown has been cancelled");
        } else {
            performer.error("No shutdown has been scheduled");
        }
    }
}
