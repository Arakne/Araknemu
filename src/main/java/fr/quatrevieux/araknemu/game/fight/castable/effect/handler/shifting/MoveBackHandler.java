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

import fr.arakne.utils.maps.CoordinateCell;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;

import java.util.Comparator;

/**
 * Move back targets
 *
 * - Start to move the most distant targets
 * - If the target is blocked, apply damage
 * - Apply damage divided by 2 on following fighters, if the obstacle is a fighter
 */
public final class MoveBackHandler implements EffectHandler {
    private final MoveBackApplier applier;

    public MoveBackHandler(Fight fight) {
        applier = new MoveBackApplier(fight);
    }

    @Override
    public void handle(CastScope cast, CastScope.EffectScope effect) {
        final ActiveFighter caster = cast.caster();
        final int distance = effect.effect().min();
        final CoordinateCell<FightCell> casterCell = caster.cell().coordinate();

        // Apply to most distant targets before, to ensure that they will not block mutually
        effect.targets().stream()
            .sorted(Comparator.<PassiveFighter>comparingInt(target -> casterCell.distance(target.cell())).reversed())
            .forEach(target -> applier.apply(caster, target, distance))
        ;
    }

    @Override
    public void buff(CastScope cast, CastScope.EffectScope effect) {
        throw new UnsupportedOperationException("Cannot use move back as buff effect");
    }
}
