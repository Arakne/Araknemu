package fr.quatrevieux.araknemu.game.fight.ai.factory.type;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.action.*;
import fr.quatrevieux.araknemu.game.fight.ai.factory.AiFactory;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

import java.util.Optional;

/**
 * Creates the aggressive AI
 * This is the default AI
 */
final public class Aggressive implements AiFactory {
    @Override
    public Optional<AI> create(Fighter fighter) {
        final Simulator simulator = fighter.fight().attachment(Simulator.class);

        return Optional.of(
            new AI(fighter, new ActionGenerator[] {
                new Attack(simulator),
                new MoveNearEnemy(),
                new TeleportNearEnemy(),
                new Boost(simulator)
            })
        );
    }
}
