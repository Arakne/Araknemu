package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Factory for make fighters
 */
public interface FighterFactory {
    /**
     * Create a PlayerFighter from a game player
     *
     * @param player The player
     *
     * @return The PlayerFighter
     */
    public PlayerFighter create(GamePlayer player);
}
