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

package fr.quatrevieux.araknemu.game.admin.debug;

import fr.arakne.utils.maps.CoordinateCell;
import fr.arakne.utils.maps.MapCell;
import fr.arakne.utils.maps.sight.CellSight;
import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.AdminUser;
import fr.quatrevieux.araknemu.game.admin.formatter.Link;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.network.game.out.game.FightStartPositions;
import org.checkerframework.checker.index.qual.NonNegative;
import org.kohsuke.args4j.Argument;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Display accessible cells by line of sight
 */
public final class LineOfSight extends AbstractCommand<LineOfSight.Arguments> {
    private final MapTemplateRepository repository;

    public LineOfSight(MapTemplateRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .help(
                formatter -> formatter
                    .description("Highlight accessible cells by line of sight")
                    .synopsis("lineofsight [target cell id]")
                    .option("target cell id", "Optional. The target cell id for dump the line of sight to this cell")
                    .seeAlso(":fightpos hide", "For hide the cells", Link.Type.EXECUTE)
            )
            .requires(Permission.DEBUG)
        ;
    }

    @Override
    public String name() {
        return "lineofsight";
    }

    @Override
    @SuppressWarnings("argument") // the cell is valid
    public void execute(AdminPerformer performer, Arguments arguments) {
        final AdminUser user = AdminUser.class.cast(performer);
        final FightMap map = new FightMap(repository.get(user.player().position().map()));

        final CoordinateCell<FightCell> current = map.get(user.player().position().cell()).coordinate();
        final CellSight<FightCell> sight = new CellSight<>(current);

        final List<Integer> accessible;
        final List<Integer> blocked;

        if (!arguments.hasTargetCell()) {
            accessible = sight.accessible().stream().map(MapCell::id).collect(Collectors.toList());
            blocked = sight.blocked().stream().map(MapCell::id).collect(Collectors.toList());
        } else {
            final Iterator<FightCell> los = sight.to(map.get(arguments.cellId()));

            accessible = new ArrayList<>();
            blocked = new ArrayList<>();

            boolean isFree = true;

            while (los.hasNext()) {
                final FightCell cell = los.next();

                if (isFree) {
                    accessible.add(cell.id());
                    isFree = !cell.sightBlocking();
                } else {
                    blocked.add(cell.id());
                }
            }
        }

        user.send(new FightStartPositions(new List[] {blocked, accessible}, 0));
    }

    @Override
    public Arguments createArguments() {
        return new Arguments();
    }

    @SuppressWarnings("initialization.field.uninitialized")
    public static final class Arguments {
        @Argument(metaVar = "target cell id")
        private @NonNegative Integer cellId;

        public void setCellId(@NonNegative Integer cellId) {
            this.cellId = cellId;
        }

        public @NonNegative int cellId() {
            return cellId;
        }

        public boolean hasTargetCell() {
            return cellId != null;
        }
    }
}
