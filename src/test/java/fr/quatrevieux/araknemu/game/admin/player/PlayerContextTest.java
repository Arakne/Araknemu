package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.admin.NullContext;
import fr.quatrevieux.araknemu.game.admin.account.AccountContext;
import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextNotFoundException;
import fr.quatrevieux.araknemu.game.item.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerContextTest extends GameBaseCase {
    private PlayerContext context;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        context = new PlayerContext(
            gamePlayer(),
            new AccountContext(
                new NullContext(),
                gamePlayer().account(),
                container.get(AccountRepository.class)
            ),
            container.get(ItemService.class)
        );
    }

    @Test
    void commands() throws CommandNotFoundException {
        assertInstanceOf(Info.class, context.command("info"));
        assertInstanceOf(GetItem.class, context.command("getitem"));
        assertInstanceOf(SetLife.class, context.command("setlife"));
        assertInstanceOf(AddStats.class, context.command("addstats"));
        assertInstanceOf(AddXp.class, context.command("addxp"));
        assertInstanceOf(Restriction.class, context.command("restriction"));

        assertContainsType(Info.class, context.commands());
        assertContainsType(GetItem.class, context.commands());
        assertContainsType(SetLife.class, context.commands());
        assertContainsType(AddStats.class, context.commands());
        assertContainsType(AddXp.class, context.commands());
        assertContainsType(Restriction.class, context.commands());
    }

    @Test
    void children() throws ContextNotFoundException {
        assertInstanceOf(AccountContext.class, context.child("account"));
    }
}
