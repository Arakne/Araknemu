package fr.quatrevieux.araknemu.game.chat.channel;

import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.chat.ChatException;
import fr.quatrevieux.araknemu.game.chat.event.BroadcastedMessage;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;
import fr.quatrevieux.araknemu.network.game.out.info.Information;

/**
 * The fight team chat : send to all teammates fighters
 */
final public class FightTeamChannel implements Channel {
    @Override
    public ChannelType type() {
        return ChannelType.FIGHT_TEAM;
    }

    @Override
    public void send(GamePlayer from, Message message) throws ChatException {
        if (!from.isFighting()) {
            throw new ChatException(ChatException.Error.UNAUTHORIZED);
        }

        if (!message.items().isEmpty()) {
            from.send(Information.cannotPostItemOnChannel());
            return;
        }

        BroadcastedMessage event = new BroadcastedMessage(
            type(),
            from,
            message.message(),
            ""
        );

        from
            .fighter()
            .team()
            .fighters()
            .forEach(player -> player.dispatch(event))
        ;
    }
}
