package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterDie;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;

/**
 * Remove the dead fighter from fight
 */
final public class RemoveDeadFighter implements Listener<FighterDie> {
    final private Fight fight;

    public RemoveDeadFighter(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void on(FighterDie event) {
        event.fighter().cell().removeFighter();

        // Stop turn if it's the playing fighter
        fight.turnList().current()
            .filter(turn -> turn.fighter().equals(event.fighter()))
            .ifPresent(FightTurn::stop)
        ;
    }

    @Override
    public Class<FighterDie> event() {
        return FighterDie.class;
    }
}
