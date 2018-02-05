package fr.quatrevieux.araknemu.game.admin.account;

import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.admin.Context;
import fr.quatrevieux.araknemu.game.admin.ContextResolver;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;

/**
 * Context resolver for account
 */
final public class AccountContextResolver implements ContextResolver {
    final private AccountService service;
    final private AccountRepository repository;

    public AccountContextResolver(AccountService service, AccountRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @Override
    public AccountContext resolve(Context globalContext, Object argument) throws ContextException {
        if (argument instanceof GameAccount) {
            return resolveByAccount(globalContext, GameAccount.class.cast(argument));
        }

        throw new ContextException("Invalid argument : " + argument);
    }

    @Override
    public String type() {
        return "account";
    }

    private AccountContext resolveByAccount(Context globalContext, GameAccount account) {
        return new AccountContext(globalContext, account, repository);
    }
}
