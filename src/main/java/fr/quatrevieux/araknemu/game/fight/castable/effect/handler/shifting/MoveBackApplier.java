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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting;

import fr.arakne.utils.maps.MapCell;
import fr.arakne.utils.maps.constant.Direction;
import fr.arakne.utils.maps.path.Decoder;
import fr.arakne.utils.value.helper.RandomUtil;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;

import java.util.Optional;

/**
 * Apply the move back effect to one fighter
 * This class will perform the move, and apply blocking damage
 */
public final class MoveBackApplier {
    public static final int BASE_DAMAGE = 8;
    public static final int MAX_DAMAGE = 8;

    private final Fight fight;
    private final @NonNegative int baseDamage;
    private final @Positive int maxDamage;

    private final Decoder<FightCell> decoder;
    private final RandomUtil random = new RandomUtil();

    public MoveBackApplier(Fight fight) {
        this(fight, BASE_DAMAGE, MAX_DAMAGE);
    }

    /**
     * @param fight The current fight instance
     * @param baseDamage The base damage per remaining distance
     * @param maxDamage The maximum damage of the random part per remaining distance
     */
    public MoveBackApplier(Fight fight, @NonNegative int baseDamage, @Positive int maxDamage) {
        this.fight = fight;
        this.baseDamage = baseDamage;
        this.maxDamage = maxDamage;
        this.decoder = fight.map().decoder();
    }

    /**
     * Apply the effect to the given target
     *
     * @param caster The spell caster
     * @param target The spell target
     * @param distance The move back distance
     */
    public void apply(Fighter caster, Fighter target, @NonNegative int distance) {
        final Direction direction = caster.cell().coordinate().directionTo(target.cell());
        FightCell destination = target.cell();

        // Check if a cell block the movement
        for (; distance > 0; --distance) {
            final Optional<FightCell> nextCell = decoder
                .nextCellByDirection(destination, direction)
                .filter(MapCell::walkable)
            ;

            if (!nextCell.isPresent()) {
                break;
            }

            destination = nextCell.get();
        }

        // Fighter has moved
        if (!destination.equals(target.cell())) {
            target.move(destination);
            fight.send(ActionEffect.slide(caster, target, destination));
        }

        if (distance > 0) {
            applyBlockingDamageChain(caster, target, destination, direction, distance);
        }
    }

    /**
     * Apply blocked move back damage on each fighter on the path
     * The damage are divided by two to each following fighter
     *
     * @param caster The move back caster
     * @param target The base target
     * @param lastCell The destination cell (where the target is blocked)
     * @param direction The move direction
     * @param distance Remain move distance
     */
    private void applyBlockingDamageChain(Fighter caster, Fighter target, FightCell lastCell, Direction direction, @NonNegative int distance) {
        int damage = computeDamage(caster, distance);

        if (damage <= 0) {
            return;
        }

        target.life().alter(caster, -damage);

        // Divide damage by 2 on each fighter
        for (damage /= 2; damage > 0; damage /= 2) {
            final Optional<FightCell> nextCell = decoder
                .nextCellByDirection(lastCell, direction)
                .filter(cell -> cell.fighter().isPresent())
            ;

            // Out of map, or no player is present here : stop the chain
            if (!nextCell.isPresent()) {
                return;
            }

            lastCell = nextCell.get();
            lastCell.fighter().get().life().alter(caster, -damage);
        }
    }

    /**
     * Compute the damage in life point for a blocked move back
     *
     * https://www.dofus.com/fr/forum/1750-dofus/234052-version-beta-1-27?page=3#entry1657700
     *
     * @param caster The spell caster
     * @param distance The remaining distance
     *
     * @return The damage
     */
    private @NonNegative int computeDamage(FighterData caster, @NonNegative int distance) {
        return (baseDamage + random.rand(1, maxDamage) * caster.level() / 50) * distance;
    }
}
