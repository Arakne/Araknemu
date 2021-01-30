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

import fr.arakne.utils.maps.MapCell;
import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.data.world.transformer.EffectAreaTransformer;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.AdminUser;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffectService;
import fr.quatrevieux.araknemu.network.game.out.game.FightStartPositions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Display effect area
 */
final public class Area extends AbstractCommand {
    final private SpellEffectService service;
    final private EffectAreaTransformer areaTransformer;

    public Area(SpellEffectService service) {
        this.service = service;
        this.areaTransformer = new EffectAreaTransformer();
    }

    @Override
    protected void build(Builder builder) {
        builder
            .requires(Permission.DEBUG)
            .description("Display spell effect area")
            .help(
                formatter -> formatter
                    .synopsis("area [area string]")
                    .options(
                        "area string",
                        "Contains two chars, the first one is the area type, the second if the size in pseudo base 64\n" +
                        "Types :\n" +
                            Arrays.stream(EffectArea.Type.values())
                                .map(type -> "\t" + type.name() + " : " + type.c())
                                .collect(Collectors.joining("\n"))
                    )
                    .example("area Cb", "Circle of size 1")
                    .example("area Tc", "Perpendicular line of size 2")
            )
        ;
    }

    @Override
    public String name() {
        return "area";
    }

    @Override
    public void execute(AdminPerformer performer, List<String> arguments) {
        final AdminUser user = AdminUser.class.cast(performer);
        final EffectArea area = areaTransformer.unserialize(arguments.get(1));
        final ExplorationMap map = user.player().exploration().map();

        final List<Integer> cells = service.area(area)
            .resolve(
                map.get(user.player().position().cell()),
                map.get(user.player().position().cell())
            )
            .stream()
            .map(MapCell::id)
            .collect(Collectors.toList())
        ;

        user.send(
            new FightStartPositions(
                new List[] {
                    cells,
                    new ArrayList(),
                },
                0
            )
        );
    }
}
