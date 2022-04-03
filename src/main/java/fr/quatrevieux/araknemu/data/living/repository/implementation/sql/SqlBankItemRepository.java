/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.data.living.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.executor.QueryExecutor;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.dbal.repository.Record;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.data.living.entity.account.AccountBank;
import fr.quatrevieux.araknemu.data.living.entity.account.BankItem;
import fr.quatrevieux.araknemu.data.living.repository.account.BankItemRepository;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.util.Asserter;
import org.checkerframework.checker.index.qual.NonNegative;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * SQL implementation for {@link BankItem} repository
 */
final class SqlBankItemRepository implements BankItemRepository {
    private final QueryExecutor executor;
    private final RepositoryUtils<BankItem> utils;
    private final Transformer<List<ItemTemplateEffectEntry>> effectsTransformer;

    public SqlBankItemRepository(QueryExecutor executor, Transformer<List<ItemTemplateEffectEntry>> effectsTransformer) {
        this.executor = executor;
        this.effectsTransformer = effectsTransformer;
        this.utils = new RepositoryUtils<>(this.executor, new Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            executor.query(
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
            executor.query("DROP TABLE BANK_ITEM");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void update(BankItem item) {
        final int count = utils.update(
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
        final int count = utils.update(
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
    public @NonNegative int count(AccountBank bank) throws RepositoryException {
        return Asserter.castNonNegative(utils.aggregate(
            "SELECT COUNT(*) FROM BANK_ITEM WHERE ACCOUNT_ID = ? AND SERVER_ID = ?",
            stmt -> {
                stmt.setInt(1, bank.accountId());
                stmt.setInt(2, bank.serverId());
            }
        ));
    }

    private class Loader implements RepositoryUtils.Loader<BankItem> {
        @Override
        public BankItem create(Record record) throws SQLException {
            return new BankItem(
                record.getInt("ACCOUNT_ID"),
                record.getInt("SERVER_ID"),
                record.getInt("ITEM_ENTRY_ID"),
                record.getInt("ITEM_TEMPLATE_ID"),
                record.unserialize("ITEM_EFFECTS", effectsTransformer),
                record.getNonNegativeInt("QUANTITY")
            );
        }

        @Override
        public BankItem fillKeys(BankItem entity, ResultSet keys) {
            throw new UnsupportedOperationException();
        }
    }
}
