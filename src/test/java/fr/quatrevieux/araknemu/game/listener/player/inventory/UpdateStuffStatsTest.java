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

package fr.quatrevieux.araknemu.game.listener.player.inventory;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.characteristic.SpecialEffects;
import fr.quatrevieux.araknemu.game.player.characteristic.event.CharacteristicsChanged;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.player.inventory.event.EquipmentChanged;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UpdateStuffStatsTest extends GameBaseCase {
    private UpdateStuffStats listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;
        listener = new UpdateStuffStats(gamePlayer(true));

        requestStack.clear();
    }

    @Test
    void onEquipmentChanged() throws SQLException, ContainerException {
        GamePlayer player = gamePlayer();

        AtomicReference<CharacteristicsChanged> ref = new AtomicReference<>();
        player.dispatcher().add(CharacteristicsChanged.class, ref::set);

        listener.on(
            new EquipmentChanged(
                new InventoryEntry(
                    null,
                    new PlayerItem(1, 1, 2425, null, 1, -1),
                    container.get(ItemService.class).create(2425, true)
                ),
                0, true
            )
        );

        assertNotNull(ref.get());

        requestStack.assertOne(new Stats(player.properties()));
    }

    @Test
    void onEquipmentChangedWithSpecialEffect() throws SQLException, ContainerException {
        GamePlayer player = gamePlayer();

        listener.on(
            new EquipmentChanged(
                new InventoryEntry(
                    null,
                    new PlayerItem(1, 1, 2425, null, 1, -1),
                    container.get(ItemService.class).create(2428, true)
                ),
                3, true
            )
        );

        assertEquals(500, player.properties().characteristics().specials().get(SpecialEffects.Type.PODS));
    }

    @Test
    void onEquipmentChangedWithUnequipSpecialEffect() throws SQLException, ContainerException {
        GamePlayer player = gamePlayer();

        player.properties().characteristics().specials().add(SpecialEffects.Type.PODS, 500);

        listener.on(
            new EquipmentChanged(
                new InventoryEntry(
                    null,
                    new PlayerItem(1, 1, 2425, null, 1, -1),
                    container.get(ItemService.class).create(2428, true)
                ),
                3, false
            )
        );

        assertEquals(0, player.properties().characteristics().specials().get(SpecialEffects.Type.PODS));
    }
}
