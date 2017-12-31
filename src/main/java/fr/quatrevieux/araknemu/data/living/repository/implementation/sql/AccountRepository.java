package fr.quatrevieux.araknemu.data.living.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.dbal.repository.*;
import fr.quatrevieux.araknemu.core.dbal.util.ConnectionPoolUtils;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;

import java.sql.ResultSet;
import java.sql.SQLException;

final class AccountRepository implements fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository {
    private static class Loader implements RepositoryUtils.Loader<Account> {
        @Override
        public Account create(ResultSet rs) throws SQLException {
            return new Account(
                rs.getInt("ACCOUNT_ID"),
                rs.getString("USERNAME"),
                rs.getString("PASSWORD"),
                rs.getString("PSEUDO")
            );
        }

        @Override
        public Account fillKeys(Account entity, ResultSet keys) throws SQLException {
            return entity.withId(
                keys.getInt(1)
            );
        }
    }

    final private ConnectionPoolUtils pool;
    final private RepositoryUtils<Account> utils;

    public AccountRepository(ConnectionPool pool) {
        this.pool = new ConnectionPoolUtils(pool);
        this.utils = new RepositoryUtils<>(this.pool, new Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            pool.query(
                "CREATE TABLE ACCOUNT (" +
                    "ACCOUNT_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "USERNAME VARCHAR(32) UNIQUE," +
                    "PASSWORD VARCHAR(256)," +
                    "PSEUDO VARCHAR(32) UNIQUE" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            pool.query("DROP TABLE ACCOUNT");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public Account add(Account entity) {
        return utils.update(
            "INSERT INTO ACCOUNT (`USERNAME`, `PASSWORD`, `PSEUDO`) VALUES (?, ?, ?)",
            rs -> {
                rs.setString(1, entity.name());
                rs.setString(2, entity.password());
                rs.setString(3, entity.pseudo());
            },
            entity
        );
    }

    @Override
    public void delete(Account entity) {
        utils.update("DELETE FROM ACCOUNT WHERE ACCOUNT_ID = ?", rs -> rs.setInt(1, entity.id()));
    }

    @Override
    public Account get(Account entity) throws RepositoryException {
        return utils.findOne(
            "SELECT * FROM ACCOUNT WHERE ACCOUNT_ID = ?",
            rs -> rs.setInt(1, entity.id())
        );
    }

    @Override
    public boolean has(Account entity) {
        return utils.aggregate(
            "SELECT COUNT(*) FROM ACCOUNT WHERE ACCOUNT_ID = ?",
            rs -> rs.setInt(1, entity.id())
        ) > 0;
    }

    @Override
    public Account findByUsername(String username) throws RepositoryException {
        return utils.findOne(
            "SELECT * FROM ACCOUNT WHERE USERNAME = ?",
            rs -> rs.setString(1, username)
        );
    }
}
