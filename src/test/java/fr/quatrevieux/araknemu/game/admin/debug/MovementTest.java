package fr.quatrevieux.araknemu.game.admin.debug;

import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.admin.AdminService;
import fr.quatrevieux.araknemu.game.admin.AdminUser;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.network.game.out.game.FightStartPositions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MovementTest extends GameBaseCase {
    private Movement command;
    private AdminUser user;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();
        explorationPlayer();

        command = new Movement(container.get(MapTemplateRepository.class));
        user = container.get(AdminService.class).user(gamePlayer());

        requestStack.clear();
    }

    @Test
    void success() throws AdminException {
        command.execute(user, Arrays.asList("movement", "2"));

        requestStack.assertLast(
            new FightStartPositions(
                new List[] {
                    Arrays.asList(434),
                    new ArrayList()
                },
                0
            )
        );
    }
}
