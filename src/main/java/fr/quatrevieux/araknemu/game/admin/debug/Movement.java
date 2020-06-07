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
import fr.quatrevieux.araknemu.game.admin.formatter.Link;
import fr.quatrevieux.araknemu.network.game.out.game.FightStartPositions;

import java.util.ArrayList;
import java.util.List;

/**
 * Display cells by their movement value
 */
final public class Movement extends AbstractCommand {
    final private MapTemplateRepository repository;

    public Movement(MapTemplateRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .description("Highlight cell by their movement value")
            .help(
                formatter -> formatter
                    .synopsis("movement [1-8]")
                    .seeAlso("${debug} fightpos hide", "For hide the cells", Link.Type.EXECUTE)
            )
            .requires(Permission.DEBUG)
        ;
    }

    @Override
    public String name() {
        return "movement";
    }

    @Override
    public void execute(AdminPerformer performer, List<String> arguments) throws AdminException {
        AdminUser user = AdminUser.class.cast(performer);

        int mov = Integer.parseInt(arguments.get(1));

        MapTemplate map = repository.get(user.player().position().map());

        List<Integer> cells = new ArrayList<>();

        for (int i = 0; i < map.cells().length; ++i) {
            if (map.cells()[i].movement().ordinal() == mov) {
                cells.add(i);
            }
        }

        user.send(
            new FightStartPositions(
                new List[] {
                    cells,
                    new ArrayList()
                },
                0
            )
        );
    }
}
