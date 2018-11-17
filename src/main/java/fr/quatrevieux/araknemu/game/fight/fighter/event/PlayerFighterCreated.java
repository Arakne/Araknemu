package fr.quatrevieux.araknemu.game.fight.fighter.event;

import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;

/**
 * A new player fighter is created
 */
final public class PlayerFighterCreated {
    final private PlayerFighter fighter;

    public PlayerFighterCreated(PlayerFighter fighter) {
        this.fighter = fighter;
    }

    public PlayerFighter fighter() {
        return fighter;
    }
}
