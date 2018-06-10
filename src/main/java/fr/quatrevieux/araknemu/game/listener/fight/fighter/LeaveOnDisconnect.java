package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.state.LeavableState;
import fr.quatrevieux.araknemu.game.handler.event.Disconnected;

/**
 * Leave the fight when disconnect
 */
final public class LeaveOnDisconnect implements Listener<Disconnected> {
    final private PlayerFighter fighter;

    public LeaveOnDisconnect(PlayerFighter fighter) {
        this.fighter = fighter;
    }

    @Override
    public void on(Disconnected event) {
        fighter.fight().state(LeavableState.class).leave(fighter);
    }

    @Override
    public Class<Disconnected> event() {
        return Disconnected.class;
    }
}
