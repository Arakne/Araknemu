package fr.quatrevieux.araknemu.data.living.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.core.dbal.util.ConnectionPoolUtils;
import fr.quatrevieux.araknemu.data.living.entity.account.AccountBank;
import fr.quatrevieux.araknemu.data.living.entity.account.BankItem;
import fr.quatrevieux.araknemu.data.living.repository.account.BankItemRepository;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * SQL implementation for {@link BankItem} repository
 */
final class SqlBankItemRepository implements BankItemRepository {
    private class Loader implements RepositoryUtils.Loader<BankItem> {
        @Override
        public BankItem create(ResultSet rs) throws SQLException {
            return new BankItem(
                rs.getInt("ACCOUNT_ID"),
                rs.getInt("SERVER_ID"),
                rs.getInt("ITEM_ENTRY_ID"),
                rs.getInt("ITEM_TEMPLATE_ID"),
                effectsTransformer.unserialize(rs.getString("ITEM_EFFECTS")),
                rs.getInt("QUANTITY")
            );
        }

        @Override
        public BankItem fillKeys(BankItem entity, ResultSet keys) {
            throw new UnsupportedOperationException();
        }
    }

    final private ConnectionPoolUtils pool;
    final private RepositoryUtils<BankItem> utils;
    final private Transformer<List<ItemTemplateEffectEntry>> effectsTransformer;

    public SqlBankItemRepository(ConnectionPool pool, Transformer<List<ItemTemplateEffectEntry>> effectsTransformer) {
        this.pool = new ConnectionPoolUtils(pool);
        this.effectsTransformer = effectsTransformer;
        this.utils = new RepositoryUtils<>(this.pool, new Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            pool.query(
                "CREATE TABLE BANK_ITEM (" +
                    "ACCOUNT_ID INTEGER," +
                    "SERVER_ID INTEGER," +
                    "ITEM_ENTRY_ID INTEGER," +
                    "ITEM_TEMPLATE_ID INTEGER," +
                    "ITEM_EFFECTS TEXT," +
                    "QUANTITY SMALLINT," +
                    "PRIMARY KEY (ACCOUNT_ID, SERVER_ID, ITEM_ENTRY_ID)" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            pool.query("DROP TABLE BANK_ITEM");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void update(BankItem item) {
        int count = utils.update(
            "UPDATE BANK_ITEM SET QUANTITY = ? WHERE ACCOUNT_ID = ? AND SERVER_ID = ? AND ITEM_ENTRY_ID = ?",
            stmt -> {
                stmt.setInt(1, item.quantity());
                stmt.setInt(2, item.accountId());
                stmt.setInt(3, item.serverId());
                stmt.setInt(4, item.entryId());
            }
        );

        if (count != 1) {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public void delete(BankItem item) {
        int count = utils.update(
            "DELETE FROM BANK_ITEM WHERE ACCOUNT_ID = ? AND SERVER_ID = ? AND ITEM_ENTRY_ID = ?",
            stmt -> {
                stmt.setInt(1, item.accountId());
                stmt.setInt(2, item.serverId());
                stmt.setInt(3, item.entryId());
            }
        );

        if (count != 1) {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public BankItem add(BankItem entity) throws RepositoryException {
        utils.update(
            "INSERT INTO BANK_ITEM (ACCOUNT_ID, SERVER_ID, ITEM_ENTRY_ID, ITEM_TEMPLATE_ID, ITEM_EFFECTS, QUANTITY) VALUES (?, ?, ?, ?, ?, ?)",
            stmt -> {
                stmt.setInt(1,    entity.accountId());
                stmt.setInt(2,    entity.serverId());
                stmt.setInt(3,    entity.entryId());
                stmt.setInt(4,    entity.itemTemplateId());
                stmt.setString(5, effectsTransformer.serialize(entity.effects()));
                stmt.setInt(6,    entity.quantity());
            }
        );

        return entity;
    }

    @Override
    public BankItem get(BankItem entity) throws RepositoryException {
        return utils.findOne(
            "SELECT * FROM BANK_ITEM WHERE ACCOUNT_ID = ? AND SERVER_ID = ? AND ITEM_ENTRY_ID = ?",
            stmt -> {
                stmt.setInt(1, entity.accountId());
                stmt.setInt(2, entity.serverId());
                stmt.setInt(3, entity.entryId());
            }
        );
    }

    @Override
    public boolean has(BankItem entity) throws RepositoryException {
        return utils.aggregate(
            "SELECT COUNT(*) FROM BANK_ITEM WHERE ACCOUNT_ID = ? AND SERVER_ID = ? AND ITEM_ENTRY_ID = ?",
            stmt -> {
                stmt.setInt(1, entity.accountId());
                stmt.setInt(2, entity.serverId());
                stmt.setInt(3, entity.entryId());
            }
        ) > 0;
    }

    @Override
    public Collection<BankItem> byBank(AccountBank bank) {
        return utils.findAll(
            "SELECT * FROM BANK_ITEM WHERE ACCOUNT_ID = ? AND SERVER_ID = ?",
            stmt -> {
                stmt.setInt(1, bank.accountId());
                stmt.setInt(2, bank.serverId());
            }
        );
    }

    @Override
    public int count(AccountBank bank) throws RepositoryException {
        return utils.aggregate(
            "SELECT COUNT(*) FROM BANK_ITEM WHERE ACCOUNT_ID = ? AND SERVER_ID = ?",
            stmt -> {
                stmt.setInt(1, bank.accountId());
                stmt.setInt(2, bank.serverId());
            }
        );
    }
}
