package fr.quatrevieux.araknemu.network.game.out.fight;

import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class AddBuffTest {
    @Test
    void generate() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Fighter fighter = Mockito.mock(Fighter.class);
        Spell spell = Mockito.mock(Spell.class);

        Mockito.when(effect.effect()).thenReturn(99);
        Mockito.when(effect.duration()).thenReturn(3);
        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.max()).thenReturn(7);
        Mockito.when(effect.text()).thenReturn("");
        Mockito.when(fighter.id()).thenReturn(123);
        Mockito.when(spell.id()).thenReturn(456);

        Buff buff = new Buff(effect, spell, fighter, fighter, Mockito.mock(BuffHook.class));

        assertEquals("GIE99;123;5;7;0;;3;456", new AddBuff(buff).toString());
    }

    @Test
    void generateWithoutMax() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Fighter fighter = Mockito.mock(Fighter.class);
        Spell spell = Mockito.mock(Spell.class);

        Mockito.when(effect.effect()).thenReturn(99);
        Mockito.when(effect.duration()).thenReturn(3);
        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.text()).thenReturn("");
        Mockito.when(fighter.id()).thenReturn(123);
        Mockito.when(spell.id()).thenReturn(456);

        Buff buff = new Buff(effect, spell, fighter, fighter, Mockito.mock(BuffHook.class));

        assertEquals("GIE99;123;5;;0;;3;456", new AddBuff(buff).toString());
    }

    @Test
    void generateWithoutSpell() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Fighter fighter = Mockito.mock(Fighter.class);
        Castable castable = Mockito.mock(Castable.class);

        Mockito.when(effect.effect()).thenReturn(99);
        Mockito.when(effect.duration()).thenReturn(3);
        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.text()).thenReturn("");
        Mockito.when(fighter.id()).thenReturn(123);

        Buff buff = new Buff(effect, castable, fighter, fighter, Mockito.mock(BuffHook.class));

        assertEquals("GIE99;123;5;;0;;3;", new AddBuff(buff).toString());
    }
}