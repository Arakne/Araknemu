package fr.quatrevieux.araknemu.data.living.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.core.dbal.util.ConnectionPoolUtils;
import fr.quatrevieux.araknemu.data.living.entity.account.AccountBank;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountBankRepository;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * SQL implementation of {@link AccountBankRepository}
 */
final class SqlAccountBankRepository implements AccountBankRepository {
    private static class Loader implements RepositoryUtils.Loader<AccountBank> {
        @Override
        public AccountBank create(ResultSet rs) throws SQLException {
            return new AccountBank(
                rs.getInt("ACCOUNT_ID"),
                rs.getInt("SERVER_ID"),
                rs.getLong("BANK_KAMAS")
            );
        }

        @Override
        public AccountBank fillKeys(AccountBank entity, ResultSet keys) {
            throw new UnsupportedOperationException();
        }
    }

    final private ConnectionPoolUtils pool;
    final private RepositoryUtils<AccountBank> utils;

    public SqlAccountBankRepository(ConnectionPool pool) {
        this.pool = new ConnectionPoolUtils(pool);
        this.utils = new RepositoryUtils<>(this.pool, new Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            pool.query(
                "CREATE TABLE BANK (" +
                    "ACCOUNT_ID INTEGER," +
                    "SERVER_ID INTEGER," +
                    "BANK_KAMAS BIGINT," +
                    "PRIMARY KEY (ACCOUNT_ID, SERVER_ID)" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            pool.query("DROP TABLE BANK");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public AccountBank add(AccountBank entity) {
        utils.update(
            "REPLACE INTO BANK (`ACCOUNT_ID`, `SERVER_ID`, `BANK_KAMAS`) VALUES (?, ?, ?)",
            rs -> {
                rs.setInt(1, entity.accountId());
                rs.setInt(2, entity.serverId());
                rs.setLong(3, entity.kamas());
            }
        );

        return entity;
    }

    @Override
    public void delete(AccountBank entity) {
        utils.update("DELETE FROM BANK WHERE ACCOUNT_ID = ? AND SERVER_ID = ?", rs -> {
            rs.setInt(1, entity.accountId());
            rs.setInt(2, entity.serverId());
        });
    }

    @Override
    public AccountBank get(AccountBank entity) throws RepositoryException {
        try {
            return utils.findOne("SELECT * FROM BANK WHERE ACCOUNT_ID = ? AND SERVER_ID = ?", rs -> {
                rs.setInt(1, entity.accountId());
                rs.setInt(2, entity.serverId());
            });
        } catch (EntityNotFoundException e) {
            entity.setKamas(0); // Ensure that kamas are set to zero

            return entity;
        }
    }

    @Override
    public boolean has(AccountBank entity) {
        return utils.aggregate("SELECT COUNT(*) FROM BANK WHERE ACCOUNT_ID = ? AND SERVER_ID = ?", rs -> {
            rs.setInt(1, entity.accountId());
            rs.setInt(2, entity.serverId());
        }) > 0;
    }
}
