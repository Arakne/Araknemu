package fr.quatrevieux.araknemu.network.game.out.account;

import fr.quatrevieux.araknemu.data.living.constraint.player.PlayerConstraints;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CharacterCreationErrorTest {
    @Test
    void generate() {
        assertEquals(
            "AAEn",
            new CharacterCreationError(PlayerConstraints.Error.CREATE_CHARACTER_BAD_NAME).toString()
        );
    }
}