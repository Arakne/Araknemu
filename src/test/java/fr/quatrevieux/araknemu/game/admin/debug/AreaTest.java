package fr.quatrevieux.araknemu.game.admin.debug;

import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.admin.AdminService;
import fr.quatrevieux.araknemu.game.admin.AdminUser;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffectService;
import fr.quatrevieux.araknemu.network.game.out.game.FightStartPositions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class AreaTest extends GameBaseCase {
    private Area command;
    private AdminUser user;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();
        explorationPlayer();

        command = new Area(container.get(SpellEffectService.class));
        user = container.get(AdminService.class).user(gamePlayer());

        requestStack.clear();
    }

    @Test
    void show() {
        user.player().setPosition(new Position(10340, 123));

        command.execute(user, Arrays.asList("area", "Cb"));

        requestStack.assertContains(FightStartPositions.class);
    }
}
