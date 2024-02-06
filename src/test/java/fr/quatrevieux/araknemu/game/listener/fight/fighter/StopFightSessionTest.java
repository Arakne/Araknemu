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

package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ending.reward.FightReward;
import fr.quatrevieux.araknemu.game.fight.event.FightFinished;
import fr.quatrevieux.araknemu.game.fight.event.FightLeaved;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class StopFightSessionTest extends FightBaseCase {
    private Fight fight;
    private StopFightSession subscriber;
    private DefaultListenerAggregate listener;
    private PlayerFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = player.fighter();
        subscriber = new StopFightSession(fighter);
        listener = new DefaultListenerAggregate();
        listener.register(subscriber);
    }

    @Test
    void onFightLeaved() {
        player.properties().life().set(50);
        listener.dispatch(new FightLeaved());

        assertFalse(player.isFighting());

        Player entity = dataSet.refresh(new Player(player.id()));
        assertEquals(50, entity.life());
    }

    @Test
    void onFightFinished() {
        player.properties().life().set(50);
        listener.dispatch(new FightFinished(Mockito.mock(FightReward.class)));

        assertFalse(player.isFighting());

        Player entity = dataSet.refresh(new Player(player.id()));
        assertEquals(50, entity.life());
    }
}