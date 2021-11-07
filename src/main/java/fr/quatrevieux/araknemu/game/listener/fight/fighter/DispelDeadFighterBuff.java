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

package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterDie;
import fr.quatrevieux.araknemu.network.game.out.fight.AddBuff;
import fr.quatrevieux.araknemu.network.game.out.fight.ClearAllBuffs;

/**
 * Dispel all buff casted by the dead fighter
 */
public final class DispelDeadFighterBuff implements Listener<FighterDie> {
    private final Fight fight;

    public DispelDeadFighterBuff(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void on(FighterDie event) {
        if (removeBuffByCaster(event.fighter())) {
            synchronizeBuffs();
        }
    }

    @Override
    public Class<FighterDie> event() {
        return FighterDie.class;
    }

    /**
     * Remove the buff from the given caster
     *
     * @return true if there is at least one dispelled buff
     */
    private boolean removeBuffByCaster(PassiveFighter caster) {
        boolean hasChange = false;

        for (Fighter fighter : fight.fighters()) {
            hasChange |= fighter.buffs().removeByCaster(caster);
        }

        return hasChange;
    }

    /**
     * Synchronize active buffs with clients
     * Clear all buffs and send active ones
     */
    private void synchronizeBuffs() {
        fight.send(new ClearAllBuffs());

        for (Fighter fighter : fight.fighters()) {
            for (Buff buff : fighter.buffs()) {
                fight.send(new AddBuff(buff));
            }
        }
    }
}
