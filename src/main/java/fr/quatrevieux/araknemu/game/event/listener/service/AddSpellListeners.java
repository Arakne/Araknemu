package fr.quatrevieux.araknemu.game.event.listener.service;

import fr.quatrevieux.araknemu.data.living.repository.player.PlayerSpellRepository;
import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.common.PlayerLoaded;
import fr.quatrevieux.araknemu.game.event.listener.player.spell.SaveSpellPosition;
import fr.quatrevieux.araknemu.game.event.listener.player.spell.SendSpellList;

/**
 * Register spell listeners when player is loaded
 */
final public class AddSpellListeners implements Listener<PlayerLoaded> {
    final private PlayerSpellRepository repository;

    public AddSpellListeners(PlayerSpellRepository repository) {
        this.repository = repository;
    }

    @Override
    public void on(PlayerLoaded event) {
        event.player().dispatcher().add(new SendSpellList(event.player()));
        event.player().dispatcher().add(new SaveSpellPosition(repository));
    }

    @Override
    public Class<PlayerLoaded> event() {
        return PlayerLoaded.class;
    }
}
