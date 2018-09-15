package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.event.TurnTerminated;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class RefreshBuffsTest extends FightBaseCase {
    private Fight fight;
    private RefreshBuffs listener;
    private PlayerFighter fighter;
    private FightTurn turn;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = player.fighter();
        listener = new RefreshBuffs();

        turn = new FightTurn(player.fighter(), fight, Duration.ofMillis(50));
        requestStack.clear();
    }

    @Test
    void onTurnTerminated() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.duration()).thenReturn(5);
        Buff buff = new Buff(effect, Mockito.mock(Spell.class), other.fighter(), player.fighter(), new BuffHook() {});

        fighter.buffs().add(buff);

        listener.on(new TurnTerminated(turn, false));

        assertEquals(4, buff.remainingTurns());
    }
}
