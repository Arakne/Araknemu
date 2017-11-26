package fr.quatrevieux.araknemu.game.handler.account;

import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.account.TokenService;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.account.LoginToken;
import fr.quatrevieux.araknemu.network.game.out.account.LoginTokenError;
import fr.quatrevieux.araknemu.network.game.out.account.LoginTokenSuccess;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

import java.util.NoSuchElementException;

/**
 * Handle login token to start client session
 */
final public class Login implements PacketHandler<GameSession, LoginToken> {
    final private TokenService tokens;
    final private AccountService service;

    public Login(TokenService tokens, AccountService service) {
        this.tokens = tokens;
        this.service = service;
    }

    @Override
    public void handle(GameSession session, LoginToken packet) {
        GameAccount account;

        try {
            account = service.load(tokens.get(packet.token()));
        } catch(NoSuchElementException e) {
            session.write(new LoginTokenError());
            session.close();
            return;
        }

        account.attach(session);
        session.write(new LoginTokenSuccess(account.community()));
    }

    @Override
    public Class<LoginToken> packet() {
        return LoginToken.class;
    }
}
