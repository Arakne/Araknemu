package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class DamageHandlerTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private PlayerFighter target;
    private DamageHandler handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();

        caster = player.fighter();
        target = other.fighter();

        handler = new DamageHandler(Element.AIR);

        requestStack.clear();
    }

    @Test
    void applyRandomEffect() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.max()).thenReturn(15);

        handler.handle(caster, Mockito.mock(Spell.class), effect, target.cell());

        int damage = target.life().max() - target.life().current();

        assertBetween(10, 15, damage);

        requestStack.assertLast(ActionEffect.alterLifePoints(caster, target, -damage));
    }

    @Test
    void applyFixedEffect() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(10);

        handler.handle(caster, Mockito.mock(Spell.class), effect, target.cell());

        int damage = target.life().max() - target.life().current();

        assertEquals(10, damage);

        requestStack.assertLast(ActionEffect.alterLifePoints(caster, target, -10));
    }

    @Test
    void applyBoostEffect() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(10);

        player.characteristics().base().set(Characteristic.AGILITY, 100);

        handler.handle(caster, Mockito.mock(Spell.class), effect, target.cell());

        int damage = target.life().max() - target.life().current();

        assertEquals(20, damage);
    }

    @Test
    void applyToEmptyCell() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Mockito.when(effect.min()).thenReturn(10);

        handler.handle(caster, Mockito.mock(Spell.class), effect, fight.map().get(5));

        requestStack.assertEmpty();
    }
}
