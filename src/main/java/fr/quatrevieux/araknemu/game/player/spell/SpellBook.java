package fr.quatrevieux.araknemu.game.player.spell;

import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.game.event.Dispatcher;

import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The player spell book
 */
final public class SpellBook implements Dispatcher {
    final static public int MAX_POSITION = 24;

    final private Dispatcher dispatcher;

    final private Map<Integer, SpellBookEntry> entries;
    final private SpellBookEntry[] entriesByPosition = new SpellBookEntry[MAX_POSITION];

    public SpellBook(Dispatcher dispatcher, Collection<SpellBookEntry> entries) {
        this.dispatcher = dispatcher;

        this.entries = entries
            .stream()
            .map(entry -> entry.attach(this))
            .collect(
                Collectors.toMap(
                    e -> e.spell().id(),
                    Function.identity()
                )
            )
        ;

        indexingByPosition();
    }

    @Override
    public void dispatch(Object event) {
        dispatcher.dispatch(event);
    }

    /**
     * Get all available spells
     */
    public Collection<SpellBookEntry> all() {
        return entries.values();
    }

    /**
     * Get one spell entry
     *
     * @param spellId The spell id
     */
    public SpellBookEntry entry(int spellId) {
        if (!entries.containsKey(spellId)) {
            throw new NoSuchElementException("The spell book do not contains Spell " + spellId);
        }

        return entries.get(spellId);
    }

    void freePosition(SpellBookEntry entry) {
        if (entry.position() > MAX_POSITION) {
            return;
        }

        entriesByPosition[entry.position() - 1] = null;
    }

    void indexing(SpellBookEntry entry) {
        if (entry.position() > MAX_POSITION) {
            return;
        }

        if (entriesByPosition[entry.position() - 1] != null) {
            entriesByPosition[entry.position() - 1].move(PlayerSpell.DEFAULT_POSITION);
        }

        entriesByPosition[entry.position() - 1] = entry;
    }

    private void indexingByPosition() {
        entries
            .values().stream()
            .filter(entry -> entry.position() <= MAX_POSITION)
            .forEach(entry -> entriesByPosition[entry.position() - 1] = entry)
        ;
    }
}
