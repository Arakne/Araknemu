package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;

import java.time.Duration;
import java.util.Optional;

public class DummyGenerator implements ActionGenerator {
    @Override
    public void initialize(AI ai) {}

    @Override
    public Optional<Action> generate(AI ai) {
        return Optional.of(
            new Action() {
                @Override
                public boolean validate() {
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
                public void failed() {}

                @Override
                public void end() {}

                @Override
                public Duration duration() {
                    return Duration.ZERO;
                }
            }
        );
    }
}
