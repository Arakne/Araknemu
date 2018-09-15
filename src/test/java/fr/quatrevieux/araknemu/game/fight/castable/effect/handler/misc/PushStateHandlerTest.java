package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.order.AlternateTeamFighterOrder;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import fr.quatrevieux.araknemu.network.game.out.fight.AddBuff;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PushStateHandlerTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private PlayerFighter target;
    private PushStateHandler handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();
        fight.turnList().start();

        caster = player.fighter();
        target = other.fighter();

        target.move(fight.map().get(123));

        handler = new PushStateHandler(fight);

        requestStack.clear();
    }

    @Test
    void handle() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.special()).thenReturn(4);
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.handle(scope, scope.effects().get(0));

        requestStack.assertAll(ActionEffect.addState(target, 4));
        assertTrue(target.states().has(4));
    }

    @Test
    void buff() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.duration()).thenReturn(2);
        Mockito.when(effect.special()).thenReturn(4);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        requestStack.assertAll(ActionEffect.addState(target, 4));
        assertTrue(target.states().has(4));

        // Skip 2 turns
        fight.turnList().current().ifPresent(FightTurn::stop);
        fight.turnList().current().ifPresent(FightTurn::stop);
        fight.turnList().current().ifPresent(FightTurn::stop);
        fight.turnList().current().ifPresent(FightTurn::stop);

        assertFalse(target.states().has(4));
        requestStack.assertOne(ActionEffect.removeState(target, 4));
    }

    @Test
    void buffSelfCastWillAddOneTurn() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.duration()).thenReturn(2);
        Mockito.when(effect.special()).thenReturn(4);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope scope = makeCastScope(caster, spell, effect, caster.cell());
        handler.buff(scope, scope.effects().get(0));

        requestStack.assertAll(ActionEffect.addState(caster, 4));
        assertTrue(caster.states().has(4));

        // Skip 2 turns
        fight.turnList().current().ifPresent(FightTurn::stop);
        fight.turnList().current().ifPresent(FightTurn::stop);
        fight.turnList().current().ifPresent(FightTurn::stop);
        fight.turnList().current().ifPresent(FightTurn::stop);

        assertTrue(caster.states().has(4));

        fight.turnList().current().ifPresent(FightTurn::stop);
        assertFalse(caster.states().has(4));
    }
}
