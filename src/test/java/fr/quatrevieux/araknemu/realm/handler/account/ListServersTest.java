package fr.quatrevieux.araknemu.realm.handler.account;

import fr.quatrevieux.araknemu.network.realm.in.AskServerList;
import fr.quatrevieux.araknemu.network.realm.out.ServerList;
import fr.quatrevieux.araknemu.realm.RealmBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ListServersTest extends RealmBaseCase {
    private ListServers handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new ListServers();
    }

    @Test
    void handle() {
        handler.handle(session, new AskServerList());

        ServerList list = new ServerList(ServerList.ONE_YEAR);

        list.add(new ServerList.Server(1, 1));

        requestStack.assertLast(list);
    }
}
