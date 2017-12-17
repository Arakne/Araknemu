package fr.quatrevieux.araknemu.network.game.in;

import fr.quatrevieux.araknemu.network.game.in.account.*;
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
            new AskGift.Parser()
        );
    }
}
