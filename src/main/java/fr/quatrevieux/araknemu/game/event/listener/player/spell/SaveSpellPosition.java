package fr.quatrevieux.araknemu.game.event.listener.player.spell;

import fr.quatrevieux.araknemu.data.living.repository.player.PlayerSpellRepository;
import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.spell.SpellMoved;

/**
 * Save the spell position after moved
 */
final public class SaveSpellPosition implements Listener<SpellMoved> {
    final private PlayerSpellRepository repository;

    public SaveSpellPosition(PlayerSpellRepository repository) {
        this.repository = repository;
    }

    @Override
    public void on(SpellMoved event) {
        repository.add(event.entry().entity());
    }

    @Override
    public Class<SpellMoved> event() {
        return SpellMoved.class;
    }
}
