package fr.quatrevieux.araknemu.game.event.listener.player.spell;

import fr.quatrevieux.araknemu.data.living.repository.player.PlayerSpellRepository;
import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.spell.SpellLearned;

/**
 * Save the new learned spell
 */
final public class SaveLearnedSpell implements Listener<SpellLearned> {
    final private PlayerSpellRepository repository;

    public SaveLearnedSpell(PlayerSpellRepository repository) {
        this.repository = repository;
    }

    @Override
    public void on(SpellLearned event) {
        repository.add(event.entry().entity());
    }

    @Override
    public Class<SpellLearned> event() {
        return SpellLearned.class;
    }
}
