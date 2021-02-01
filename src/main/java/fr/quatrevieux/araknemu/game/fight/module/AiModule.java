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

package fr.quatrevieux.araknemu.game.fight.module;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.ai.FighterAI;
import fr.quatrevieux.araknemu.game.fight.ai.factory.AiFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterInitialized;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.event.TurnStarted;

/**
 * Fight module for enable AI
 */
public final class AiModule implements FightModule {
    private final AiFactory factory;

    public AiModule(AiFactory factory) {
        this.factory = factory;
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<FighterInitialized>() {
                @Override
                public void on(FighterInitialized event) {
                    init(event.fighter());
                }

                @Override
                public Class<FighterInitialized> event() {
                    return FighterInitialized.class;
                }
            },
            new Listener<TurnStarted>() {
                @Override
                public void on(TurnStarted event) {
                    start(event.turn());
                }

                @Override
                public Class<TurnStarted> event() {
                    return TurnStarted.class;
                }
            },
        };
    }

    /**
     * Initialize the AI for the fighter (if supported)
     */
    private void init(Fighter fighter) {
        factory.create(fighter).ifPresent(fighter::attach);
    }

    /**
     * Starts the AI on turn start
     */
    private void start(FightTurn turn) {
        final FighterAI ai = turn.fighter().attachment(FighterAI.class);

        if (ai != null) {
            ai.start(turn);
        }
    }
}
