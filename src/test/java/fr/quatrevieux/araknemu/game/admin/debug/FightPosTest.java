package fr.quatrevieux.araknemu.game.admin.debug;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.admin.AdminService;
import fr.quatrevieux.araknemu.game.admin.AdminUser;
import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.LogType;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.network.game.out.basic.admin.CommandResult;
import fr.quatrevieux.araknemu.network.game.out.game.FightStartPositions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class FightPosTest extends GameBaseCase {
    private FightPos command;
    private AdminUser user;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();
        explorationPlayer();

        command = new FightPos(container.get(MapTemplateRepository.class));
        user = container.get(AdminService.class).user(gamePlayer());

        requestStack.clear();
    }

    @Test
    void hide() throws AdminException {
        command.execute(user, Arrays.asList("fightpos", "hide"));

        requestStack.assertLast("GV");
    }

    @Test
    void noFightPos() throws AdminException {
        user.player().setPosition(new Position(10300, 123));

        command.execute(user, Arrays.asList("fightpos"));

        requestStack.assertLast(
            new CommandResult(LogType.ERROR, "No fight places found")
        );
    }

    @Test
    void noDisplay() throws AdminException {
        user.player().setPosition(new Position(10340, 123));

        command.execute(user, Arrays.asList("fightpos"));

        requestStack.assertLast(
            new CommandResult(LogType.DEFAULT, "Places : [[55, 83, 114, 127, 128, 170, 171, 183, 185, 198], [48, 63, 75, 90, 92, 106, 121, 122, 137, 150]]")
        );
    }

    @Test
    void show() throws AdminException, ContainerException {
        user.player().setPosition(new Position(10340, 123));

        command.execute(user, Arrays.asList("fightpos", "show"));

        requestStack.assertAll(
            new CommandResult(LogType.DEFAULT, "Places : [[55, 83, 114, 127, 128, 170, 171, 183, 185, 198], [48, 63, 75, 90, 92, 106, 121, 122, 137, 150]]"),
            new FightStartPositions(container.get(MapTemplateRepository.class).get(10340).fightPlaces(), 0)
        );
    }
}
