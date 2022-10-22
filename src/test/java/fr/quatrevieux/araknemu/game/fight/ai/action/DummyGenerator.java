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

package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.factory.ActionsFactory;

import java.time.Duration;
import java.util.Optional;

public class DummyGenerator implements ActionGenerator {
    @Override
    public void initialize(AI ai) {}

    @Override
    public Optional<Action> generate(AI ai, AiActionFactory actions) {
        return Optional.of(
            new Action() {
                @Override
                public boolean validate(Turn turn) {
                    return true;
                }

                @Override
                public ActionResult start() {
                    return new ActionResult() {
                        @Override
                        public int action() {
                            return 0;
                        }

                        @Override
                        public Fighter performer() {
                            return null;
                        }

                        @Override
                        public Object[] arguments() {
                            return new Object[0];
                        }

                        @Override
                        public boolean success() {
                            return false;
                        }

                        @Override
                        public boolean secret() {
                            return false;
                        }

                        @Override
                        public void apply(FightTurn turn) {

                        }
                    };
                }

                @Override
                public Fighter performer() {
                    return null;
                }

                @Override
                public ActionType type() {
                    return null;
                }

                @Override
                public Duration duration() {
                    return Duration.ZERO;
                }
            }
        );
    }
}
