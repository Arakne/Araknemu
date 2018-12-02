package fr.quatrevieux.araknemu.game.listener.player.inventory;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.characteristic.event.CharacteristicsChanged;
import fr.quatrevieux.araknemu.game.player.inventory.event.EquipmentChanged;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.characteristic.SpecialEffects;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

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

        requestStack.assertLast(
            new Stats(player.properties())
        );
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
