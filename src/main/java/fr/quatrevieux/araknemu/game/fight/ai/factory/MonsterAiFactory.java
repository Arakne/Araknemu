package fr.quatrevieux.araknemu.game.fight.ai.factory;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Registry of AI factories for monsters
 */
final public class MonsterAiFactory implements AiFactory {
    final private Map<String, AiFactory> factories = new HashMap<>();

    class ResolveAi implements FighterOperation {
        private AI ai;

        @Override
        public void onMonster(MonsterFighter fighter) {
            factories.get(fighter.monster().ai())
                .create(fighter)
                .ifPresent(ai -> this.ai = ai)
            ;
        }

        public Optional<AI> get() {
            return Optional.ofNullable(ai);
        }
    }

    public void register(String type, AiFactory factory) {
        factories.put(type, factory);
    }

    @Override
    public Optional<AI> create(Fighter fighter) {
        return fighter.apply(new ResolveAi()).get();
    }
}
