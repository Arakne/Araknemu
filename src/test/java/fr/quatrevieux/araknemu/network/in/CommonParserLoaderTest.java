package fr.quatrevieux.araknemu.network.in;

import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class CommonParserLoaderTest {
    @Test
    void packets() {
        ParserLoader loader = new CommonParserLoader();

        Collection<SinglePacketParser> parsers = loader.load();

        assertContainsInstance(PingResponse.Parser.class, parsers);
        assertContainsInstance(AskQueuePosition.Parser.class, parsers);
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
