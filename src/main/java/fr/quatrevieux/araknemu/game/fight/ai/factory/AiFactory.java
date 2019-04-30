package fr.quatrevieux.araknemu.game.fight.ai.factory;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

import java.util.Optional;

/**
 * Factory for AI
 */
public interface AiFactory {
    /**
     * Create the AI for the given fighter
     *
     * @param fighter The fighter to control by the AI
     *
     * @return The AI, or an empty optional if cannot be resolved
     */
    public Optional<AI> create(Fighter fighter);
}
