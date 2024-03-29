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
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.network.game.out.game.FightStartPositions;
import org.checkerframework.checker.index.qual.NonNegative;
import org.kohsuke.args4j.Argument;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

        final CoordinateCell<BattlefieldCell> current = map.get(user.player().position().cell()).coordinate();
        final CellSight<BattlefieldCell> sight = new CellSight<>(current);

        final List<BattlefieldCell> accessible;
        final List<BattlefieldCell> blocked;

        if (!arguments.hasTargetCell()) {
            accessible = new ArrayList<>(sight.accessible());
            blocked = new ArrayList<>(sight.blocked());
        } else {
            final Iterator<BattlefieldCell> los = sight.to(map.get(arguments.cellId()));

            accessible = new ArrayList<>();
            blocked = new ArrayList<>();

            boolean isFree = true;

            while (los.hasNext()) {
                final BattlefieldCell cell = los.next();

                if (isFree) {
                    accessible.add(cell);
                    isFree = !cell.sightBlocking();
                } else {
                    blocked.add(cell);
                }
            }
        }

        user.send(new FightStartPositions(new MapCell[][] {
            blocked.toArray(new MapCell[0]),
            accessible.toArray(new MapCell[0]),
        }, 0));
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
