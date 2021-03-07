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

package fr.quatrevieux.araknemu.game.admin.global;

import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.LogType;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Simple "echo" command
 */
public final class Echo extends AbstractCommand {
    @Override
    protected void build(Builder builder) {
        builder
            .description("Write to console the arguments")
            .help(
                formatter -> formatter
                    .synopsis("echo [-i|s|e] [args ...]")

                    .options("-i", "Print as information (default)")
                    .options("-s", "Print as success")
                    .options("-e", "Print as error")

                    .example("echo Hello World !", "Print 'Hello World !' in white (info)")
                    .example("echo -e WAKE !!!", "Print 'WAKE !!!' in red (error)")
            )
        ;
    }

    @Override
    public String name() {
        return "echo";
    }

    @Override
    public void execute(AdminPerformer performer, List<String> arguments) {
        LogType log = LogType.DEFAULT;
        int start = 2;

        switch (arguments.get(1)) {
            case "-e":
                log = LogType.ERROR;
                break;
            case "-s":
                log = LogType.SUCCESS;
                break;
            case "-i":
                log = LogType.DEFAULT;
                break;
            default:
                start = 1;
        }

        performer.log(log, StringUtils.join(arguments.listIterator(start), " "));
    }
}
