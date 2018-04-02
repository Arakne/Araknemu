package fr.quatrevieux.araknemu.game.fight.event;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * The player has join the fight
 */
final public class FightJoined {
    final private Fight fight;
    final private Fighter fighter;

    public FightJoined(Fight fight, Fighter fighter) {
        this.fight = fight;
        this.fighter = fighter;
    }

    public Fight fight() {
        return fight;
    }

    public Fighter fighter() {
        return fighter;
    }
}
