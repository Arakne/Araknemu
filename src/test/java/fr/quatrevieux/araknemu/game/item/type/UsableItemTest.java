package fr.quatrevieux.araknemu.game.item.type;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;
import fr.quatrevieux.araknemu.network.game.out.info.Information;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class UsableItemTest extends GameBaseCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushUsableItems();
    }

    @Test
    void effects() throws ContainerException {
        UsableItem item = (UsableItem) container.get(ItemService.class).create(283);

        assertCount(1, item.effects());
        assertInstanceOf(UseEffect.class, item.effects().get(0));
    }

    @Test
    void equalsSameInstance() throws ContainerException {
        UsableItem item = (UsableItem) container.get(ItemService.class).create(283);

        assertTrue(item.equals(item));
    }

    @Test
    void equalsTwoSameItem() throws ContainerException {
        assertEquals(
            container.get(ItemService.class).create(283),
            container.get(ItemService.class).create(283)
        );
    }

    @Test
    void notEquals() throws ContainerException {
        assertNotEquals(
            container.get(ItemService.class).create(283),
            container.get(ItemService.class).create(800)
        );
    }

    @Test
    void hashCodeEqualsObjects() throws ContainerException {
        assertEquals(
            container.get(ItemService.class).create(283).hashCode(),
            container.get(ItemService.class).create(283).hashCode()
        );
    }

    @Test
    void hashCodeNotEqualsObjects() throws ContainerException {
        assertNotEquals(
            container.get(ItemService.class).create(283).hashCode(),
            container.get(ItemService.class).create(800).hashCode()
        );
    }

    @Test
    void checkOnSelfSuccess() throws ContainerException, SQLException {
        UsableItem item = (UsableItem) container.get(ItemService.class).create(800);

        assertTrue(item.check(explorationPlayer()));
    }

    @Test
    void checkOnSelfNotValid() throws SQLException, ContainerException {
        UsableItem item = (UsableItem) container.get(ItemService.class).create(468);

        assertFalse(item.check(explorationPlayer()));
    }

    @Test
    void checkOnTargetNotValid() throws Exception {
        UsableItem item = (UsableItem) container.get(ItemService.class).create(800);

        assertFalse(item.checkTarget(explorationPlayer(), new ExplorationPlayer(makeOtherPlayer()), 0));
    }

    @Test
    void checkOnTargetSuccess() throws Exception {
        UsableItem item = (UsableItem) container.get(ItemService.class).create(468);

        ExplorationPlayer target = new ExplorationPlayer(makeOtherPlayer());
        target.life().set(10);

        assertTrue(item.checkTarget(explorationPlayer(), target, 0));
    }

    @Test
    void applyOnSelf() throws ContainerException, SQLException {
        UsableItem item = (UsableItem) container.get(ItemService.class).create(800);

        item.apply(explorationPlayer());

        assertEquals(1, explorationPlayer().characteristics().base().get(Characteristic.AGILITY));
        requestStack.assertLast(Information.characteristicBoosted(Characteristic.AGILITY, 1));
    }

    @Test
    void applyOnTarget() throws Exception {
        UsableItem item = (UsableItem) container.get(ItemService.class).create(468);

        ExplorationPlayer target = new ExplorationPlayer(makeOtherPlayer());
        target.life().set(10);

        item.applyToTarget(explorationPlayer(), target, 0);
        assertEquals(20, target.life().current());
    }
}
