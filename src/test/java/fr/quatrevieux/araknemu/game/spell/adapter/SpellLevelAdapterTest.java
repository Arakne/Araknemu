/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.spell.adapter;

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.data.world.entity.SpellTemplate;
import fr.quatrevieux.araknemu.data.world.repository.SpellTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffectService;
import fr.quatrevieux.araknemu.game.spell.effect.SpellTemplateEffectAdapter;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpellLevelAdapterTest extends GameBaseCase {
    private SpellTemplateRepository repository;
    private SpellEffectService effectService;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushSpells();

        repository = container.get(SpellTemplateRepository.class);
        effectService = container.get(SpellEffectService.class);
    }

    @Test
    void getters() {
        SpellLevelAdapter spell = new SpellLevelAdapter(
            1, repository.get(202), repository.get(202).levels()[0],
            new ArrayList<>(),
            new ArrayList<>()
        );

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

        assertEquals(0, spell.spriteId());
        assertEquals("0,1,1", spell.spriteArgs());
    }

    @Test
    void effectsWithTarget() {
        SpellTemplate template = repository.get(6);
        SpellLevelAdapter spell = new SpellLevelAdapter(
            3, template, template.levels()[2],
            effectService.makeAll(template.levels()[2].effects(), template.levels()[2].effectAreas(), template.targets()),
            effectService.makeAll(template.levels()[2].criticalEffects(), template.levels()[2].effectAreas().subList(1, 2), template.targets())
        );

        assertCount(1, spell.effects());
        assertEquals(new SpellEffectTarget(4), spell.effects().get(0).target());

        assertCount(1, spell.criticalEffects());
        assertEquals(new SpellEffectTarget(4), spell.criticalEffects().get(0).target());
    }

    @Test
    void effectsMultiple() {
        SpellTemplate template = repository.get(2);

        SpellLevelAdapter spell = new SpellLevelAdapter(
            3, template, template.levels()[2],
            effectService.makeAll(template.levels()[2].effects(), template.levels()[2].effectAreas(), template.targets()),
            effectService.makeAll(template.levels()[2].criticalEffects(), template.levels()[2].effectAreas().subList(2, 4), template.targets())
        );

        assertCount(2, spell.effects());
        assertContainsOnly(SpellTemplateEffectAdapter.class, spell.effects());
        assertCount(2, spell.criticalEffects());
        assertContainsOnly(SpellTemplateEffectAdapter.class, spell.criticalEffects());

        assertEquals(101, spell.effects().get(0).effect());
        assertEquals(1, spell.effects().get(0).min());
        assertEquals(2, spell.effects().get(0).max());
        assertEquals(0, spell.effects().get(0).special());
        assertEquals(1, spell.effects().get(0).duration());
        assertEquals(0, spell.effects().get(0).probability());
        assertEquals("1d2+0", spell.effects().get(0).text());
        assertEquals(EffectArea.Type.CELL, spell.effects().get(0).area().type());
        assertEquals(0, spell.effects().get(0).area().size());
        assertEquals(SpellEffectTarget.DEFAULT, spell.effects().get(0).target());
        assertEquals(100, spell.effects().get(1).effect());
        assertEquals(2, spell.effects().get(1).min());
        assertEquals(4, spell.effects().get(1).max());
        assertEquals(0, spell.effects().get(1).special());
        assertEquals(0, spell.effects().get(1).duration());
        assertEquals(0, spell.effects().get(1).probability());
        assertEquals("1d3+1", spell.effects().get(1).text());
        assertEquals(EffectArea.Type.CELL, spell.effects().get(1).area().type());
        assertEquals(0, spell.effects().get(1).area().size());
        assertEquals(SpellEffectTarget.DEFAULT, spell.effects().get(1).target());

        assertEquals(101, spell.criticalEffects().get(0).effect());
        assertEquals(2, spell.criticalEffects().get(0).min());
        assertEquals(0, spell.criticalEffects().get(0).max());
        assertEquals(0, spell.criticalEffects().get(0).special());
        assertEquals(1, spell.criticalEffects().get(0).duration());
        assertEquals(0, spell.criticalEffects().get(0).probability());
        assertEquals("0d0+2", spell.criticalEffects().get(0).text());
        assertEquals(EffectArea.Type.CELL, spell.criticalEffects().get(0).area().type());
        assertEquals(0, spell.criticalEffects().get(0).area().size());
        assertEquals(SpellEffectTarget.DEFAULT, spell.criticalEffects().get(0).target());
        assertEquals(100, spell.criticalEffects().get(1).effect());
        assertEquals(5, spell.criticalEffects().get(1).min());
        assertEquals(0, spell.criticalEffects().get(1).max());
        assertEquals(0, spell.criticalEffects().get(1).special());
        assertEquals(0, spell.criticalEffects().get(1).duration());
        assertEquals(0, spell.criticalEffects().get(1).probability());
        assertEquals("0d0+5", spell.criticalEffects().get(1).text());
        assertEquals(EffectArea.Type.CELL, spell.criticalEffects().get(1).area().type());
        assertEquals(0, spell.criticalEffects().get(1).area().size());
        assertEquals(SpellEffectTarget.DEFAULT, spell.criticalEffects().get(1).target());
    }
}
