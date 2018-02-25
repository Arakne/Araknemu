package fr.quatrevieux.araknemu.game.event.listener.player;

import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.common.CharacteristicsChanged;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Rebuild the life points when characteristics changed
 */
final public class ComputeLifePoints implements Listener<CharacteristicsChanged> {
    final private GamePlayer player;

    public ComputeLifePoints(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(CharacteristicsChanged event) {
        player.life().rebuild();
    }

    @Override
    public Class<CharacteristicsChanged> event() {
        return CharacteristicsChanged.class;
    }
}
