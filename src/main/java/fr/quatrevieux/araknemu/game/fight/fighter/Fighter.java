package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;

/**
 * Base fighter
 */
public interface Fighter extends Dispatcher {
    /**
     * Get the fighter ID
     * This value is unique on the fight
     */
    public int id();

    /**
     * Get the current player cell
     */
    public FightCell cell();

    /**
     * Go to the given cell
     */
    public void move(FightCell cell);

    /**
     * Get the fighter sprite
     */
    public Sprite sprite();

    // @todo temporary
    public int currentLife();

    public int maxLife();

    /**
     * Get the fighter total characteristics
     */
    public Characteristics characteristics();

    /**
     * Get the fighter team
     */
    public FightTeam team();

    /**
     * Join a team
     */
    public void join(FightTeam team);

    /**
     * Get the fight
     */
    public Fight fight();

    /**
     * Set the fight one the fighter
     */
    public void setFight(Fight fight);
}
