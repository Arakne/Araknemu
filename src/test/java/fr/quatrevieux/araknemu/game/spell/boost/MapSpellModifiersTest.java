package fr.quatrevieux.araknemu.game.spell.boost;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MapSpellModifiersTest {
    private Map<SpellsBoosts.Modifier, Integer> map;
    private MapSpellModifiers modifiers;

    @BeforeEach
    void setUp() {
        map = new HashMap<>();
        modifiers = new MapSpellModifiers(5, map);
    }

    @Test
    void spellId() {
        assertEquals(5, modifiers.spellId());
    }

    @Test
    void valueNotFound() {
        assertEquals(0, modifiers.value(SpellsBoosts.Modifier.DAMAGE));
    }

    @Test
    void valueFound() {
        map.put(SpellsBoosts.Modifier.DAMAGE, 5);

        assertEquals(5, modifiers.value(SpellsBoosts.Modifier.DAMAGE));
    }

    @Test
    void has() {
        map.put(SpellsBoosts.Modifier.DAMAGE, 5);

        assertTrue(modifiers.has(SpellsBoosts.Modifier.DAMAGE));
        assertFalse(modifiers.has(SpellsBoosts.Modifier.LAUNCH_LINE));
    }

    @Test
    void range() {
        map.put(SpellsBoosts.Modifier.RANGE, 5);
        assertEquals(5, modifiers.range());
    }

    @Test
    void modifiableRange() {
        assertFalse(modifiers.modifiableRange());

        map.put(SpellsBoosts.Modifier.MODIFIABLE_RANGE, 5);
        assertTrue(modifiers.modifiableRange());
    }

    @Test
    void damage() {
        map.put(SpellsBoosts.Modifier.DAMAGE, 5);
        assertEquals(5, modifiers.damage());
    }

    @Test
    void heal() {
        map.put(SpellsBoosts.Modifier.HEAL, 5);
        assertEquals(5, modifiers.heal());
    }

    @Test
    void apCost() {
        map.put(SpellsBoosts.Modifier.AP_COST, 5);
        assertEquals(5, modifiers.apCost());
    }

    @Test
    void delay() {
        map.put(SpellsBoosts.Modifier.REDUCE_DELAY, 5);
        assertEquals(5, modifiers.delay());
    }

    @Test
    void hasFixedDelay() {
        assertFalse(modifiers.hasFixedDelay());

        map.put(SpellsBoosts.Modifier.SET_DELAY, 5);
        assertTrue(modifiers.hasFixedDelay());
    }

    @Test
    void fixedDelay() {
        map.put(SpellsBoosts.Modifier.SET_DELAY, 5);
        assertEquals(5, modifiers.fixedDelay());
    }

    @Test
    void launchOutline() {
        assertFalse(modifiers.launchOutline());

        map.put(SpellsBoosts.Modifier.LAUNCH_LINE, 5);
        assertTrue(modifiers.launchOutline());
    }

    @Test
    void lineOfSight() {
        assertFalse(modifiers.lineOfSight());

        map.put(SpellsBoosts.Modifier.LINE_OF_SIGHT, 5);
        assertTrue(modifiers.lineOfSight());
    }

    @Test
    void launchPerTarget() {
        map.put(SpellsBoosts.Modifier.LAUNCH_PER_TARGET, 5);
        assertEquals(5, modifiers.launchPerTarget());
    }

    @Test
    void launchPerTurn() {
        map.put(SpellsBoosts.Modifier.LAUNCH_PER_TURN, 5);
        assertEquals(5, modifiers.launchPerTurn());
    }

    @Test
    void criticalHit() {
        map.put(SpellsBoosts.Modifier.CRITICAL, 5);
        assertEquals(5, modifiers.criticalHit());
    }
}
