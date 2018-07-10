package fr.quatrevieux.araknemu.network.game.out.fight.action;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.Spell;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

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
}