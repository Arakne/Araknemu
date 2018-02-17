package fr.quatrevieux.araknemu.game.event.listener.player.inventory;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.common.CharacteristicsChanged;
import fr.quatrevieux.araknemu.game.event.inventory.EquipmentChanged;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.world.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class UpdateStuffStatsTest extends GameBaseCase {
    private UpdateStuffStats listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new UpdateStuffStats(gamePlayer(true));

        requestStack.clear();
    }

    @Test
    void onEquipmentChanged() throws SQLException, ContainerException {
        GamePlayer player = gamePlayer();

        AtomicReference<CharacteristicsChanged> ref = new AtomicReference<>();
        player.dispatcher().add(CharacteristicsChanged.class, ref::set);

        listener.on(
            new EquipmentChanged(Mockito.mock(ItemEntry.class), 0)
        );

        assertNotNull(ref.get());

        requestStack.assertLast(
            new Stats(player)
        );
    }
}
