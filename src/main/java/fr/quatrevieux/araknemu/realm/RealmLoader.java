package fr.quatrevieux.araknemu.realm;

import fr.quatrevieux.araknemu.Araknemu;
import fr.quatrevieux.araknemu.data.RepositoriesContainer;
import fr.quatrevieux.araknemu.network.in.*;
import fr.quatrevieux.araknemu.network.realm.RealmIoHandler;
import fr.quatrevieux.araknemu.network.realm.in.Credentials;
import fr.quatrevieux.araknemu.network.realm.in.DofusVersion;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationService;
import fr.quatrevieux.araknemu.realm.handler.*;
import fr.quatrevieux.araknemu.realm.handler.account.Authenticate;
import fr.quatrevieux.araknemu.realm.handler.account.ListServers;

import java.sql.SQLException;

/**
 * Load RealmService
 */
final public class RealmLoader {
    final private Araknemu app;

    public RealmLoader(Araknemu app) {
        this.app = app;
    }

    /**
     * Load the service
     */
    public RealmService load() throws SQLException {
        RealmConfiguration configuration = app.configuration().module(RealmConfiguration.class);
        RepositoriesContainer repositories = new RepositoriesContainer(
            app.database().get("realm")
        );

        AuthenticationService authenticationService = new AuthenticationService(
            repositories.accounts()
        );

        return new RealmService(
            configuration,
            new RealmIoHandler(
                new DefaultDispatcher<>(
                    new PacketHandler[] {
                        new StartSession(),
                        new StopSession(authenticationService),
                        new CheckDofusVersion(configuration),
                        new Authenticate(authenticationService),
                        new CheckQueuePosition(),
                        new ListServers(),
                        new PongResponse()
                    }
                ),
                new PacketParser[] {DofusVersion.parser(), Credentials.parser()},
                new AggregatePacketParser(
                    new AggregateParserLoader(
                        new ParserLoader[] {
                            new PackageParserLoader("fr.quatrevieux.araknemu.network.realm.in"),
                            new PackageParserLoader("fr.quatrevieux.araknemu.network.in")
                        }
                    ).load()
                )
            )
        );
    }
}
