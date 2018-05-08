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

class StealLifeHandlerTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private PlayerFighter target;
    private StealLifeHandler handler;

    int baseLife;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();

        caster = player.fighter();
        target = other.fighter();

        caster.life().alter(caster, -50);
        baseLife = caster.life().current();

        handler = new StealLifeHandler(Element.AIR);

        requestStack.clear();
    }

    @Test
    void applyFixedEffect() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(10);

        handler.handle(caster, Mockito.mock(Spell.class), effect, target.cell());

        assertEquals(10, target.life().max() - target.life().current());
        assertEquals(5, caster.life().current() - baseLife);

        requestStack.assertAll(
            ActionEffect.alterLifePoints(caster, target, -10),
            ActionEffect.alterLifePoints(caster, caster, 5)
        );
    }

    @Test
    void applyBoostEffect() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(10);

        player.characteristics().base().set(Characteristic.AGILITY, 100);

        handler.handle(caster, Mockito.mock(Spell.class), effect, target.cell());

        assertEquals(20, target.life().max() - target.life().current());
        assertEquals(10, caster.life().current() - baseLife);
    }

    @Test
    void applyOnFullLife() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(10);

        caster.life().alter(caster, 50);
        requestStack.clear();

        handler.handle(caster, Mockito.mock(Spell.class), effect, target.cell());

        requestStack.assertAll(
            ActionEffect.alterLifePoints(caster, target, -10),
            ActionEffect.alterLifePoints(caster, caster, 0)
        );
    }

    @Test
    void applyWithoutEffect() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(0);

        handler.handle(caster, Mockito.mock(Spell.class), effect, target.cell());

        requestStack.assertAll(ActionEffect.alterLifePoints(caster, target, 0));
    }

    @Test
    void applyHealTooHigh() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        caster.life().alter(caster, 45);
        requestStack.clear();

        Mockito.when(effect.min()).thenReturn(20);

        handler.handle(caster, Mockito.mock(Spell.class), effect, target.cell());

        requestStack.assertAll(
            ActionEffect.alterLifePoints(caster, target, -20),
            ActionEffect.alterLifePoints(caster, caster, 5)
        );
    }

    @Test
    void applyDamage1() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(1);

        handler.handle(caster, Mockito.mock(Spell.class), effect, target.cell());

        requestStack.assertAll(
            ActionEffect.alterLifePoints(caster, target, -1),
            ActionEffect.alterLifePoints(caster, caster, 1)
        );
    }

    @Test
    void applyToEmptyCell() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Mockito.when(effect.min()).thenReturn(10);

        handler.handle(caster, Mockito.mock(Spell.class), effect, fight.map().get(5));

        requestStack.assertEmpty();

        assertEquals(baseLife, caster.life().current());
    }
}