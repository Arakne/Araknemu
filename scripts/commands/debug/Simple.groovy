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

import fr.quatrevieux.araknemu.game.admin.AbstractCommand
import fr.quatrevieux.araknemu.game.admin.AdminPerformer
import fr.quatrevieux.araknemu.game.admin.exception.AdminException

class Simple extends AbstractCommand<String> {
    @Override
    protected void build(Builder builder) {
        builder.help({it
            .description("Command with simple string argument")
            .synopsis(":simple ARG")
        })
    }

    @Override
    String name() { "simple" }

    @Override
    void execute(AdminPerformer performer, String arguments) throws AdminException {
        performer.info("Execute {} with {}", name(), arguments)
    }
}
