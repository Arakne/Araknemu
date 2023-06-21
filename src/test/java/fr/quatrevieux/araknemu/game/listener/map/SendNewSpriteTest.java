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

package fr.quatrevieux.araknemu.game.listener.map;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.map.event.NewSpriteOnMap;
import fr.quatrevieux.araknemu.game.exploration.sprite.PlayerSprite;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collections;

class SendNewSpriteTest extends GameBaseCase {
    private SendNewSprite listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendNewSprite(
            explorationPlayer().map()
        );
    }

    @Test
    void onSelfSprite() throws SQLException, ContainerException {
        requestStack.clear();

        listener.on(
            new NewSpriteOnMap(
                new PlayerSprite(explorationPlayer())
            )
        );

        requestStack.assertEmpty();
    }

    @Test
    void onOtherSprite() throws Exception {
        ExplorationPlayer player = new ExplorationPlayer(makeOtherPlayer());
        player.changeMap(explorationPlayer().map(), 123);
        Sprite sprite = new PlayerSprite(player);

        listener.on(
            new NewSpriteOnMap(sprite)
        );

        requestStack.assertLast(
            new AddSprites(
                Collections.singleton(sprite)
            )
        );
    }
}
