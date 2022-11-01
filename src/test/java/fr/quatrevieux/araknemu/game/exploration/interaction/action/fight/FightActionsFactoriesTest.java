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

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.Action;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ExplorationActionRegistry;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.spectator.SpectatorFactory;
import fr.quatrevieux.araknemu.game.fight.turn.order.AlternateTeamFighterOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FightActionsFactoriesTest extends FightBaseCase {
    private ExplorationActionRegistry factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        factory = new ExplorationActionRegistry(new FightActionsFactories(container.get(FightService.class), container.get(FighterFactory.class), container.get(SpectatorFactory.class)));

        dataSet.pushMaps().pushSubAreas().pushAreas();
    }

    @Test
    void joinFight() throws Exception {
        ExplorationPlayer player = explorationPlayer();

        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);
        player.changeMap(map, 123);

        Fight fight = createSimpleFight(map);

        Action action = factory.create(player, ActionType.JOIN_FIGHT, new String[] {fight.id() + "", fight.team(0).id() + ""});

        assertInstanceOf(JoinFight.class, action);
    }

    @Test
    void joinFightNotOnMap() throws Exception {
        ExplorationPlayer player = explorationPlayer();
        player.leave();

        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        Fight fight = createSimpleFight(map);

        assertThrows(IllegalArgumentException.class, () -> factory.create(player, ActionType.JOIN_FIGHT, new String[] {fight.id() + "", fight.team(0).id() + ""}));
    }

    @Test
    void joinFightAsSpectator() throws Exception {
        ExplorationPlayer player = explorationPlayer();

        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);
        player.changeMap(map, 123);

        Fight fight = createSimpleFight(map);
        fight.start(new AlternateTeamFighterOrder());

        Action action = factory.create(player, ActionType.JOIN_FIGHT, new String[] {fight.id() + ""});

        assertInstanceOf(JoinFightAsSpectator.class, action);
    }

    @Test
    void joinFightAsSpectatorNotOnMap() throws Exception {
        ExplorationPlayer player = explorationPlayer();
        player.leave();

        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        Fight fight = createSimpleFight(map);
        fight.start(new AlternateTeamFighterOrder());

        assertThrows(IllegalArgumentException.class, () -> factory.create(player, ActionType.JOIN_FIGHT, new String[] {fight.id() + ""}));
    }

    @Test
    void joinMissingFightId() throws Exception {
        ExplorationPlayer player = explorationPlayer();

        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);
        player.changeMap(map, 123);

        Fight fight = createSimpleFight(map);
        fight.start(new AlternateTeamFighterOrder());

        assertThrows(IllegalArgumentException.class, () -> factory.create(player, ActionType.JOIN_FIGHT, new String[] {}));
    }
}
