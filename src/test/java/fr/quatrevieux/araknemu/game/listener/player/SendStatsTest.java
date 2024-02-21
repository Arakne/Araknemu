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

package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.player.characteristic.event.CharacteristicsChanged;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class SendStatsTest extends FightBaseCase {
    private SendStats listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendStats(gamePlayer(true));
        requestStack.clear();
    }

    @Test
    void onCharacteristicsChanged() throws SQLException, ContainerException {
        listener.on(new CharacteristicsChanged());

        requestStack.assertLast(
            new Stats(gamePlayer().properties())
        );
    }

    @Test
    void onCharacteristicsChangedWithFighter() throws Exception {
        createFight();
        PlayerFighter fighter = player.fighter();

        fighter.init();
        fighter.life().heal(fighter, -100);

        listener.on(new CharacteristicsChanged());

        requestStack.assertLast(new Stats(fighter.properties()));
    }
}
