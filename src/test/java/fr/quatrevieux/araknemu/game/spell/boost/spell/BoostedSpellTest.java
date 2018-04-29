package fr.quatrevieux.araknemu.game.spell.boost.spell;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import fr.quatrevieux.araknemu.game.spell.boost.MapSpellModifiers;
import fr.quatrevieux.araknemu.game.spell.boost.SpellsBoosts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BoostedSpellTest extends GameBaseCase {
    private Spell spell;
    private Map<SpellsBoosts.Modifier, Integer> boostMap;
    private MapSpellModifiers modifiers;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushSpells();

        boostMap = new HashMap<>();
        modifiers = new MapSpellModifiers(2, boostMap);
        spell = new BoostedSpell(
            container.get(SpellService.class).get(2).level(1),
            modifiers
        );
    }

    @Test
    void getters() {
        assertEquals(2, spell.id());
        assertEquals(1, spell.level());
        assertEquals(100, spell.criticalFailure());
        assertEquals(6, spell.minPlayerLevel());
        assertFalse(spell.endsTurnOnFailure());
        assertInstanceOf(BoostedSpellConstraints.class, spell.constraints());
    }

    @Test
    void apCost() {
        assertEquals(3, spell.apCost());

        boostMap.put(SpellsBoosts.Modifier.AP_COST, 2);

        assertEquals(1, spell.apCost());
    }

    @Test
    void criticalHit() {
        assertEquals(50, spell.criticalHit());

        boostMap.put(SpellsBoosts.Modifier.CRITICAL, 30);

        assertEquals(20, spell.criticalHit());
    }

    @Test
    void modifiableRange() {
        assertTrue(spell.modifiableRange());

        boostMap.put(SpellsBoosts.Modifier.MODIFIABLE_RANGE, 1);
        assertTrue(spell.modifiableRange());
    }

    @Test
    void effects() {
        assertContainsOnly(BoostedSpellEffect.class, spell.effects());

        boostMap.put(SpellsBoosts.Modifier.DAMAGE, 5);

        assertEquals(1, spell.effects().get(0).min());
        assertEquals(2, spell.effects().get(0).max());
        assertEquals(0, spell.effects().get(0).boost());
        assertEquals(1, spell.effects().get(1).min());
        assertEquals(0, spell.effects().get(1).max());
        assertEquals(5, spell.effects().get(1).boost());
    }

    @Test
    void criticalEffects() {
        assertContainsOnly(BoostedSpellEffect.class, spell.criticalEffects());

        boostMap.put(SpellsBoosts.Modifier.DAMAGE, 5);

        assertEquals(2, spell.criticalEffects().get(0).min());
        assertEquals(0, spell.criticalEffects().get(0).max());
        assertEquals(0, spell.criticalEffects().get(0).boost());
        assertEquals(2, spell.criticalEffects().get(1).min());
        assertEquals(0, spell.criticalEffects().get(1).max());
        assertEquals(5, spell.criticalEffects().get(1).boost());
    }
}
