package fr.quatrevieux.araknemu.data.living.constraint.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerConstraintsTest extends GameBaseCase {
    private PlayerConstraints constraints;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        constraints = new PlayerConstraints(
            container.get(PlayerRepository.class),
            configuration.player()
        );

        dataSet.use(Player.class);
    }

    @Test
    void emptyName() {
        assertFalse(constraints.check(Player.forCreation(1, 1, "", Race.FECA, Sex.MALE, new Colors(-1, -1, -1))));
        assertEquals(PlayerConstraints.Error.CREATE_CHARACTER_BAD_NAME, constraints.error());
    }

    @Test
    void nameTooShort() {
        assertFalse(constraints.check(Player.forCreation(1, 1, "a", Race.FECA, Sex.MALE, new Colors(-1, -1, -1))));
        assertEquals(PlayerConstraints.Error.CREATE_CHARACTER_BAD_NAME, constraints.error());
    }

    @Test
    void nameTooLong() {
        assertFalse(constraints.check(Player.forCreation(1, 1, "Myverylongnamewithtoomuchcharacters", Race.FECA, Sex.MALE, new Colors(-1, -1, -1))));
        assertEquals(PlayerConstraints.Error.CREATE_CHARACTER_BAD_NAME, constraints.error());
    }

    @Test
    void nameBadFormat() {
        assertFalse(constraints.check(Player.forCreation(1, 1, "-My-invalid-name-", Race.FECA, Sex.MALE, new Colors(-1, -1, -1))));
        assertEquals(PlayerConstraints.Error.CREATE_CHARACTER_BAD_NAME, constraints.error());
    }

    @Test
    void nameAlreadyUsed() throws ContainerException {
        dataSet.push(Player.forCreation(1, 1, "My-name", Race.FECA, Sex.MALE, new Colors(-1, -1, -1)));

        assertFalse(constraints.check(Player.forCreation(1, 1, "My-name", Race.FECA, Sex.MALE, new Colors(-1, -1, -1))));
        assertEquals(PlayerConstraints.Error.NAME_ALEREADY_EXISTS, constraints.error());
    }

    @Test
    void tooMuchCharacters() throws ContainerException {
        dataSet.push(Player.forCreation(1, 1, "One", Race.FECA, Sex.MALE, new Colors(-1, -1, -1)));
        dataSet.push(Player.forCreation(1, 1, "Two", Race.FECA, Sex.MALE, new Colors(-1, -1, -1)));
        dataSet.push(Player.forCreation(1, 1, "Three", Race.FECA, Sex.MALE, new Colors(-1, -1, -1)));
        dataSet.push(Player.forCreation(1, 1, "Four", Race.FECA, Sex.MALE, new Colors(-1, -1, -1)));
        dataSet.push(Player.forCreation(1, 1, "Five", Race.FECA, Sex.MALE, new Colors(-1, -1, -1)));

        assertFalse(constraints.check(Player.forCreation(1, 1, "My-name", Race.FECA, Sex.MALE, new Colors(-1, -1, -1))));
        assertEquals(PlayerConstraints.Error.CREATE_CHARACTER_FULL, constraints.error());
    }
}
