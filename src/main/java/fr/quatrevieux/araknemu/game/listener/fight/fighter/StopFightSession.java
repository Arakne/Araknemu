package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.event.FightLeaved;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;

/**
 * Stop the fight session on leave fight
 */
final public class StopFightSession implements Listener<FightLeaved> {
    final private PlayerFighter fighter;

    public StopFightSession(PlayerFighter fighter) {
        this.fighter = fighter;
    }

    @Override
    public void on(FightLeaved event) {
        fighter.player().stop(fighter);
    }

    @Override
    public Class<FightLeaved> event() {
        return FightLeaved.class;
    }
}
