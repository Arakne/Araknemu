package fr.quatrevieux.araknemu.game.fight.ai.factory.type;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.action.*;
import fr.quatrevieux.araknemu.game.fight.ai.factory.AiFactory;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

import java.util.Optional;

/**
 * AI for run away monsters (like Tofu)
 *
 * This AI use the smallest MP quantity for attack, and flees farthest from enemies
 */
final public class Runaway implements AiFactory {
    @Override
    public Optional<AI> create(Fighter fighter) {
        final Simulator simulator = fighter.fight().attachment(Simulator.class);

        return Optional.of(
            new AI(fighter, new ActionGenerator[] {
                new Attack(simulator),
                new MoveToAttack(simulator),
                new MoveFarEnemies(),
                new Boost(simulator),
            })
        );
    }
}
