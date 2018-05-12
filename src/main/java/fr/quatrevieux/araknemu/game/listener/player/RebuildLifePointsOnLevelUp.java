package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.experience.event.PlayerLevelUp;

/**
 * Rebuild the life points when player win a new level
 *
 * Issue #55 : https://github.com/vincent4vx/Araknemu/issues/55
 */
final public class RebuildLifePointsOnLevelUp implements Listener<PlayerLevelUp> {
    final private GamePlayer player;

    public RebuildLifePointsOnLevelUp(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(PlayerLevelUp event) {
        player.life().rebuild();
    }

    @Override
    public Class<PlayerLevelUp> event() {
        return PlayerLevelUp.class;
    }
}
