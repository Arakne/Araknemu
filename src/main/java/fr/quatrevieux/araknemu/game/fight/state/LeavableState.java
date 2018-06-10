package fr.quatrevieux.araknemu.game.fight.state;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * State which can be leaved
 */
public interface LeavableState extends FightState {
    /**
     * Leave the fight
     *
     * @param fighter The fighter who leave
     */
    public void leave(Fighter fighter);
}
