package fr.quatrevieux.araknemu.network.game.out.spell;

import fr.quatrevieux.araknemu.game.spell.boost.SpellsBoosts;

/**
 * Set the spell boost modifier
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Spells.as#L69
 */
final public class SpellBoost {
    final private int spellId;
    final private SpellsBoosts.Modifier modifier;
    final private int value;

    public SpellBoost(int spellId, SpellsBoosts.Modifier modifier, int value) {
        this.spellId = spellId;
        this.modifier = modifier;
        this.value = value;
    }

    @Override
    public String toString() {
        return "SB" + modifier.effectId() + ";" + spellId + ";" + value;
    }
}
