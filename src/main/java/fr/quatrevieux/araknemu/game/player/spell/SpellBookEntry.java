package fr.quatrevieux.araknemu.game.player.spell;

import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.game.player.spell.event.SpellMoved;
import fr.quatrevieux.araknemu.game.player.spell.event.SpellUpgraded;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellLevels;

/**
 * Entry for the spell book
 */
final public class SpellBookEntry {
    final private PlayerSpell entity;
    final private SpellLevels spell;

    private SpellBook spellBook;

    public SpellBookEntry(PlayerSpell entity, SpellLevels spell) {
        this.entity = entity;
        this.spell = spell;
    }

    /**
     * Get the spell
     */
    public Spell spell() {
        return spell.level(entity.level());
    }

    /**
     * Get the spell position
     */
    public int position() {
        return entity.position();
    }

    /**
     * This entry is a class spell ?
     */
    public boolean classSpell() {
        return entity.classSpell();
    }

    /**
     * Move the spell to a new position
     *
     * @param position The target position
     */
    public void move(int position) {
        if (position < 1) {
            throw new IllegalArgumentException("Bad position");
        }

        spellBook.freePosition(this);
        entity.setPosition(position);
        spellBook.indexing(this);

        spellBook.dispatch(new SpellMoved(this));
    }

    /**
     * Upgrade the spell to the next level
     */
    public void upgrade() {
        if (entity.level() == spell.max()) {
            throw new IllegalStateException("Maximum spell level reached");
        }

        int nextLevel = entity.level() + 1;
        Spell nextSpell = spell.level(nextLevel);

        if (!spellBook.canUpgrade(nextSpell)) {
            throw new IllegalStateException("Cannot upgrade spell");
        }

        spellBook.removePointsForUpgrade(nextSpell);
        entity.setLevel(nextLevel);

        spellBook.dispatch(new SpellUpgraded(this));
    }

    /**
     * Get the entity
     *
     * /!\ Internal method for listeners
     */
    public PlayerSpell entity() {
        return entity;
    }

    /**
     * Attach the spell book to the entry
     *
     * @param spellBook SpellBook to attach
     */
    SpellBookEntry attach(SpellBook spellBook) {
        if (this.spellBook != null) {
            throw new IllegalStateException("SpellBook is already set");
        }

        this.spellBook = spellBook;

        return this;
    }
}
