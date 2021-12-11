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

package fr.quatrevieux.araknemu.game.exploration.interaction.action.fight;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionQueue;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.BlockingAction;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.spectator.SpectatorFactory;
import fr.quatrevieux.araknemu.game.fight.team.ConfigurableTeamOptions;
import fr.quatrevieux.araknemu.network.game.out.fight.BeginFight;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.FighterTurnOrder;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.StartTurn;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.TurnMiddle;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import fr.quatrevieux.araknemu.network.game.out.info.Information;
import fr.quatrevieux.araknemu.network.game.out.info.StopLifeTimer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JoinFightAsSpectatorTest extends FightBaseCase {
    private Fight fight;
    private JoinFightAsSpectator action;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);
        explorationPlayer().changeMap(map, 123);

        fight = createSimpleFight(map);
        action = new JoinFightAsSpectator(container.get(SpectatorFactory.class), explorationPlayer(), fight);

        requestStack.clear();
    }

    @Test
    void busy() throws SQLException, ContainerException {
        explorationPlayer().interactions().push(new MyBlockingAction());
        assertTrue(explorationPlayer().interactions().busy());

        action.start(new ActionQueue());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.JOIN_FIGHT, player.id(), "o")
        );
    }

    @Test
    void notActive() throws SQLException {
        action.start(new ActionQueue());
        assertFalse(explorationPlayer().player().isSpectator());

        requestStack.assertLast(Error.cantJoinFightAsSpectator());
    }

    @Test
    void badMap() throws SQLException, ContainerException {
        explorationPlayer().changeMap(container.get(ExplorationMapService.class).load(10540), 123);
        assertFalse(explorationPlayer().player().isSpectator());

        action.start(new ActionQueue());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.JOIN_FIGHT, player.id(), "p")
        );
    }

    @Test
    void success() throws InterruptedException, SQLException {
        fight.nextState();
        fight.turnList().start();
        requestStack.clear();

        action.start(new ActionQueue());
        Thread.sleep(100);

        assertTrue(gamePlayer().isSpectator());
        assertFalse(gamePlayer().isExploring());
        assertSame(fight, gamePlayer().spectator().fight());

        requestStack.assertAll(
            new StopLifeTimer(),
            Information.spectatorHasJoinFight("Bob"),
            new fr.quatrevieux.araknemu.network.game.out.fight.JoinFightAsSpectator(fight),
            new AddSprites(fight.fighters().stream().map(Fighter::sprite).collect(Collectors.toList())),
            new BeginFight(),
            new FighterTurnOrder(fight.turnList()),
            new TurnMiddle(fight.fighters()),
            new StartTurn(fight.turnList().current().get())
        );
    }

    @Test
    void spectatorsNotAllowed() throws InterruptedException, SQLException {
        fight.nextState();
        fight.turnList().start();
        ConfigurableTeamOptions.class.cast(fight.team(0).options()).toggleAllowSpectators();
        requestStack.clear();

        action.start(new ActionQueue());
        Thread.sleep(100);

        assertFalse(explorationPlayer().player().isSpectator());
        requestStack.assertLast(Error.cantJoinFightAsSpectator());
    }

    public static class MyBlockingAction implements BlockingAction {
        private int id;

        @Override
        public void cancel(String argument) { }

        @Override
        public void end() { }

        @Override
        public int id() {
            return id;
        }

        @Override
        public void setId(int id) {
            this.id = id;
        }

        @Override
        public void start(ActionQueue queue) {
            queue.setPending(this);
        }

        @Override
        public ExplorationPlayer performer() {
            return null;
        }

        @Override
        public ActionType type() {
            return null;
        }

        @Override
        public Object[] arguments() {
            return new Object[0];
        }
    }
}
