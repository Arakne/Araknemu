package fr.quatrevieux.araknemu.game.handler.account;

import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.network.exception.CloseWithPacket;
import fr.quatrevieux.araknemu.network.game.in.account.ChoosePlayingCharacter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SelectCharacterTest extends GameBaseCase {
    private SelectCharacter handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new SelectCharacter(
            container.get(PlayerService.class)
        );

        login();

        dataSet.use(Player.class);
        insertRaces();
    }

    @Test
    void handleBadPlayer() throws Exception {
        try {
            handler.handle(session, new ChoosePlayingCharacter(123));
        } catch (CloseWithPacket e) {
            assertEquals("ASE", e.packet().toString());
        }
    }

    @Test
    void handleSuccess() throws Exception {
        int id = dataSet.push(new Player(-1, 1, 2, "Bob", Race.FECA, Sex.MALE, new Colors(123, 456, 789), 23, null)).id();

        handler.handle(session, new ChoosePlayingCharacter(id));

        requestStack.assertLast("ASK1|Bob|23||0|10|7b|1c8|315|");
    }
}
