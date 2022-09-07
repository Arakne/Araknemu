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

package fr.quatrevieux.araknemu.network.game.out.fight.action;

import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ActionEffectTest {
    @Test
    void usedMovementPoints() {
        Fighter fighter = Mockito.mock(Fighter.class);
        Mockito.when(fighter.id()).thenReturn(123);

        assertEquals(
            "GA;129;123;123,-4",
            ActionEffect.usedMovementPoints(fighter, 4).toString()
        );
    }

    @Test
    void usedActionPoints() {
        Fighter fighter = Mockito.mock(Fighter.class);
        Mockito.when(fighter.id()).thenReturn(123);

        assertEquals(
            "GA;102;123;123,-4",
            ActionEffect.usedActionPoints(fighter, 4).toString()
        );
    }

    @Test
    void alterLifePoints() {
        Fighter fighter = Mockito.mock(Fighter.class);
        Mockito.when(fighter.id()).thenReturn(123);

        Fighter target = Mockito.mock(Fighter.class);
        Mockito.when(target.id()).thenReturn(321);

        assertEquals(
            "GA;100;123;321,-42",
            ActionEffect.alterLifePoints(fighter, target, -42).toString()
        );
    }

    @Test
    void criticalHitSpell() {
        Fighter fighter = Mockito.mock(Fighter.class);
        Mockito.when(fighter.id()).thenReturn(123);

        Spell spell = Mockito.mock(Spell.class);
        Mockito.when(spell.id()).thenReturn(456);

        assertEquals(
            "GA;301;123;456",
            ActionEffect.criticalHitSpell(fighter, spell).toString()
        );
    }

    @Test
    void fighterDie() {
        Fighter caster = Mockito.mock(Fighter.class);
        Mockito.when(caster.id()).thenReturn(123);

        Fighter fighter = Mockito.mock(Fighter.class);
        Mockito.when(fighter.id()).thenReturn(321);

        assertEquals(
            "GA;103;123;321",
            ActionEffect.fighterDie(caster, fighter).toString()
        );
    }

    @Test
    void teleport() {
        Fighter caster = Mockito.mock(Fighter.class);
        Mockito.when(caster.id()).thenReturn(123);

        Fighter fighter = Mockito.mock(Fighter.class);
        Mockito.when(fighter.id()).thenReturn(321);

        FightCell target = Mockito.mock(FightCell.class);
        Mockito.when(target.id()).thenReturn(456);

        assertEquals(
            "GA;4;123;321,456",
            ActionEffect.teleport(caster, fighter, target).toString()
        );
    }

    @Test
    void criticalHitCloseCombat() {
        Fighter caster = Mockito.mock(Fighter.class);
        Mockito.when(caster.id()).thenReturn(123);

        assertEquals(
            "GA;304;123;",
            ActionEffect.criticalHitCloseCombat(caster).toString()
        );
    }

    @Test
    void skipNextTurn() {
        Fighter caster = Mockito.mock(Fighter.class);
        Mockito.when(caster.id()).thenReturn(123);

        Fighter target = Mockito.mock(Fighter.class);
        Mockito.when(target.id()).thenReturn(456);

        assertEquals(
            "GA;140;123;456",
            ActionEffect.skipNextTurn(caster, target).toString()
        );
    }

    @Test
    void returnSpell() {
        Fighter fighter = Mockito.mock(Fighter.class);
        Mockito.when(fighter.id()).thenReturn(456);

        assertEquals("GA;106;456;456,1", ActionEffect.returnSpell(fighter, true).toString());
        assertEquals("GA;106;456;456,0", ActionEffect.returnSpell(fighter, false).toString());
    }

    @Test
    void buff() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Fighter caster = Mockito.mock(Fighter.class);
        Fighter target = Mockito.mock(Fighter.class);

        Mockito.when(effect.effect()).thenReturn(111);
        Mockito.when(effect.min()).thenReturn(3);
        Mockito.when(effect.duration()).thenReturn(5);

        Mockito.when(caster.id()).thenReturn(123);
        Mockito.when(target.id()).thenReturn(456);

        Buff buff = new Buff(effect, Mockito.mock(Spell.class), caster, target, null);

        assertEquals("GA;111;123;456,3,5", ActionEffect.buff(buff, 3).toString());
        assertEquals("GA;111;123;456,-3,5", ActionEffect.buff(buff, -3).toString());
    }

    @Test
    void addActionPoints() {
        Fighter fighter = Mockito.mock(Fighter.class);
        Mockito.when(fighter.id()).thenReturn(456);

        assertEquals("GA;120;456;456,2", ActionEffect.addActionPoints(fighter, 2).toString());
    }

    @Test
    void removeActionPoints() {
        Fighter fighter = Mockito.mock(Fighter.class);
        Mockito.when(fighter.id()).thenReturn(456);

        assertEquals("GA;168;456;456,-2", ActionEffect.removeActionPoints(fighter, 2).toString());
    }

    @Test
    void addMovementPoints() {
        Fighter fighter = Mockito.mock(Fighter.class);
        Mockito.when(fighter.id()).thenReturn(456);

        assertEquals("GA;128;456;456,2", ActionEffect.addMovementPoints(fighter, 2).toString());
    }

    @Test
    void removeMovementPoints() {
        Fighter fighter = Mockito.mock(Fighter.class);
        Mockito.when(fighter.id()).thenReturn(456);

        assertEquals("GA;169;456;456,-2", ActionEffect.removeMovementPoints(fighter, 2).toString());
    }

    @Test
    void reduceDamage() {
        Fighter fighter = Mockito.mock(Fighter.class);
        Mockito.when(fighter.id()).thenReturn(456);

        assertEquals("GA;105;456;456,5", ActionEffect.reducedDamage(fighter, 5).toString());
    }

    @Test
    void addState() {
        Fighter fighter = Mockito.mock(Fighter.class);
        Mockito.when(fighter.id()).thenReturn(456);

        assertEquals("GA;950;456;456,5,1", ActionEffect.addState(fighter, 5).toString());
    }

    @Test
    void removeState() {
        Fighter fighter = Mockito.mock(Fighter.class);
        Mockito.when(fighter.id()).thenReturn(456);

        assertEquals("GA;950;456;456,5,0", ActionEffect.removeState(fighter, 5).toString());
    }

    @Test
    void dispelBuffs() {
        Fighter fighter = Mockito.mock(Fighter.class);
        Fighter fighter2 = Mockito.mock(Fighter.class);

        Mockito.when(fighter.id()).thenReturn(456);
        Mockito.when(fighter2.id()).thenReturn(460);
        
        assertEquals("GA;132;456;460", ActionEffect.dispelBuffs(fighter, fighter2).toString());
    }

    @Test
    void slide() {
        Fighter fighter = Mockito.mock(Fighter.class);
        Fighter fighter2 = Mockito.mock(Fighter.class);
        FightCell cell = Mockito.mock(FightCell.class);

        Mockito.when(fighter.id()).thenReturn(456);
        Mockito.when(fighter2.id()).thenReturn(460);
        Mockito.when(cell.id()).thenReturn(123);

        assertEquals("GA;5;456;460,123", ActionEffect.slide(fighter, fighter2, cell).toString());
    }

    @Test
    void reflectedDamage() {
        Fighter fighter = Mockito.mock(Fighter.class);

        Mockito.when(fighter.id()).thenReturn(456);

        assertEquals("GA;107;456;456,15", ActionEffect.reflectedDamage(fighter, 15).toString());
    }

    @Test
    void dodgeActionPointLost() {
        Fighter caster = Mockito.mock(Fighter.class);
        Fighter target = Mockito.mock(Fighter.class);

        Mockito.when(caster.id()).thenReturn(456);
        Mockito.when(target.id()).thenReturn(123);

        assertEquals("GA;308;456;123,2", ActionEffect.dodgeActionPointLost(caster, target, 2).toString());
    }

    @Test
    void dodgeMovementPointLost() {
        Fighter caster = Mockito.mock(Fighter.class);
        Fighter target = Mockito.mock(Fighter.class);

        Mockito.when(caster.id()).thenReturn(456);
        Mockito.when(target.id()).thenReturn(123);

        assertEquals("GA;309;456;123,2", ActionEffect.dodgeMovementPointLost(caster, target, 2).toString());
    }

    @Test
    void changeAppearance() {
        Fighter caster = Mockito.mock(Fighter.class);
        Fighter target = Mockito.mock(Fighter.class);

        Sprite sprite = Mockito.mock(Sprite.class);

        Mockito.when(caster.id()).thenReturn(456);
        Mockito.when(target.id()).thenReturn(123);
        Mockito.when(target.sprite()).thenReturn(sprite);
        Mockito.when(sprite.gfxId()).thenReturn(10);

        assertEquals("GA;149;456;123,10,147,5", ActionEffect.changeAppearance(caster, target, 147, 5).toString());
    }

    @Test
    void resetAppearance() {
        Fighter caster = Mockito.mock(Fighter.class);
        Fighter target = Mockito.mock(Fighter.class);

        Sprite sprite = Mockito.mock(Sprite.class);

        Mockito.when(caster.id()).thenReturn(456);
        Mockito.when(target.id()).thenReturn(123);
        Mockito.when(target.sprite()).thenReturn(sprite);
        Mockito.when(sprite.gfxId()).thenReturn(10);

        assertEquals("GA;149;456;123,10,10,0", ActionEffect.resetAppearance(caster, target).toString());
    }

    @Test
    void launchVisualEffect() {
        Fighter caster = Mockito.mock(Fighter.class);
        Spell spell = Mockito.mock(Spell.class);
        FightCell cell = Mockito.mock(FightCell.class);

        Mockito.when(caster.id()).thenReturn(456);
        Mockito.when(spell.spriteId()).thenReturn(12);
        Mockito.when(spell.spriteArgs()).thenReturn("11,0,1");
        Mockito.when(spell.level()).thenReturn(3);
        Mockito.when(cell.id()).thenReturn(325);

        assertEquals("GA;208;456;325,12,11,0,1,3", ActionEffect.launchVisualEffect(caster, cell, spell).toString());
    }

    @Test
    void boostSight() {
        Fighter caster = Mockito.mock(Fighter.class);
        Mockito.when(caster.id()).thenReturn(456);

        Fighter target = Mockito.mock(Fighter.class);
        Mockito.when(target.id()).thenReturn(123);

        assertEquals("GA;117;456;123,5,3", ActionEffect.boostSight(caster, target, 5, 3).toString());
    }

    @Test
    void decreaseSight() {
        Fighter caster = Mockito.mock(Fighter.class);
        Mockito.when(caster.id()).thenReturn(456);

        Fighter target = Mockito.mock(Fighter.class);
        Mockito.when(target.id()).thenReturn(123);

        assertEquals("GA;116;456;123,5,3", ActionEffect.decreaseSight(caster, target, 5, 3).toString());
    }

    @Test
    void fighterHidden() {
        Fighter caster = Mockito.mock(Fighter.class);
        Mockito.when(caster.id()).thenReturn(456);

        Fighter target = Mockito.mock(Fighter.class);
        Mockito.when(target.id()).thenReturn(123);

        assertEquals("GA;150;456;123,1", ActionEffect.fighterHidden(caster, target).toString());
    }

    @Test
    void fighterVisible() {
        Fighter caster = Mockito.mock(Fighter.class);
        Mockito.when(caster.id()).thenReturn(456);

        Fighter target = Mockito.mock(Fighter.class);
        Mockito.when(target.id()).thenReturn(123);

        assertEquals("GA;150;456;123,0", ActionEffect.fighterVisible(caster, target).toString());
    }
}
