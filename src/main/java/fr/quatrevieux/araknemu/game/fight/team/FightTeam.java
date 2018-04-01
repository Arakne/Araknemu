package fr.quatrevieux.araknemu.game.fight.team;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.world.util.Sender;

import java.util.Collection;
import java.util.List;

/**
 * Team for fight
 */
public interface FightTeam extends Sender {
    /**
     * Get the team number (0 or 1)
     */
    public int number();

    /**
     * Get start places
     */
    public List<Integer> startPlaces();

    /**
     * Get the fighter list
     */
    public Collection<? extends Fighter> fighters();

    /**
     * Send packet to all players
     */
    @Override
    public void send(Object packet);
}
