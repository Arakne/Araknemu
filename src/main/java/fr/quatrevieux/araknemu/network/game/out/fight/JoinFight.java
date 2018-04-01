package fr.quatrevieux.araknemu.network.game.out.fight;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;

/**
 * Initialise the fight
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L110
 */
final public class JoinFight {
    final private Fight fight;

    public JoinFight(Fight fight) {
        this.fight = fight;
    }

    @Override
    public String toString() {
        return "GJK" +
            fight.state().id() + "|" +
            (fight.type().canCancel() ? "1" : "0") + "|" +
            "1|0|" + // Show ready / cancel banner + not spectator
            (fight.type().hasPlacementTimeLimit() ? fight.state(PlacementState.class).remainingTime() : "") + "|" +
            fight.type().id()
        ;
    }
}
