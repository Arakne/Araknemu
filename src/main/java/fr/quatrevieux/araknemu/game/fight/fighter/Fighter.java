package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffList;
import fr.quatrevieux.araknemu.game.fight.castable.weapon.CastableWeapon;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.spell.SpellList;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.quatrevieux.araknemu.game.world.map.Direction;

/**
 * Base fighter
 *
 * @todo extends Creature
 */
public interface Fighter extends Dispatcher {
    /**
     * Initialise the fighter when fight started
     */
    public void init();

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
     * Get the fighter orientation
     */
    public Direction orientation();

    /**
     * Change the fighter orientation
     */
    public void setOrientation(Direction orientation);

    /**
     * Go to the given cell
     */
    public void move(FightCell cell);

    /**
     * Get the fighter sprite
     */
    public Sprite sprite();

    /**
     * Get the fighter life
     */
    public FighterLife life();

    /**
     * Get the fighter total characteristics
     */
    public FighterCharacteristics characteristics();

    /**
     * Get the fighter spells
     */
    public SpellList spells();

    /**
     * Get the weapon
     *
     * @throws fr.quatrevieux.araknemu.game.fight.exception.FightException When cannot get any weapon on the fighter
     */
    public CastableWeapon weapon();

    /**
     * Get the current buffs
     */
    public BuffList buffs();

    /**
     * Get the fighter states
     */
    public States states();

    /**
     * Attach an attribute to the fighter
     *
     * @param key The attachment key
     * @param value The attached value
     *
     * @see Fighter#attachment(Object) For get the attachment
     */
    public void attach(Object key, Object value);

    /**
     * Attach an object by its class
     *
     * @param value The attachment
     *
     * @see Fighter#attachment(Class) For get the attachment
     */
    default public void attach(Object value) {
        attach(value.getClass(), value);
    }

    /**
     * Get an attachment by its key
     *
     * @param key The attachment key
     *
     * @return The attached value
     *
     * @see Fighter#attach(Object, Object) For set the attachment
     */
    public Object attachment(Object key);

    /**
     * Get attachment by its class
     *
     * @param type The attachment class
     *
     * @return The attachment
     *
     * @see Fighter#attach(Object) Fir set the attachment
     */
    default public <T> T attachment(Class<T> type) {
        return type.cast(attachment((Object) type));
    }

    /**
     * Get the fighter level
     */
    public int level();

    /**
     * Get the fighter team
     */
    public FightTeam team();

    /**
     * Get the fight
     */
    public Fight fight();

    /**
     * Join the fight
     *
     * @param fight Fight to join
     * @param startCell The start cell
     */
    public void joinFight(Fight fight, FightCell startCell);

    /**
     * Check if the fighter is ready for fight
     */
    public boolean ready();

    /**
     * Check if the player is dead
     */
    public boolean dead();

    /**
     * Start to play the turn
     *
     * @param turn The fighter turn
     */
    public void play(FightTurn turn);

    /**
     * Stop the turn
     */
    public void stop();

    /**
     * Check if the fighter is on the fight (The fight is set and is on a cell)
     */
    public boolean isOnFight();

    /**
     * Check if the fighter is the team leader
     */
    default public boolean isTeamLeader() {
        return equals(team().leader());
    }
}
