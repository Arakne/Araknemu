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
import fr.quatrevieux.araknemu.game.SavingService;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;

/**
 * Command for perform game server save
 */
public final class Save extends AbstractCommand<Void> {
    private final SavingService service;

    public Save(SavingService service) {
        this.service = service;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .help(help -> help.description("Save all connected players"))
            .requires(Permission.MANAGE_PLAYER)
        ;
    }

    @Override
    public String name() {
        return "save";
    }

    @Override
    public void execute(AdminPerformer performer, Void arguments) throws AdminException {
        if (service.execute()) {
            performer.info("Start save");
        } else {
            performer.error("A save is already in progress");
        }
    }
}
