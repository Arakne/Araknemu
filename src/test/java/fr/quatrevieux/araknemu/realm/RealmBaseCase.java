package fr.quatrevieux.araknemu.realm;

import fr.quatrevieux.araknemu.Araknemu;
import fr.quatrevieux.araknemu.DatabaseTestCase;
import fr.quatrevieux.araknemu.TestingDataSet;
import fr.quatrevieux.araknemu.core.config.Configuration;
import fr.quatrevieux.araknemu.core.config.DefaultConfiguration;
import fr.quatrevieux.araknemu.core.config.IniDriver;
import fr.quatrevieux.araknemu.core.dbal.DatabaseConfiguration;
import fr.quatrevieux.araknemu.core.dbal.DefaultDatabaseHandler;
import fr.quatrevieux.araknemu.core.dbal.repository.Repository;
import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.di.ItemPoolContainer;
import fr.quatrevieux.araknemu.data.RepositoriesModule;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.network.realm.RealmSession;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.DummySession;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.ini4j.Ini;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Stack;

public class RealmBaseCase extends DatabaseTestCase {
    static public class SendingRequestStack extends IoFilterAdapter {
        public Stack<Object> messages = new Stack<>();

        @Override
        public void messageSent(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
            messages.push(writeRequest.getMessage());
        }

        public void assertLast(Object packet) {
            Assertions.assertEquals(packet, messages.peek());
        }

        public void assertLast(String packet) {
            Assertions.assertEquals(packet, messages.peek().toString());
        }

        public void assertCount(int count) {
            Assertions.assertEquals(count, messages.size());
        }

        public void assertAll(Object... packets) {
            Assertions.assertArrayEquals(packets, messages.toArray());
        }

        public void assertEmpty() {
            Assertions.assertTrue(messages.isEmpty());
        }
    }

    protected Container container;
    protected RealmConfiguration configuration;
    protected RealmService service;
    protected RealmSession session;
    protected DummySession ioSession;
    protected SendingRequestStack requestStack;
    protected IoHandler ioHandler;
    protected Araknemu app;
    protected TestingDataSet dataSet;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        Configuration conf = new DefaultConfiguration(
            new IniDriver(new Ini(new File("src/test/test_config.ini")))
        );

        app = new Araknemu(
            conf,
            new DefaultDatabaseHandler(
                conf.module(DatabaseConfiguration.class)
            )
        );

        container = new ItemPoolContainer();
        container.register(new RealmModule(app));
        container.register(new RepositoriesModule());

        service = container.get(RealmService.class);
        configuration = service.configuration();

        ioSession = new DummySession();
        session = new RealmSession(ioSession, true);

        ioSession.getFilterChain().addLast("test", requestStack = new SendingRequestStack());
        ioHandler = service.ioHandler();

        dataSet = new TestingDataSet(container);
        dataSet
            .declare(Account.class, AccountRepository.class)
        ;
    }

    @AfterEach
    void tearDown() throws ContainerException {
        dataSet.destroy();
    }

    public void assertClosed() {
        Assertions.assertTrue(ioSession.isClosing());
    }

    public void sendPacket(Object packet) throws Exception {
        ioHandler.messageReceived(ioSession, packet);
    }
}
