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

package fr.quatrevieux.araknemu.game.admin.debug;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.AdminUser;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.network.game.out.fight.CancelFight;
import fr.quatrevieux.araknemu.network.game.out.game.FightStartPositions;

import java.util.Arrays;
import java.util.List;

/**
 * Show / hide fight places
 */
public final class FightPos extends AbstractCommand {
    private final MapTemplateRepository repository;

    public FightPos(MapTemplateRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .description("Show all fight positions")
            .help(
                formatter -> formatter
                    .synopsis("fightpos [show|hide]")
                    .example("fightpos show", "Show the fight positions")
                    .example("fightpos hide", "Hide all fight positions")
            )
            .requires(Permission.DEBUG)
        ;
    }

    @Override
    public String name() {
        return "fightpos";
    }

    @Override
    public void execute(AdminPerformer performer, List<String> arguments) throws AdminException {
        final AdminUser user = AdminUser.class.cast(performer);

        if (arguments.size() > 1 && arguments.get(1).equalsIgnoreCase("hide")) {
            user.player().exploration().leave();
            user.send(new CancelFight());
            return;
        }

        final MapTemplate map = repository.get(user.player().position().map());

        if (
            map.fightPlaces().length < 2
            || map.fightPlaces()[0].isEmpty()
            || map.fightPlaces()[1].isEmpty()
        ) {
            performer.error("No fight places found");
            return;
        }

        performer.info("Places : {}", Arrays.toString(map.fightPlaces()));

        if (arguments.size() > 1 && arguments.get(1).equalsIgnoreCase("show")) {
            user.send(new FightStartPositions(map.fightPlaces(), 0));
        }
    }
}
