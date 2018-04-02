package fr.quatrevieux.araknemu.game.listener.player.chat;

import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.account.event.CharacterCreationStarted;

import java.util.Set;

/**
 * Add chat channels on character creation
 */
final public class AddChatChannels implements Listener<CharacterCreationStarted> {
    final private GameConfiguration.ChatConfiguration configuration;
    final private Transformer<Set<ChannelType>> transformer;

    public AddChatChannels(GameConfiguration.ChatConfiguration configuration, Transformer<Set<ChannelType>> transformer) {
        this.configuration = configuration;
        this.transformer = transformer;
    }

    @Override
    public void on(CharacterCreationStarted event) {
        Set<ChannelType> channels = transformer.unserialize(
            configuration.defaultChannels()
        );

        if (event.character().account().isMaster()) {
            channels.addAll(
                transformer.unserialize(configuration.adminChannels())
            );
        }

        event.character().character().setChannels(channels);
    }

    @Override
    public Class<CharacterCreationStarted> event() {
        return CharacterCreationStarted.class;
    }
}
