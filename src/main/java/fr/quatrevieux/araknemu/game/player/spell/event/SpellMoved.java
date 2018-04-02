package fr.quatrevieux.araknemu.game.player.spell.event;

import fr.quatrevieux.araknemu.game.player.spell.SpellBookEntry;

/**
 * The spell is moved
 */
final public class SpellMoved {
    private SpellBookEntry entry;

    public SpellMoved(SpellBookEntry entry) {
        this.entry = entry;
    }

    public SpellBookEntry entry() {
        return entry;
    }
}
