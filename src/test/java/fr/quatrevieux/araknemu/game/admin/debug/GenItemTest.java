package fr.quatrevieux.araknemu.game.admin.debug;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.world.transformer.ItemEffectsTransformer;
import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.world.item.effect.ItemEffect;
import fr.quatrevieux.araknemu.network.game.out.chat.MessageSent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class GenItemTest extends CommandTestCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushItemTemplates();

        command = new GenItem(container.get(ItemService.class));
    }

    @Test
    void executeSimple() throws ContainerException, SQLException, AdminException {
        execute("genitem", "284");

        assertOutput("Generate item Sel (284) : Resource");
    }

    @Test
    void executeMax() throws ContainerException, SQLException, AdminException {
        execute("genitem", "--max", "39");

        assertOutput(
            "Generate item Petite Amulette du Hibou (39) : Wearable",
            "Effects :",
            "===========================",
            "CharacteristicEffect{ADD_INTELLIGENCE:2}",
            "==========================="
        );
    }

    @Test
    void executeRealUser() throws ContainerException, SQLException, AdminException {
        command.execute(user(), Arrays.asList("genitem", "39"));

        requestStack.assertLast(
            new MessageSent(
                gamePlayer(),
                ChannelType.ADMIN,
                "Generated item : °0",
                "39!7e#2#0#0#0d0+2"
            )
        );
    }
}
