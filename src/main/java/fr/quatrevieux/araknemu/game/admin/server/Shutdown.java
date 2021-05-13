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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.admin.server;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.ShutdownService;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.executor.argument.type.SubArguments;
import fr.quatrevieux.araknemu.game.admin.executor.argument.type.SubArgumentsCommandTrait;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.spi.SubCommand;
import org.kohsuke.args4j.spi.SubCommands;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;

/**
 * Command for shutdown the server
 */
public final class Shutdown extends AbstractCommand<Shutdown.Arguments> implements SubArgumentsCommandTrait<Shutdown.Arguments> {
    private final ShutdownService service;

    public Shutdown(ShutdownService service) {
        this.service = service;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .description("Stop the server")
            .help(formatter -> formatter
                .synopsis("shutdown [now|in|at|show] ARGUMENTS")
                .options("now", "Shutdown the server immediately. Do not requires any parameters.")
                .options("in DURATION", "Shutdown the server in a given amount of time. Format is <i>[hours]h[minutes]m[seconds]s</i>. All parts are optional.")
                .options("at TIME", "Shutdown the server at a given time. Format is <i>[hours]:[minutes]:[seconds]</i>. Seconds are optional.")
                .options("show", "Show the current scheduled shutdown.")
                .example("${server} shutdown now", "Stop the server immediately.")
                .example("${server} shutdown at 15:00", "Stop the server at 15:00:00.")
                .example("${server} shutdown in 30m", "Stop the server in 30 minutes.")
            )
            .requires(Permission.SUPER_ADMIN)
        ;
    }

    @Override
    public String name() {
        return "shutdown";
    }

    private void shutdownNow() {
        service.now();
    }

    private void shutdownIn(AdminPerformer performer, Duration duration) {
        service.schedule(duration);
        show(performer);
    }

    private void shutdownAt(AdminPerformer performer, LocalTime time) {
        Duration delay = Duration.between(LocalTime.now(), time);

        // scheduled time is before now : consider that it's scheduled for the next day
        if (delay.isNegative()) {
            delay = delay.plusDays(1);
        }

        shutdownIn(performer, delay);
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

    @Override
    public Arguments createArguments() {
        return new Arguments();
    }

    public static final class Arguments implements SubArguments<Shutdown> {
        @Argument(required = true)
        @SubCommands({
            @SubCommand(name = "now", impl = NowArguments.class),
            @SubCommand(name = "in", impl = InArguments.class),
            @SubCommand(name = "at", impl = AtArguments.class),
            @SubCommand(name = "show", impl = ShowArguments.class),
            @SubCommand(name = "cancel", impl = CancelArguments.class),
        })
        private SubArguments<Shutdown> sub;

        @Override
        public void execute(AdminPerformer performer, Shutdown command) throws AdminException {
            sub.execute(performer, command);
        }

        public static final class NowArguments implements SubArguments<Shutdown> {
            @Override
            public void execute(AdminPerformer performer, Shutdown command) throws AdminException {
                command.shutdownNow();
            }
        }

        public static final class ShowArguments implements SubArguments<Shutdown> {
            @Override
            public void execute(AdminPerformer performer, Shutdown command) throws AdminException {
                command.show(performer);
            }
        }

        public static final class CancelArguments implements SubArguments<Shutdown> {
            @Override
            public void execute(AdminPerformer performer, Shutdown command) throws AdminException {
                command.cancel(performer);
            }
        }

        public static final class InArguments implements SubArguments<Shutdown> {
            @Argument(required = true)
            private Duration duration;

            @Override
            public void execute(AdminPerformer performer, Shutdown command) throws AdminException {
                command.shutdownIn(performer, duration);
            }
        }

        public static final class AtArguments implements SubArguments<Shutdown> {
            @Argument(required = true)
            private LocalTime time;

            @Override
            public void execute(AdminPerformer performer, Shutdown command) throws AdminException {
                command.shutdownAt(performer, time);
            }
        }
    }
}
