package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.experience.event.PlayerLevelUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RebuildLifePointsOnLevelUpTest extends GameBaseCase {
    private GamePlayer player;
    private RebuildLifePointsOnLevelUp listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new RebuildLifePointsOnLevelUp(
            player = gamePlayer()
        );

        player.dispatcher().remove(RebuildLifePointsOnLevelUp.class);
    }

    @Test
    void onCharacteristicsChanged() {
        player.properties().experience().add(860000);
        listener.on(new PlayerLevelUp(51));

        assertEquals(300, player.properties().life().max());
    }
}