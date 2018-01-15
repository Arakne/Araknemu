package fr.quatrevieux.araknemu.common.account;

import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.network.adapter.Session;

/**
 * Abstract class for Living Account
 * @param <S> The session type
 */
abstract public class AbstractLivingAccount<S extends Session> implements LivingAccount<S> {
    final protected Account account;

    protected S session;

    public AbstractLivingAccount(Account account) {
        this.account = account;
    }

    @Override
    public boolean isMaster() {
        return !account.permissions().isEmpty();
    }

    @Override
    public int id() {
        return account.id();
    }

    @Override
    public String pseudo() {
        return account.pseudo();
    }

    @Override
    public int community() {
        return 0;
    }

    @Override
    public void attach(S session) {
        this.session = session;
    }

    @Override
    public void detach() {
        this.session = null;
    }

    @Override
    public boolean isLogged() {
        return session != null && session.isAlive();
    }
}
