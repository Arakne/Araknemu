package fr.quatrevieux.araknemu.network.game.out.fight.turn;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

import java.util.Collection;

/**
 * Send fighters information between two turns
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L326
 */
final public class TurnMiddle {
    final private Collection<Fighter> fighters;

    public TurnMiddle(Collection<Fighter> fighters) {
        this.fighters = fighters;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("GTM");

        for(Fighter fighter : fighters){
            sb.append('|').append(fighter.id()).append(';');

            // @todo fighter dead
//            if(fighter.dead()){
//                sb.append(1);
//                continue;
//            }

            sb.append("0;").append(fighter.life().current()).append(';')
                .append(fighter.characteristics().get(Characteristic.ACTION_POINT)).append(';')
                .append(fighter.characteristics().get(Characteristic.MOVEMENT_POINT)).append(';')
                .append(fighter.cell().id()).append(';') // @todo set cell to -1 when hidden
                .append(fighter.life().max())
            ;
        }

        return sb.toString();
    }
}
