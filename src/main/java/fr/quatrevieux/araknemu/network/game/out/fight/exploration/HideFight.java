package fr.quatrevieux.araknemu.network.game.out.fight.exploration;

import fr.quatrevieux.araknemu.game.fight.Fight;

/**
 * Hide the fight from exploration map
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L1144
 */
final public class HideFight {
    final private Fight fight;

    public HideFight(Fight fight) {
        this.fight = fight;
    }

    @Override
    public String toString() {
        return "Gc-" + fight.id();
    }
}
