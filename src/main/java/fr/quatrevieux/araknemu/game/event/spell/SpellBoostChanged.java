package fr.quatrevieux.araknemu.game.event.spell;

import fr.quatrevieux.araknemu.game.spell.boost.SpellsBoosts;

/**
 * Event trigger when a spell boost is changed
 */
final public class SpellBoostChanged {
    final private int spellId;
    final private SpellsBoosts.Modifier modifier;
    final private int value;

    public SpellBoostChanged(int spellId, SpellsBoosts.Modifier modifier, int value) {
        this.spellId = spellId;
        this.modifier = modifier;
        this.value = value;
    }

    public int spellId() {
        return spellId;
    }

    public SpellsBoosts.Modifier modifier() {
        return modifier;
    }

    public int value() {
        return value;
    }
}
