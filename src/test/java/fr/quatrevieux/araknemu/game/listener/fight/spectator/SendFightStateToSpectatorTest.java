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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.listener.fight.spectator;

import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.spectator.Spectator;
import fr.quatrevieux.araknemu.game.fight.spectator.event.StartWatchFight;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.AddBuff;
import fr.quatrevieux.araknemu.network.game.out.fight.BeginFight;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.FighterTurnOrder;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.StartTurn;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.TurnMiddle;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.stream.Collectors;

class SendFightStateToSpectatorTest extends FightBaseCase {
    private Fight fight;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createSimpleFight(container.get(ExplorationMapService.class).load(10340));
    }

    @Test
    void onStartWatchFightTurnNotYetStarted() throws SQLException {
        SendFightStateToSpectator listener = new SendFightStateToSpectator(new Spectator(gamePlayer(), fight));
        requestStack.clear();

        fight.nextState();

        listener.on(new StartWatchFight());

        requestStack.assertAll(
            new fr.quatrevieux.araknemu.network.game.out.fight.JoinFightAsSpectator(fight),
            new AddSprites(fight.fighters().stream().map(Fighter::sprite).collect(Collectors.toList())),
            new BeginFight(),
            new FighterTurnOrder(fight.turnList()),
            new TurnMiddle(fight.fighters())
        );
    }

    @Test
    void onStartWatchFightTurnStarted() throws SQLException {
        SendFightStateToSpectator listener = new SendFightStateToSpectator(new Spectator(gamePlayer(), fight));
        requestStack.clear();

        fight.nextState();
        fight.turnList().start();

        listener.on(new StartWatchFight());

        requestStack.assertAll(
            new fr.quatrevieux.araknemu.network.game.out.fight.JoinFightAsSpectator(fight),
            new AddSprites(fight.fighters().stream().map(Fighter::sprite).collect(Collectors.toList())),
            new BeginFight(),
            new FighterTurnOrder(fight.turnList()),
            new TurnMiddle(fight.fighters()),
            new StartTurn(fight.turnList().current().get())
        );
    }

    @Test
    void onStartWatchFightWithDeadFighter() throws SQLException {
        PlayerFighter deadFighter = makePlayerFighter(makeSimpleGamePlayer(15));

        fight.team(0).join(deadFighter);
        deadFighter.joinFight(fight, fight.map().get(150));

        SendFightStateToSpectator listener = new SendFightStateToSpectator(new Spectator(gamePlayer(), fight));
        requestStack.clear();

        fight.nextState();
        fight.turnList().start();

        deadFighter.life().kill(deadFighter);

        listener.on(new StartWatchFight());

        requestStack.assertAll(
            new fr.quatrevieux.araknemu.network.game.out.fight.JoinFightAsSpectator(fight),
            new AddSprites(fight.fighters().stream().map(Fighter::sprite).collect(Collectors.toList())),
            new BeginFight(),
            new FighterTurnOrder(fight.turnList()),
            new TurnMiddle(fight.fighters()),
            new StartTurn(fight.turnList().current().get())
        );
    }

    @Test
    void onStartWatchFightWithBuffs() throws SQLException {
        SendFightStateToSpectator listener = new SendFightStateToSpectator(new Spectator(gamePlayer(), fight));

        fight.nextState();
        fight.turnList().start();

        SpellEffect effect1 = Mockito.mock(SpellEffect.class);
        Mockito.when(effect1.effect()).thenReturn(15);
        Mockito.when(effect1.min()).thenReturn(3);
        Mockito.when(effect1.duration()).thenReturn(5);

        SpellEffect effect2 = Mockito.mock(SpellEffect.class);
        Mockito.when(effect2.effect()).thenReturn(8);
        Mockito.when(effect2.min()).thenReturn(80);
        Mockito.when(effect2.duration()).thenReturn(10);

        Buff buff1 = new Buff(effect1, Mockito.mock(Spell.class), fight.fighters().get(0), fight.fighters().get(0), Mockito.mock(BuffHook.class));
        Buff buff2 = new Buff(effect2, Mockito.mock(Spell.class), fight.fighters().get(0), fight.fighters().get(0), Mockito.mock(BuffHook.class));

        fight.fighters().get(0).buffs().add(buff1);
        fight.fighters().get(0).buffs().add(buff2);
        requestStack.clear();

        listener.on(new StartWatchFight());

        requestStack.assertAll(
            new fr.quatrevieux.araknemu.network.game.out.fight.JoinFightAsSpectator(fight),
            new AddSprites(fight.fighters().stream().map(Fighter::sprite).collect(Collectors.toList())),
            new BeginFight(),
            new FighterTurnOrder(fight.turnList()),
            new TurnMiddle(fight.fighters()),
            new StartTurn(fight.turnList().current().get()),
            new AddBuff(buff1),
            new AddBuff(buff2)
        );
    }

    @Test
    void onStartWatchFightWithInvisibility() throws SQLException {
        SendFightStateToSpectator listener = new SendFightStateToSpectator(new Spectator(gamePlayer(), fight));

        fight.nextState();
        fight.turnList().start();

        fight.fighters().get(0).setHidden(fight.fighters().get(0), true);
        requestStack.clear();

        listener.on(new StartWatchFight());

        requestStack.assertAll(
            new fr.quatrevieux.araknemu.network.game.out.fight.JoinFightAsSpectator(fight),
            new AddSprites(fight.fighters().stream().map(Fighter::sprite).collect(Collectors.toList())),
            new BeginFight(),
            new FighterTurnOrder(fight.turnList()),
            new TurnMiddle(fight.fighters()),
            new StartTurn(fight.turnList().current().get()),
            ActionEffect.fighterHidden(fight.fighters().get(0), fight.fighters().get(0))
        );
    }
}
