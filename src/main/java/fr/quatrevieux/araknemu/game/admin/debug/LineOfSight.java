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

import fr.arakne.utils.maps.CoordinateCell;
import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.AdminUser;
import fr.quatrevieux.araknemu.game.admin.formatter.Link;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.network.game.out.game.FightStartPositions;

import java.util.ArrayList;
import java.util.List;

/**
 * Display accessible cells by line of sight
 */
final public class LineOfSight extends AbstractCommand {
    final private MapTemplateRepository repository;

    public LineOfSight(MapTemplateRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .description("Highlight accessible cells by line of sight")
            .help(
                formatter -> formatter
                    .synopsis("lineofsight")
                    .seeAlso("${debug} fightpos hide", "For hide the cells", Link.Type.EXECUTE)
            )
            .requires(Permission.DEBUG)
        ;
    }

    @Override
    public String name() {
        return "lineofsight";
    }

    @Override
    public void execute(AdminPerformer performer, List<String> arguments) {
        AdminUser user = AdminUser.class.cast(performer);

        FightMap map = new FightMap(repository.get(user.player().position().map()));

        List<Integer> accessible = new ArrayList<>();
        List<Integer> blocked = new ArrayList<>();

        CoordinateCell<FightCell> current = new CoordinateCell<>(map.get(user.player().position().cell()));
        fr.arakne.utils.maps.LineOfSight<FightCell> lineOfSight = new fr.arakne.utils.maps.LineOfSight<>(map);

        for (int i = 0; i < map.size(); ++i) {
            if (lineOfSight.between(current, new CoordinateCell<>(map.get(i)))) {
                accessible.add(i);
            } else {
                blocked.add(i);
            }
        }

        user.send(
            new FightStartPositions(
                new List[] {blocked, accessible},
                0
            )
        );
    }
}
