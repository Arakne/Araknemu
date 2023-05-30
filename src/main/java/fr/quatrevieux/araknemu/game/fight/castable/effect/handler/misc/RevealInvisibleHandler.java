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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldObject;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;

import java.util.Collection;
import java.util.Set;

/**
 * Reveal all invisible objects and fighters
 */
public final class RevealInvisibleHandler implements EffectHandler {
    private final Fight fight;

    public RevealInvisibleHandler(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void handle(FightCastScope cast, FightCastScope.EffectScope effect) {
        final Fighter caster = cast.caster();

        revealFighters(caster, effect.targets());
        revealObjects(caster, effect);
    }

    @Override
    public void buff(FightCastScope cast, FightCastScope.EffectScope effect) {
        throw new UnsupportedOperationException("Reveal invisible effect can only be used as direct effect");
    }

    private void revealFighters(Fighter caster, Collection<Fighter> targets) {
        for (Fighter fighter : targets) {
            if (fighter.hidden()) {
                fighter.setHidden(caster, false);
            }
        }
    }

    private void revealObjects(Fighter caster, FightCastScope.EffectScope effect) {
        final Set<FightCell> cells = effect.cells();

        for (BattlefieldObject object : fight.map().objects()) {
            if (cells.contains(object.cell()) && !object.visible(caster)) {
                object.show(caster);
            }
        }
    }
}
