package fr.quatrevieux.araknemu.game.handler.account;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.account.TokenService;
import fr.quatrevieux.araknemu.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.network.exception.CloseWithPacket;
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
    public void handle(GameSession session, LoginToken packet) throws Exception {
        if (session.isLogged()) {
            throw new CloseImmediately("Account already attached");
        }

        GameAccount account;

        try {
            account = service.load(tokens.get(packet.token()));
        } catch(NoSuchElementException | EntityNotFoundException e) {
            throw new CloseWithPacket(new LoginTokenError());
        }

        account.attach(session);
        session.write(new LoginTokenSuccess(account.community()));
    }

    @Override
    public Class<LoginToken> packet() {
        return LoginToken.class;
    }
}
