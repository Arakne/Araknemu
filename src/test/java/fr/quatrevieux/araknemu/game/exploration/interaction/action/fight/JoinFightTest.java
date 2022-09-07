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
import fr.quatrevieux.araknemu.game.fight.exception.JoinFightException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.team.ConfigurableTeamOptions;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import fr.quatrevieux.araknemu.network.game.out.game.FightStartPositions;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import fr.quatrevieux.araknemu.network.game.out.info.StopLifeTimer;
import io.github.artsok.RepeatedIfExceptionsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class JoinFightTest extends FightBaseCase {
    private Fight fight;
    private JoinFight action;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);
        explorationPlayer().changeMap(map, 123);

        fight = createSimpleFight(map);
        action = new JoinFight(explorationPlayer(), fight, fight.team(0), container.get(FighterFactory.class));

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
    void tooLate() {
        fight.nextState();

        action.start(new ActionQueue());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.JOIN_FIGHT, player.id(), "l")
        );
    }

    @Test
    void badMap() throws SQLException, ContainerException {
        explorationPlayer().changeMap(container.get(ExplorationMapService.class).load(10540), 123);

        action.start(new ActionQueue());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.JOIN_FIGHT, player.id(), "p")
        );
    }

    @Test
    void notOnMap() throws SQLException, ContainerException {
        explorationPlayer().leave();

        action.start(new ActionQueue());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.JOIN_FIGHT, player.id(), "p")
        );
    }

    @RepeatedIfExceptionsTest
    void fullTeam() throws SQLException, ContainerException, JoinFightException, InterruptedException {
        for (int i = 10; fight.team(0).fighters().size() < fight.team(0).startPlaces().size(); ++i) {
            fight.team(0).join(makePlayerFighter(makeSimpleGamePlayer(i)));
        }

        action.start(new ActionQueue());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.JOIN_FIGHT, player.id(), "t")
        );
    }

    @RepeatedIfExceptionsTest
    void success() throws InterruptedException {
        action.start(new ActionQueue());

        assertTrue(player.isFighting());
        assertFalse(player.isExploring());
        assertSame(fight, player.fighter().fight());
        assertContains(player.fighter().cell(), fight.team(0).startPlaces());
        assertContains(player.fighter(), fight.team(0).fighters());

        requestStack.assertAll(
            new StopLifeTimer(),
            new fr.quatrevieux.araknemu.network.game.out.fight.JoinFight(fight),
            new AddSprites(fight.fighters().stream().map(Fighter::sprite).collect(Collectors.toList())),
            new FightStartPositions(new FightCell[][] { fight.team(0).startPlaces().toArray(new FightCell[0]), fight.team(1).startPlaces().toArray(new FightCell[0]) }, 0),
            new AddSprites(Collections.singleton(player.fighter().sprite()))
        );
    }

    @RepeatedIfExceptionsTest
    void locked() throws InterruptedException {
        ConfigurableTeamOptions.class.cast(fight.team(0).options()).toggleAllowJoinTeam();

        action.start(new ActionQueue());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.JOIN_FIGHT, player.id(), "f")
        );
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
