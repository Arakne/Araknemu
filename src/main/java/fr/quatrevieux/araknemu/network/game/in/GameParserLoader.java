package fr.quatrevieux.araknemu.network.game.in;

import fr.quatrevieux.araknemu.network.game.in.account.*;
import fr.quatrevieux.araknemu.network.game.in.basic.AskDate;
import fr.quatrevieux.araknemu.network.game.in.basic.admin.AdminCommand;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;
import fr.quatrevieux.araknemu.network.game.in.chat.SubscribeChannels;
import fr.quatrevieux.araknemu.network.game.in.game.AskExtraInfo;
import fr.quatrevieux.araknemu.network.game.in.game.CreateGameRequest;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionCancel;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionAcknowledge;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionRequest;
import fr.quatrevieux.araknemu.network.game.in.info.ScreenInfo;
import fr.quatrevieux.araknemu.network.in.ParserLoader;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

import java.util.Arrays;
import java.util.Collection;

/**
 * Parser loader for game packets
 */
final public class GameParserLoader implements ParserLoader {
    @Override
    public Collection<SinglePacketParser> load() {
        return Arrays.asList(
            new LoginToken.Parser(),
            new AskCharacterList.Parser(),
            new AskRegionalVersion.Parser(),
            new AddCharacterRequest.Parser(),
            new ChoosePlayingCharacter.Parser(),
            new ClientUid.Parser(),
            new ScreenInfo.Parser(),
            new AskGift.Parser(),
            new CreateGameRequest.Parser(),
            new AskExtraInfo.Parser(),
            new GameActionRequest.Parser(),
            new GameActionAcknowledge.Parser(),
            new GameActionCancel.Parser(),
            new AskDate.Parser(),
            new Ping.Parser(),
            new Message.Parser(),
            new DeleteCharacterRequest.Parser(),
            new SubscribeChannels.Parser(),
            new AskRandomName.Parser(),
            new AdminCommand.Parser()
        );
    }
}
