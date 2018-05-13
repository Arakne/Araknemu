package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class TeleportHandlerTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private TeleportHandler handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();

        caster = player.fighter();

        handler = new TeleportHandler(fight);

        requestStack.clear();
    }

    @Test
    void applySuccess() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        FightCell lastCell = caster.cell();

        FightCell target = fight.map().get(123);

        handler.handle(caster, Mockito.mock(Spell.class), effect, target);

        requestStack.assertLast(ActionEffect.teleport(caster, caster, target));

        assertSame(caster, target.fighter().get());
        assertSame(target, caster.cell());
        assertFalse(lastCell.fighter().isPresent());
    }

    @Test
    void applyNotFreeCell() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        FightCell lastCell = caster.cell();

        FightCell target = other.fighter().cell();

        handler.handle(caster, Mockito.mock(Spell.class), effect, target);

        requestStack.assertEmpty();

        assertSame(lastCell, caster.cell());
    }
}