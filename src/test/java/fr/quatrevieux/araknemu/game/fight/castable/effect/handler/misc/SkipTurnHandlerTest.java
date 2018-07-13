package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.network.game.out.fight.AddBuff;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SkipTurnHandlerTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private PlayerFighter target;
    private SkipTurnHandler handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();

        caster = player.fighter();
        target = other.fighter();

        target.move(fight.map().get(123));

        handler = new SkipTurnHandler(fight);

        requestStack.clear();
    }

    @Test
    void handle() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());

        handler.handle(caster, Mockito.mock(Spell.class), effect, target.cell());

        Optional<Buff> buff = target.buffs().stream().findFirst();

        assertTrue(buff.isPresent());
        assertSame(effect, buff.get().effect());

        requestStack.assertAll(
            ActionEffect.skipNextTurn(caster, target),
            new AddBuff(buff.get())
        );
    }

    @Test
    void buff() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.duration()).thenReturn(3);

        handler.buff(caster, Mockito.mock(Spell.class), effect, target.cell());

        Optional<Buff> buff = target.buffs().stream().findFirst();

        assertTrue(buff.isPresent());
        assertSame(effect, buff.get().effect());

        requestStack.assertAll(
            ActionEffect.skipNextTurn(caster, target),
            new AddBuff(buff.get())
        );
    }
}
