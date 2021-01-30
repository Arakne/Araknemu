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
import fr.quatrevieux.araknemu.game.player.PlayerService;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Display information about the server
 */
final public class Info extends AbstractCommand {
    final private Araknemu app;
    final private PlayerService playerService;
    final private GameService gameService;

    public Info(Araknemu app, PlayerService playerService, GameService gameService) {
        this.app = app;
        this.playerService = playerService;
        this.gameService = gameService;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .description("Display information about the server")
            .help(formatter -> formatter
                .synopsis("info")
                .example("${server} info", "Display server info")
            )
        ;
    }

    @Override
    public String name() {
        return "info";
    }

    @Override
    public void execute(AdminPerformer performer, List<String> arguments) {
        performer.success("===== Server information =====");
        performer.info(
            "Uptime : {} (started at : {})",
            formatDuration(Duration.between(app.startDate(), Instant.now())),
            app.startDate()
        );
        performer.info("Online : {} sessions and {} players", gameService.sessions().size(), playerService.online().size());
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
