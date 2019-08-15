package fr.quatrevieux.araknemu.game.exploration.npc.dialog.parameter;

import fr.quatrevieux.araknemu.game.account.bank.BankService;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;

/**
 * Resolve the bank cost
 */
final public class BankCostResolver implements VariableResolver {
    final private BankService service;

    public BankCostResolver(BankService service) {
        this.service = service;
    }

    @Override
    public String name() {
        return "bankCost";
    }

    @Override
    public Object value(ExplorationPlayer player) {
        return service.cost(player.account());
    }
}
