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

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

/**
 * Perform the switch position of two fighters
 */
public final class SwitchPositionApplier {
    private final Fight fight;

    public SwitchPositionApplier(Fight fight) {
        this.fight = fight;
    }

    /**
     * Apply the switch effect
     *
     * If one of the fighter is dead, nothing is done (because the fighter is not on a cell anymore)
     *
     * @param caster The switch spell caster
     * @param target The other fighter to switch with
     */
    public void apply(Fighter caster, Fighter target) {
        if (caster.dead() || target.dead()) {
            return;
        }

        final FightCell casterCell = caster.cell();
        final FightCell targetCell = target.cell();

        // Unset cells
        casterCell.removeFighter(caster);
        targetCell.removeFighter(target);

        // Switch cells
        caster.move(targetCell);
        target.move(casterCell);

        // Synchronize positions with client
        fight.send(ActionEffect.teleport(caster, caster, targetCell));
        fight.send(ActionEffect.teleport(caster, target, casterCell));
    }
}
