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

package fr.quatrevieux.araknemu.game.player;

import fr.arakne.utils.maps.constant.Direction;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.sprite.PlayerSprite;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.player.inventory.slot.BootsSlot;
import fr.quatrevieux.araknemu.game.player.inventory.slot.HelmetSlot;
import fr.quatrevieux.araknemu.game.player.inventory.slot.WeaponSlot;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerSpriteTest extends GameBaseCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;
    }

    @Test
    void data() throws SQLException, ContainerException {
        GamePlayer player = gamePlayer();

        PlayerSprite sprite = new PlayerSprite(new ExplorationPlayer(player));

        assertEquals(1, sprite.id());
        assertEquals(200, sprite.cell());
        assertEquals("Bob", sprite.name());
        assertEquals(Direction.SOUTH_EAST, sprite.orientation());
        assertEquals(Sprite.Type.PLAYER, sprite.type());
        assertEquals(10, sprite.gfxId());
    }

    @Test
    void generateString() throws SQLException, ContainerException {
        assertEquals(
            "200;1;0;1;Bob;1;10^100x100;0;;7b;1c8;315;,,,,;;;;;;8;",
            new PlayerSprite(new ExplorationPlayer(gamePlayer())).toString()
        );
    }

    @Test
    void withRestrictions() throws SQLException, ContainerException {
        ExplorationPlayer exploration = explorationPlayer();
        exploration.player().restrictions().set(
            fr.quatrevieux.araknemu.game.player.Restrictions.Restriction.DENY_CHALLENGE,
            fr.quatrevieux.araknemu.game.player.Restrictions.Restriction.DENY_ASSAULT
        );

        assertEquals(
            "279;1;0;1;Bob;1;10^100x100;0;;7b;1c8;315;,,,,;;;;;;b;",
            new PlayerSprite(exploration).toString()
        );
    }

    @Test
    void withAccessories() throws SQLException, ContainerException, InventoryException {
        gamePlayer().inventory().add(container.get(ItemService.class).create(2411), 1, 6);
        gamePlayer().inventory().add(container.get(ItemService.class).create(2414), 1, 7);
        gamePlayer().inventory().add(container.get(ItemService.class).create(2416), 1, 1);

        assertEquals(
            "200;1;0;1;Bob;1;10^100x100;0;;7b;1c8;315;970,96b,96e,,;;;;;;8;",
            new PlayerSprite(new ExplorationPlayer(gamePlayer())).toString()
        );
    }

    @Test
    void withOrientation() throws SQLException, ContainerException {
        ExplorationPlayer exploration = new ExplorationPlayer(gamePlayer());

        exploration.setOrientation(Direction.WEST);

        assertEquals(
            "200;4;0;1;Bob;1;10^100x100;0;;7b;1c8;315;,,,,;;;;;;8;",
            new PlayerSprite(exploration).toString()
        );
    }

    @Test
    void generateTwiceWithoutChangeConstantPart() throws SQLException {
        ExplorationPlayer exploration = new ExplorationPlayer(gamePlayer());

        assertEquals(
            "200;1;0;1;Bob;1;10^100x100;0;;7b;1c8;315;,,,,;;;;;;8;",
            new PlayerSprite(exploration).toString()
        );

        exploration.setOrientation(Direction.WEST);

        assertEquals(
            "200;4;0;1;Bob;1;10^100x100;0;;7b;1c8;315;,,,,;;;;;;8;",
            new PlayerSprite(exploration).toString()
        );
    }

    @Test
    void shouldRegenerateOnChange() throws SQLException, NoSuchFieldException, IllegalAccessException {
        ExplorationPlayer exploration = new ExplorationPlayer(gamePlayer());

        assertEquals("200;1;0;1;Bob;1;10^100x100;0;;7b;1c8;315;,,,,;;;;;;8;", new PlayerSprite(exploration).toString());

        exploration.player().restrictions().unset(Restrictions.Restriction.ALLOW_MOVE_ALL_DIRECTION);
        exploration.player().restrictions().set(Restrictions.Restriction.DENY_MERCHANT);
        exploration.player().restrictions().set(Restrictions.Restriction.DENY_ASSAULT);
        exploration.player().restrictions().set(Restrictions.Restriction.DENY_EXCHANGE);

        exploration.restrictions().refresh();
        assertEquals("200;1;0;1;Bob;1;10^100x100;0;;7b;1c8;315;,,,,;;;;;;d;", new PlayerSprite(exploration).toString());

        exploration.player().inventory()
            .add(container.get(ItemService.class).create(2411))
            .move(HelmetSlot.SLOT_ID, 1)
        ;
        assertEquals("200;1;0;1;Bob;1;10^100x100;0;;7b;1c8;315;,96b,,,;;;;;;d;", new PlayerSprite(exploration).toString());

        exploration.player().inventory()
            .add(container.get(ItemService.class).create(2416))
            .move(WeaponSlot.SLOT_ID, 1)
        ;
        assertEquals("200;1;0;1;Bob;1;10^100x100;0;;7b;1c8;315;970,96b,,,;;;;;;d;", new PlayerSprite(exploration).toString());

        exploration.player().inventory().bySlot(HelmetSlot.SLOT_ID).ifPresent(InventoryEntry::unequip);
        assertEquals("200;1;0;1;Bob;1;10^100x100;0;;7b;1c8;315;970,,,,;;;;;;d;", new PlayerSprite(exploration).toString());
    }
}
