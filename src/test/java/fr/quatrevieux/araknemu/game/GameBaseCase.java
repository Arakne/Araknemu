package fr.quatrevieux.araknemu.game;

import fr.quatrevieux.araknemu.Araknemu;
import fr.quatrevieux.araknemu.DatabaseTestCase;
import fr.quatrevieux.araknemu.TestingDataSet;
import fr.quatrevieux.araknemu.core.config.Configuration;
import fr.quatrevieux.araknemu.core.config.DefaultConfiguration;
import fr.quatrevieux.araknemu.core.config.IniDriver;
import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.dbal.DatabaseConfiguration;
import fr.quatrevieux.araknemu.core.dbal.DefaultDatabaseHandler;
import fr.quatrevieux.araknemu.core.dbal.util.ConnectionPoolUtils;
import fr.quatrevieux.araknemu.core.di.*;
import fr.quatrevieux.araknemu.data.RepositoriesModule;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerRace;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerRaceRepository;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.connector.RealmConnector;
import fr.quatrevieux.araknemu.network.game.GameSession;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.DummySession;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.ini4j.Ini;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.io.File;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Stack;

public class GameBaseCase extends DatabaseTestCase {
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

    static public class ConnectorModule implements ContainerModule {
        @Override
        public void configure(ContainerConfigurator configurator) {
            configurator.persist(
                RealmConnector.class,
                c -> Mockito.mock(RealmConnector.class)
            );
        }
    }

    protected Container container;
    protected GameConfiguration configuration;
    protected DummySession ioSession;
    protected GameSession session;
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
        container.register(new ConnectorModule());
        container.register(new GameModule(app));
        container.register(new RepositoriesModule());

        configuration = container.get(GameConfiguration.class);

        ioSession = new DummySession();
        ioSession.setAttribute("testing");
        session = new GameSession(ioSession);

        ioSession.getFilterChain().addLast("test", requestStack = new SendingRequestStack());
        //ioHandler = service.ioHandler();

        dataSet = new TestingDataSet(container);
        dataSet
            .declare(Account.class, AccountRepository.class)
            .declare(Player.class, PlayerRepository.class)
            .declare(PlayerRace.class, PlayerRaceRepository.class)
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

    public void login() throws ContainerException {
        session.attach(
            new GameAccount(
                new Account(1),
                container.get(AccountService.class),
                1
            )
        );
    }

    public void insertRaces() throws SQLException, ContainerException {
        dataSet.use(PlayerRace.class);

        ConnectionPoolUtils utils = new ConnectionPoolUtils(container.get(ConnectionPool.class));

        if (dataSet.repository(PlayerRace.class).has(new PlayerRace(Race.FECA, null, null))) {
            return;
        }

        utils.query(
            "INSERT INTO PLAYER_RACE (RACE_ID, RACE_NAME, RACE_STATS) VALUES " +
                "(1,  'Feca',     '8:1;9:34;a:6;b:3;j:1;')," +
                "(2,  'Osamodas', '8:1;9:34;a:6;b:3;j:1;')," +
                "(3,  'Enutrof',  '8:1;9:3o;a:6;b:3;j:1;')," +
                "(4,  'Sram',     '8:1;9:34;a:6;b:3;j:1;')," +
                "(5,  'Xelor',    '8:1;9:34;a:6;b:3;j:1;')," +
                "(6,  'Ecaflip',  '8:1;9:34;a:6;b:3;j:1;')," +
                "(7,  'Eniripsa', '8:1;9:34;a:6;b:3;j:1;')," +
                "(8,  'Iop',      '8:1;9:34;a:6;b:3;j:1;')," +
                "(9,  'Cra',      '8:1;9:34;a:6;b:3;j:1;')," +
                "(10, 'Sadida',   '8:1;9:34;a:6;b:3;j:1;')," +
                "(11, 'Sacrieur', '8:1;9:34;a:6;b:3;j:1;')," +
                "(12, 'Pandawa',  '8:1;9:34;a:6;b:3;j:1;')"
        );
    }
}
