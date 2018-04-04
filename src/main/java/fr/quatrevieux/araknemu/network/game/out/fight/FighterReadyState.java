package fr.quatrevieux.araknemu.network.game.out.fight;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * Notify the ready state of the fighter
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L213
 */
final public class FighterReadyState {
    final private Fighter fighter;

    public FighterReadyState(Fighter fighter) {
        this.fighter = fighter;
    }

    @Override
    public String toString() {
        return "GR" + (fighter.ready() ? "1" : "0") + fighter.id();
    }
}
