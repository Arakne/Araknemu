package fr.quatrevieux.araknemu.game.listener.service;

import fr.quatrevieux.araknemu.data.living.repository.player.PlayerSpellRepository;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.listener.player.spell.*;
import fr.quatrevieux.araknemu.game.player.event.PlayerLoaded;
import fr.quatrevieux.araknemu.game.listener.player.spell.*;

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
        event.player().dispatcher().add(new SaveSpellPosition(repository));
        event.player().dispatcher().add(new SaveLearnedSpell(repository));
        event.player().dispatcher().add(new SaveUpgradedSpell(event.player(), repository));

        event.player().dispatcher().add(new SendSpellList(event.player()));
        event.player().dispatcher().add(new SendAllSpellBoosts(event.player()));
        event.player().dispatcher().add(new SendLearnedSpell(event.player()));
        event.player().dispatcher().add(new SendUpgradedSpell(event.player()));
        event.player().dispatcher().add(new SendSpellBoost(event.player()));
    }

    @Override
    public Class<PlayerLoaded> event() {
        return PlayerLoaded.class;
    }
}
