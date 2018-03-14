package fr.quatrevieux.araknemu.game.player.spell;

import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.event.spell.SpellLearned;
import fr.quatrevieux.araknemu.game.spell.SpellLevels;

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
    final private Player player;

    final private Map<Integer, SpellBookEntry> entries;
    final private SpellBookEntry[] entriesByPosition = new SpellBookEntry[MAX_POSITION];

    public SpellBook(Dispatcher dispatcher, Player player, Collection<SpellBookEntry> entries) {
        this.dispatcher = dispatcher;
        this.player = player;

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
        if (!has(spellId)) {
            throw new NoSuchElementException("The spell book do not contains Spell " + spellId);
        }

        return entries.get(spellId);
    }

    /**
     * Check if the user has the spell
     *
     * @param spellId Spell to check
     */
    public boolean has(int spellId) {
        return entries.containsKey(spellId);
    }

    /**
     * Check if the player can learn the spell
     *
     * @param spell Spell to learn
     */
    public boolean canLearn(SpellLevels spell) {
        return !has(spell.id()) && player.level() >= spell.level(1).minPlayerLevel();
    }

    /**
     * Learn a spell
     */
    public void learn(SpellLevels spell) {
        if (!canLearn(spell)) {
            throw new IllegalArgumentException("Cannot learn the spell " + spell.name() + " (" + spell.id() + ")");
        }

        SpellBookEntry entry = new SpellBookEntry(
            new PlayerSpell(player.id(), spell.id(), false),
            spell
        );

        entries.put(spell.id(), entry);
        dispatch(new SpellLearned(entry));
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
