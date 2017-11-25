package fr.quatrevieux.araknemu.network.realm.in;

import fr.quatrevieux.araknemu.network.in.ParserLoader;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class RealmParserLoaderTest {
    @Test
    void packets() {
        ParserLoader loader = new RealmParserLoader();

        Collection<SinglePacketParser> parsers = loader.load();

        assertContainsInstance(AskServerList.Parser.class, parsers);
        assertContainsInstance(AskQueuePosition.Parser.class, parsers);
        assertContainsInstance(ChooseServer.Parser.class, parsers);
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