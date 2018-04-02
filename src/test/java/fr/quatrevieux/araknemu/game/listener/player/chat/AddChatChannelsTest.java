package fr.quatrevieux.araknemu.game.listener.player.chat;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.transformer.ChannelsTransformer;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.account.AccountCharacter;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.account.event.CharacterCreationStarted;
import fr.quatrevieux.araknemu.game.listener.player.chat.AddChatChannels;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class AddChatChannelsTest extends GameBaseCase {
    private AddChatChannels listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new AddChatChannels(
            container.get(GameConfiguration.class).chat(),
            new ChannelsTransformer()
        );
    }

    @Test
    void onSimpleCharacterCreation() throws ContainerException {
        AccountCharacter character = new AccountCharacter(
            new GameAccount(
                new Account(5, "simple", "", ""),
                container.get(AccountService.class),
                2
            ),
            new Player(2)
        );

        listener.on(
            new CharacterCreationStarted(character)
        );

        assertEquals(
            EnumSet.of(
                ChannelType.MESSAGES,
                ChannelType.FIGHT_TEAM,
                ChannelType.PRIVATE,
                ChannelType.GROUP,
                ChannelType.TRADE,
                ChannelType.RECRUITMENT,
                ChannelType.PVP,
                ChannelType.GUILD,
                ChannelType.INFO
            ),
            character.character().channels()
        );
    }

    @Test
    void onAdminCharacterCreation() throws ContainerException {
        AccountCharacter character = new AccountCharacter(
            new GameAccount(
                new Account(5, "admin", "", "admin", EnumSet.allOf(Permission.class), "", ""),
                container.get(AccountService.class),
                2
            ),
            new Player(2)
        );

        listener.on(
            new CharacterCreationStarted(character)
        );

        assertEquals(
            EnumSet.of(
                ChannelType.MESSAGES,
                ChannelType.FIGHT_TEAM,
                ChannelType.PRIVATE,
                ChannelType.GROUP,
                ChannelType.TRADE,
                ChannelType.RECRUITMENT,
                ChannelType.PVP,
                ChannelType.GUILD,
                ChannelType.INFO,
                ChannelType.ADMIN
            ),
            character.character().channels()
        );
    }
}
