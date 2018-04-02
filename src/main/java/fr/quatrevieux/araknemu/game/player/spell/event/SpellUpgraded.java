package fr.quatrevieux.araknemu.game.player.spell.event;

import fr.quatrevieux.araknemu.game.player.spell.SpellBookEntry;

/**
 * The spell is successfully upgraded
 */
final public class SpellUpgraded {
    private SpellBookEntry entry;

    public SpellUpgraded(SpellBookEntry entry) {
        this.entry = entry;
    }

    public SpellBookEntry entry() {
        return entry;
    }
}
