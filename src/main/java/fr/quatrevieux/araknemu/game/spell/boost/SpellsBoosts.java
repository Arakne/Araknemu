package fr.quatrevieux.araknemu.game.spell.boost;

import fr.quatrevieux.araknemu.game.spell.Spell;

import java.util.Collection;

/**
 * Handle spells boosts effects
 */
public interface SpellsBoosts {
    public enum Modifier {
        RANGE(281),
        MODIFIABLE_RANGE(282),
        DAMAGE(283),
        HEAL(284),
        AP_COST(285),
        REDUCE_DELAY(286),
        CRITICAL(287),
        LAUNCH_LINE(288),
        LINE_OF_SIGHT(289),
        LAUNCH_PER_TURN(290),
        LAUNCH_PER_TARGET(291),
        SET_DELAY(292);

        final private int effectId;

        Modifier(int effectId) {
            this.effectId = effectId;
        }

        public int effectId() {
            return effectId;
        }
    }

    /**
     * Boost the spell
     *
     * @param spellId The spell to boost
     * @param modifier The effect modifier
     * @param value The boosted value
     *
     * @return The new boosted value
     */
    public int boost(int spellId, Modifier modifier, int value);

    /**
     * Set the modifier value
     *
     * @param spellId The spell to modify
     * @param modifier The spell modifier
     * @param value The new value
     *
     * @return The new value
     */
    public int set(int spellId, Modifier modifier, int value);

    /**
     * Remove the modifier
     *
     * @param spellId Spell
     * @param modifier The modifier
     */
    public void unset(int spellId, Modifier modifier);

    /**
     * Get spell modifiers for the spell id
     *
     * @param spellId The spell to check
     */
    public SpellModifiers modifiers(int spellId);

    /**
     * Get the boosted spell
     *
     * @param spell spell to boost
     */
    public Spell get(Spell spell);

    /**
     * Get all spells modifiers
     */
    public Collection<SpellModifiers> all();
}