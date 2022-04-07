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

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.creature.Operation;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.event.NewSpriteOnMap;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;

import java.util.Collections;

/**
 * Send new sprites added on the current map
 */
public final class SendNewSprite implements Listener<NewSpriteOnMap> {
    private final ExplorationMap map;

    public SendNewSprite(ExplorationMap map) {
        this.map = map;
    }

    @Override
    public void on(NewSpriteOnMap event) {
        // Save the string value for optimisation
        final String packet = new AddSprites(Collections.singleton(event.sprite())).toString();

        map.apply(new Operation<Void>() {
            @Override
            public Void onExplorationPlayer(ExplorationPlayer player) {
                if (player.id() != event.sprite().id()) {
                    player.send(packet);
                }

                return null;
            }
        });
    }

    @Override
    public Class<NewSpriteOnMap> event() {
        return NewSpriteOnMap.class;
    }
}
