package fr.quatrevieux.araknemu.network.realm.in;

import fr.quatrevieux.araknemu.network.in.ParserLoader;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

import java.util.Arrays;
import java.util.Collection;

/**
 * Load realm input packet parsers
 */
final public class RealmParserLoader implements ParserLoader {
    @Override
    public Collection<SinglePacketParser> load() {
        return Arrays.asList(
            new AskQueuePosition.Parser(),
            new AskServerList.Parser(),
            new ChooseServer.Parser()
        );
    }
}
