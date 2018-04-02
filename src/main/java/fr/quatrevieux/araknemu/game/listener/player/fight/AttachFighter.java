package fr.quatrevieux.araknemu.game.listener.player.fight;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.event.FightJoined;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayerFighter;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Attach the fighter to the player
 */
final public class AttachFighter implements Listener<FightJoined> {
    final private GamePlayer player;

    public AttachFighter(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(FightJoined event) {
        player.attachFighter(PlayerFighter.class.cast(event.fighter()));
    }

    @Override
    public Class<FightJoined> event() {
        return FightJoined.class;
    }
}
