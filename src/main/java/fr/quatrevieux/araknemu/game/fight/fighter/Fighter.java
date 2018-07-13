package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffList;
import fr.quatrevieux.araknemu.game.fight.castable.weapon.CastableWeapon;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.item.type.Weapon;
import fr.quatrevieux.araknemu.game.spell.SpellList;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;

/**
 * Base fighter
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
     * Get the fighter level
     */
    public int level();

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
     * Check if the fighter is the team leader
     */
    default public boolean isTeamLeader() {
        return equals(team().leader());
    }
}
