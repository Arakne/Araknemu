package fr.quatrevieux.araknemu.realm;

import fr.quatrevieux.araknemu.Araknemu;
import fr.quatrevieux.araknemu.DatabaseTestCase;
import fr.quatrevieux.araknemu.TestingDataSet;
import fr.quatrevieux.araknemu.core.config.Configuration;
import fr.quatrevieux.araknemu.core.config.DefaultConfiguration;
import fr.quatrevieux.araknemu.core.config.IniDriver;
import fr.quatrevieux.araknemu.core.dbal.DatabaseConfiguration;
import fr.quatrevieux.araknemu.core.dbal.DefaultDatabaseHandler;
import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.di.ItemPoolContainer;
import fr.quatrevieux.araknemu.data.living.repository.implementation.sql.LivingRepositoriesModule;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.network.realm.RealmSession;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationAccount;
import fr.quatrevieux.araknemu.realm.host.GameConnector;
import fr.quatrevieux.araknemu.realm.host.GameHost;
import fr.quatrevieux.araknemu.realm.host.HostService;
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
import java.util.Arrays;
import java.util.Stack;

public class RealmBaseCase extends DatabaseTestCase {
    static public class SendingRequestStack extends IoFilterAdapter {
        public Stack<Object> messages = new Stack<>();

        @Override
        public void messageSent(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
            messages.push(writeRequest.getMessage());
        }

        public void assertLast(Object packet) {
            assertLast(packet.toString());
        }

        public void assertLast(String packet) {
            Assertions.assertEquals(packet, messages.peek().toString());
        }

        public void assertCount(int count) {
            Assertions.assertEquals(count, messages.size());
        }

        public void assertAll(Object... packets) {
            Assertions.assertArrayEquals(
                Arrays.stream(packets).map(Object::toString).toArray(),
                messages.stream().map(Object::toString).toArray()
            );
        }

        public void assertEmpty() {
            Assertions.assertTrue(messages.isEmpty());
        }
    }

    static public class GameConnectorStub implements GameConnector {
        public boolean checkLogin;
        public String token;
        public AuthenticationAccount account;

        @Override
        public void checkLogin(AuthenticationAccount account, HostResponse<Boolean> response) {
            response.response(checkLogin);
            this.account = account;
        }

        @Override
        public void token(AuthenticationAccount account, HostResponse<String> response) {
            this.account = account;
            response.response(token);
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
    protected GameHost gameHost;
    protected GameConnectorStub connector;

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
        container.register(new LivingRepositoriesModule(
            app.database().get("realm")
        ));

        service = container.get(RealmService.class);
        configuration = service.configuration();

        ioSession = new DummySession();
        ioSession.setAttribute("testing");
        session = new RealmSession(ioSession);

        ioSession.getFilterChain().addLast("test", requestStack = new SendingRequestStack());
        ioHandler = service.ioHandler();

        dataSet = new TestingDataSet(container);
        dataSet
            .declare(Account.class, AccountRepository.class)
        ;

        container.get(HostService.class).declare(
            gameHost = new GameHost(
                connector = new GameConnectorStub(),
                1,
                1234,
                "127.0.0.1"
            )
        );

        gameHost.setCanLog(true);
        gameHost.setState(GameHost.State.ONLINE);
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
