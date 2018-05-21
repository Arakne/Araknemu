package fr.quatrevieux.araknemu.game.fight.team;

import fr.quatrevieux.araknemu.data.constant.Alignment;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.world.util.Sender;

import java.util.Collection;
import java.util.List;

/**
 * Team for fight
 */
public interface FightTeam extends Sender {
    /**
     * Get the team id (must be unique over the map)
     * Generally the team id is the team leader id
     */
    public int id();

    /**
     * Get the team cell id on the map
     */
    public int cell();

    /**
     * Get the team type
     */
    public int type();

    /**
     * Get the team alignment
     */
    public Alignment alignment();

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

    /**
     * Check if there is at least one alive fighter in the team
     */
    public boolean alive();
}
