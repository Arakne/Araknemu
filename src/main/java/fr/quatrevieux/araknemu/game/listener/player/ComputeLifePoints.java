package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.characteristic.event.CharacteristicsChanged;

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
        player.properties().life().rebuild();
    }

    @Override
    public Class<CharacteristicsChanged> event() {
        return CharacteristicsChanged.class;
    }
}
