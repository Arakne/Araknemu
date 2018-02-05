package fr.quatrevieux.araknemu.game.admin.account;

import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.admin.Command;
import fr.quatrevieux.araknemu.game.admin.Context;
import fr.quatrevieux.araknemu.game.admin.SimpleContext;
import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextNotFoundException;

import java.util.Collection;

/**
 * Context for account
 */
final public class AccountContext implements Context {
    final private Context globalContext;
    final private GameAccount account;
    final private AccountRepository repository;

    final private Context context;

    public AccountContext(Context globalContext, GameAccount account, AccountRepository repository) {
        this.globalContext = globalContext;
        this.account = account;
        this.repository = repository;

        context = configure();
    }

    @Override
    public Command command(String name) throws CommandNotFoundException {
        return context.command(name);
    }

    @Override
    public Collection<Command> commands() {
        return context.commands();
    }

    @Override
    public Context child(String name) throws ContextNotFoundException {
        return context.child(name);
    }

    private Context configure() {
        return new SimpleContext(globalContext)
            .add(new Info(account, repository))
        ;
    }
}
