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

package fr.quatrevieux.araknemu.game.fight.ai.proxy;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import fr.quatrevieux.araknemu.game.fight.turn.TurnPoints;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;

/**
 * Proxy class for override turn
 *
 * Note: this object is immutable
 */
public final class ProxyTurn implements Turn {
    private final AI<?> ai;
    private final ActiveFighter fighter;

    /**
     * @param ai The real AI instance (i.e. not the proxy one, because it will cause a cyclic reference)
     * @param fighter The overridden fighter instance
     */
    public ProxyTurn(AI<?> ai, ActiveFighter fighter) {
        this.ai = ai;
        this.fighter = fighter;
    }

    @Override
    public ActiveFighter fighter() {
        return fighter;
    }

    @Override
    public boolean active() {
        return ai.turn().active();
    }

    @Override
    public void perform(Action action) throws FightException {
        throw new UnsupportedOperationException("This is a proxy turn");
    }

    @Override
    public void later(Runnable nextAction) {
        throw new UnsupportedOperationException("This is a proxy turn");
    }

    @Override
    public TurnPoints points() {
        return ai.turn().points();
    }

    @Override
    public void stop() {
        throw new UnsupportedOperationException("This is a proxy turn");
    }
}
