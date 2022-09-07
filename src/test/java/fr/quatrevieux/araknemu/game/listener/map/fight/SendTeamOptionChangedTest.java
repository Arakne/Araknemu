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

package fr.quatrevieux.araknemu.game.listener.map.fight;

import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.team.ConfigurableTeamOptions;
import fr.quatrevieux.araknemu.game.fight.team.event.AllowJoinTeamChanged;
import fr.quatrevieux.araknemu.game.fight.team.event.AllowSpectatorChanged;
import fr.quatrevieux.araknemu.game.fight.team.event.NeedHelpChanged;
import fr.quatrevieux.araknemu.network.game.out.fight.FightOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SendTeamOptionChangedTest extends FightBaseCase {
    private ListenerAggregate dispatcher;
    private Fight fight;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);
        explorationPlayer().changeMap(map, 150);

        dispatcher = new DefaultListenerAggregate();
        dispatcher.register(new SendTeamOptionChanged(map));
        fight = createSimpleFight(map);
    }

    @Test
    void onSpectatorOptionChanged() {
        dispatcher.dispatch(new AllowSpectatorChanged(fight.team(0).options(), true));
        requestStack.assertLast(new FightOption(fight.team(0).id(), FightOption.Type.BLOCK_SPECTATOR, false));

        ConfigurableTeamOptions.class.cast(fight.team(0).options()).toggleAllowSpectators();
        dispatcher.dispatch(new AllowSpectatorChanged(fight.team(0).options(), false));
        requestStack.assertLast(new FightOption(fight.team(0).id(), FightOption.Type.BLOCK_SPECTATOR, true));
    }

    @Test
    void onBlockJoinChanged() {
        dispatcher.dispatch(new AllowJoinTeamChanged(fight.team(0).options(), true));
        requestStack.assertLast(new FightOption(fight.team(0).id(), FightOption.Type.BLOCK_JOINER, false));

        ConfigurableTeamOptions.class.cast(fight.team(0).options()).toggleAllowJoinTeam();
        dispatcher.dispatch(new AllowJoinTeamChanged(fight.team(0).options(), false));
        requestStack.assertLast(new FightOption(fight.team(0).id(), FightOption.Type.BLOCK_JOINER, true));
    }

    @Test
    void onNeedHelpChanged() {
        ConfigurableTeamOptions.class.cast(fight.team(0).options()).toggleNeedHelp();
        dispatcher.dispatch(new NeedHelpChanged(fight.team(0).options(), true));
        requestStack.assertLast(new FightOption(fight.team(0).id(), FightOption.Type.NEED_HELP, true));

        ConfigurableTeamOptions.class.cast(fight.team(0).options()).toggleNeedHelp();
        dispatcher.dispatch(new NeedHelpChanged(fight.team(0).options(), false));
        requestStack.assertLast(new FightOption(fight.team(0).id(), FightOption.Type.NEED_HELP, false));
    }
}
