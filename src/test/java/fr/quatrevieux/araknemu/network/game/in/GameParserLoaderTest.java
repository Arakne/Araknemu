package fr.quatrevieux.araknemu.network.game.in;

import fr.quatrevieux.araknemu.network.game.in.account.*;
import fr.quatrevieux.araknemu.network.game.in.basic.AskDate;
import fr.quatrevieux.araknemu.network.game.in.basic.admin.AdminCommand;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;
import fr.quatrevieux.araknemu.network.game.in.chat.SubscribeChannels;
import fr.quatrevieux.araknemu.network.game.in.fight.FighterChangePlace;
import fr.quatrevieux.araknemu.network.game.in.fight.FighterReady;
import fr.quatrevieux.araknemu.network.game.in.game.AskExtraInfo;
import fr.quatrevieux.araknemu.network.game.in.game.CreateGameRequest;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionCancel;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionAcknowledge;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionRequest;
import fr.quatrevieux.araknemu.network.game.in.object.ObjectDeleteRequest;
import fr.quatrevieux.araknemu.network.game.in.object.ObjectMoveRequest;
import fr.quatrevieux.araknemu.network.game.in.object.ObjectUseRequest;
import fr.quatrevieux.araknemu.network.game.in.spell.SpellMove;
import fr.quatrevieux.araknemu.network.game.in.spell.SpellUpgrade;
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
        assertContainsInstance(GameActionRequest.Parser.class, parsers);
        assertContainsInstance(GameActionAcknowledge.Parser.class, parsers);
        assertContainsInstance(GameActionCancel.Parser.class, parsers);
        assertContainsInstance(AskDate.Parser.class, parsers);
        assertContainsInstance(Ping.Parser.class, parsers);
        assertContainsInstance(Message.Parser.class, parsers);
        assertContainsInstance(DeleteCharacterRequest.Parser.class, parsers);
        assertContainsInstance(SubscribeChannels.Parser.class, parsers);
        assertContainsInstance(AskRandomName.Parser.class, parsers);
        assertContainsInstance(AdminCommand.Parser.class, parsers);
        assertContainsInstance(ObjectMoveRequest.Parser.class, parsers);
        assertContainsInstance(AskBoost.Parser.class, parsers);
        assertContainsInstance(ObjectDeleteRequest.Parser.class, parsers);
        assertContainsInstance(ObjectUseRequest.Parser.class, parsers);
        assertContainsInstance(SpellMove.Parser.class, parsers);
        assertContainsInstance(SpellUpgrade.Parser.class, parsers);
        assertContainsInstance(FighterChangePlace.Parser.class, parsers);
        assertContainsInstance(FighterReady.Parser.class, parsers);
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