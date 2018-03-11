package fr.quatrevieux.araknemu.game.player.spell;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The player spell book
 */
final public class SpellBook {
    final private Map<Integer, SpellBookEntry> entries;

    public SpellBook(Collection<SpellBookEntry> entries) {
        this.entries = entries
            .stream()
            .collect(
                Collectors.toMap(
                    e -> e.spell().id(),
                    Function.identity()
                )
            )
        ;
    }

    /**
     * Get all available spells
     */
    public Collection<SpellBookEntry> all() {
        return entries.values();
    }
}
