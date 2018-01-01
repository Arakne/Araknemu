package fr.quatrevieux.araknemu.network.game.in;

import fr.quatrevieux.araknemu.network.game.in.account.AskCharacterList;
import fr.quatrevieux.araknemu.network.game.in.account.ChoosePlayingCharacter;
import fr.quatrevieux.araknemu.network.game.in.account.LoginToken;
import fr.quatrevieux.araknemu.network.game.in.game.AskExtraInfo;
import fr.quatrevieux.araknemu.network.game.in.game.CreateGameRequest;
import fr.quatrevieux.araknemu.network.in.ParserLoader;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class GameParserLoaderTest {
    @Test
    void packets() {
        ParserLoader loader = new GameParserLoader();

        Collection<SinglePacketParser> parsers = loader.load();

        assertContainsInstance(LoginToken.Parser.class, parsers);
        assertContainsInstance(AskCharacterList.Parser.class, parsers);
        assertContainsInstance(ChoosePlayingCharacter.Parser.class, parsers);
        assertContainsInstance(AskExtraInfo.Parser.class, parsers);
        assertContainsInstance(CreateGameRequest.Parser.class, parsers);
    }

    public void assertContainsInstance(Class type, Collection collection) {
        for (Object o : collection) {
            if (type.isInstance(o)) {
                return;
            }
        }

        fail("Cannot found instance of " + type.getSimpleName());
    }
}