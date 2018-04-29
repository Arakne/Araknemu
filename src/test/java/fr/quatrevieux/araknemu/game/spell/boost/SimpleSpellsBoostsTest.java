package fr.quatrevieux.araknemu.game.spell.boost;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import fr.quatrevieux.araknemu.game.spell.boost.spell.BoostedSpell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleSpellsBoostsTest extends GameBaseCase {
    private SimpleSpellsBoosts boosts;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        boosts = new SimpleSpellsBoosts();

        dataSet.pushSpells();
    }

    @Test
    void boostUnknownSpellWillSetTheValue() {
        assertEquals(15, boosts.boost(5, SpellsBoosts.Modifier.DAMAGE, 15));
        assertEquals(15, boosts.modifiers(5).damage());
    }

    @Test
    void boostUnknownModifierWillSetTheValue() {
        boosts.boost(5, SpellsBoosts.Modifier.CRITICAL, 15);

        assertEquals(15, boosts.boost(5, SpellsBoosts.Modifier.DAMAGE, 15));
        assertEquals(15, boosts.modifiers(5).damage());
    }

    @Test
    void boostWillAddValueModifier() {
        boosts.boost(5, SpellsBoosts.Modifier.CRITICAL, 10);
        assertEquals(20, boosts.boost(5, SpellsBoosts.Modifier.CRITICAL, 10));
        assertEquals(20, boosts.modifiers(5).criticalHit());
    }

    @Test
    void setNewValue() {
        assertEquals(5, boosts.set(1, SpellsBoosts.Modifier.DAMAGE, 5));
        assertEquals(5, boosts.modifiers(1).damage());
    }

    @Test
    void setOverrideOldValue() {
        boosts.set(1, SpellsBoosts.Modifier.DAMAGE, 5);

        assertEquals(10, boosts.set(1, SpellsBoosts.Modifier.DAMAGE, 10));
        assertEquals(10, boosts.modifiers(1).damage());
    }

    @Test
    void unsetNotSetValueDoNothing() {
        boosts.unset(1, SpellsBoosts.Modifier.DAMAGE);
    }

    @Test
    void unsetWillRemoveValue() {
        boosts.set(1, SpellsBoosts.Modifier.DAMAGE, 5);
        boosts.unset(1, SpellsBoosts.Modifier.DAMAGE);

        assertFalse(boosts.modifiers(1).has(SpellsBoosts.Modifier.DAMAGE));
    }

    @Test
    void getNotBoosted() throws ContainerException {
        Spell spell = container.get(SpellService.class).get(3).level(2);

        assertSame(spell, boosts.get(spell));
    }

    @Test
    void getBoosted() throws ContainerException {
        boosts.set(3, SpellsBoosts.Modifier.DAMAGE, 5);
        boosts.set(3, SpellsBoosts.Modifier.LINE_OF_SIGHT, 1);

        Spell spell = container.get(SpellService.class).get(3).level(2);

        Spell boosted = boosts.get(spell);

        assertInstanceOf(BoostedSpell.class, boosted);

        assertEquals(3, boosted.effects().get(0).min());
        assertEquals(5, boosted.effects().get(0).boost());
        assertEquals(7, boosted.effects().get(0).max());
        assertFalse(boosted.constraints().lineOfSight());
    }

    @Test
    void all() {
        boosts.set(3, SpellsBoosts.Modifier.DAMAGE, 5);
        boosts.set(6, SpellsBoosts.Modifier.LINE_OF_SIGHT, 1);

        assertCount(2, boosts.all());
        assertContainsOnly(MapSpellModifiers.class, boosts.all());
    }
}
