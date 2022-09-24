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
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

/**
 * Handle teleport effect
 */
public final class TeleportHandler implements EffectHandler {
    private final Fight fight;

    public TeleportHandler(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void handle(CastScope<Fighter> cast, CastScope<Fighter>.EffectScope effect) {
        if (!cast.target().walkable()) {
            return; // @todo exception ?
        }

        cast.caster().move(cast.target());

        fight.send(ActionEffect.teleport(cast.caster(), cast.caster(), cast.target()));
    }

    @Override
    public void buff(CastScope<Fighter> cast, CastScope<Fighter>.EffectScope effect) {
        throw new UnsupportedOperationException("Cannot use Teleport as buff effect");
    }
}
