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

import com.sun.management.OperatingSystemMXBean;
import fr.quatrevieux.araknemu.Araknemu;
import fr.quatrevieux.araknemu.game.GameService;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.player.PlayerService;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;

/**
 * Display information about the server
 */
public final class Info extends AbstractCommand<Void> {
    private final Araknemu app;
    private final PlayerService playerService;
    private final GameService gameService;
    private final FightService fightService;

    public Info(Araknemu app, PlayerService playerService, GameService gameService, FightService fightService) {
        this.app = app;
        this.playerService = playerService;
        this.gameService = gameService;
        this.fightService = fightService;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .help(formatter -> formatter
                .description("Display information about the server")
                .synopsis("info")
                .example("*info", "Display server info")
            )
        ;
    }

    @Override
    public String name() {
        return "info";
    }

    @Override
    public void execute(AdminPerformer performer, Void arguments) {
        performer.success("===== Server information =====");
        performer.info(
            "Uptime : {} (started at : {})",
            formatDuration(Duration.between(app.startDate(), Instant.now())),
            app.startDate()
        );
        performer.info("Online : {} sessions and {} players", gameService.sessions().size(), playerService.online().size());
        performer.info("Fights : {} fights with {} fighters",
            fightService.fights().size(),
            fightService.fights().stream().mapToLong(fight -> fight.fighters().all().size()).sum()
        );
        performer.info(
            "RAM usage : {} / {}",
            formatBytes(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()),
            formatBytes(Runtime.getRuntime().totalMemory())
        );
        performer.info("Number of threads : {}", Thread.activeCount());
        performer.info("CPU Usage {}%", cpuUsage());
    }

    private String formatDuration(Duration duration) {
        final StringBuilder formatted = new StringBuilder();

        if (duration.toDays() > 0) {
            formatted.append(duration.toDays()).append(" days ");
        }

        if (duration.toHours() % 24 > 0) {
            formatted.append(duration.toHours() % 24).append(" hours ");
        }

        formatted.append(duration.toMinutes() % 60).append(" minutes");

        return formatted.toString();
    }

    @SuppressWarnings({"array.access.unsafe.high.range", "array.access.unsafe.high"}) // Ignore out of range for units (I don't think that TB of RAM will be reached)
    private static String formatBytes(long bytes) {
        final long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);

        if (absB < 1024) {
            return bytes + " B";
        }

        long value = absB;

        final char[] units = new char[] {'K', 'M', 'G'};

        int currentUnit = 0;

        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ++currentUnit;
        }

        value *= Long.signum(bytes);

        return String.format("%.1f %ciB", value / 1024.0, units[currentUnit]);
    }

    private int cpuUsage() {
        final OperatingSystemMXBean os = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        return (int) (100 * os.getProcessCpuLoad());
    }
}
