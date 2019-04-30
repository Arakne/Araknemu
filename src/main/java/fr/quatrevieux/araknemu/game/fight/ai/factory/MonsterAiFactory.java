package fr.quatrevieux.araknemu.game.fight.ai.factory;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Registry of AI factories for monsters
 */
final public class MonsterAiFactory implements AiFactory {
    final private Map<String, AiFactory> factories = new HashMap<>();

    public void register(String type, AiFactory factory) {
        factories.put(type, factory);
    }

    @Override
    public Optional<AI> create(Fighter fighter) {
        // @todo visitor
        if (fighter instanceof MonsterFighter) {
            MonsterFighter monsterFighter = (MonsterFighter) fighter;

            return factories.get(monsterFighter.monster().ai()).create(fighter);
        }

        return Optional.empty();
    }
}
