package fr.quatrevieux.araknemu.game.player.spell.event;

import fr.quatrevieux.araknemu.game.player.spell.SpellBookEntry;

/**
 * Event trigger when a new spell is learned
 */
final public class SpellLearned {
    final private SpellBookEntry entry;

    public SpellLearned(SpellBookEntry entry) {
        this.entry = entry;
    }

    public SpellBookEntry entry() {
        return entry;
    }
}
