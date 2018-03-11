package fr.quatrevieux.araknemu.game.spell;

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.data.world.repository.SpellTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.spell.adapter.SpellLevelAdapter;
import fr.quatrevieux.araknemu.game.spell.adapter.SpellLevelConstraintAdapter;
import fr.quatrevieux.araknemu.game.spell.effect.SpellTemplateEffectAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class SpellLevelsTest extends GameBaseCase {
    private SpellLevels levels;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushSpells();

        levels = new SpellLevels(container.get(SpellTemplateRepository.class).get(202));
    }

    @Test
    void getters() {
        assertEquals(202, levels.id());
        assertEquals(5, levels.max());
        assertEquals("Morsure du Bouftou", levels.name());
    }

    @Test
    void levelTooLow() {
        assertThrows(NoSuchElementException.class, () -> levels.level(0));
    }

    @Test
    void levelTooHigh() {
        assertThrows(NoSuchElementException.class, () -> levels.level(6));
    }

    @Test
    void level() {
        Spell spell = levels.level(1);

        assertInstanceOf(SpellLevelAdapter.class, spell);
        assertEquals(202, spell.id());
        assertEquals(1, spell.level());
        assertEquals(0, spell.minPlayerLevel());
        assertEquals(4, spell.apCost());
        assertEquals(50, spell.criticalHit());
        assertEquals(100, spell.criticalFailure());
        assertFalse(spell.modifiableRange());
        assertFalse(spell.endsTurnOnFailure());

        assertInstanceOf(SpellLevelConstraintAdapter.class, spell.constraints());
        assertEquals(1, spell.constraints().range().min());
        assertEquals(1, spell.constraints().range().max());
        assertFalse(spell.constraints().lineLaunch());
        assertTrue(spell.constraints().lineOfSight());
        assertFalse(spell.constraints().freeCell());
        assertEquals(3, spell.constraints().launchPerTurn());
        assertEquals(0, spell.constraints().launchPerTarget());
        assertEquals(0, spell.constraints().launchDelay());
        assertArrayEquals(new int[0], spell.constraints().requiredStates());
        assertArrayEquals(new int[] {18, 19, 3, 1, 41}, spell.constraints().forbiddenStates());

        assertCount(1, spell.effects());
        assertContainsOnly(SpellTemplateEffectAdapter.class, spell.effects());
        assertEquals(100, spell.effects().get(0).effect());
        assertEquals(4, spell.effects().get(0).min());
        assertEquals(9, spell.effects().get(0).max());
        assertEquals(0, spell.effects().get(0).special());
        assertEquals(0, spell.effects().get(0).duration());
        assertEquals(0, spell.effects().get(0).probability());
        assertEquals("1d6+3", spell.effects().get(0).text());
        assertEquals(EffectArea.Type.CELL, spell.effects().get(0).area().type());
        assertEquals(0, spell.effects().get(0).area().size());
        assertEquals(0, spell.effects().get(0).target());

        assertCount(1, spell.criticalEffects());
        assertContainsOnly(SpellTemplateEffectAdapter.class, spell.criticalEffects());
        assertEquals(100, spell.criticalEffects().get(0).effect());
        assertEquals(13, spell.criticalEffects().get(0).min());
        assertEquals(0, spell.criticalEffects().get(0).max());
        assertEquals(0, spell.criticalEffects().get(0).special());
        assertEquals(0, spell.criticalEffects().get(0).duration());
        assertEquals(0, spell.criticalEffects().get(0).probability());
        assertEquals("0d0+13", spell.criticalEffects().get(0).text());
        assertEquals(EffectArea.Type.CELL, spell.criticalEffects().get(0).area().type());
        assertEquals(0, spell.criticalEffects().get(0).area().size());
        assertEquals(0, spell.criticalEffects().get(0).target());
    }
}
