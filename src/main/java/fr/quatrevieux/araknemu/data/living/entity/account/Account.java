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

package fr.quatrevieux.araknemu.data.living.entity.account;

import fr.quatrevieux.araknemu.common.account.Permission;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * AuthenticationAccount data
 */
public final class Account {
    private final int id;

    private String name;
    private String password;
    private String pseudo;
    private Set<Permission> permissions;
    private String question;
    private String answer;

    public Account(int id, String name, String password, String pseudo, Set<Permission> permissions, String question, String answer) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.pseudo = pseudo;
        this.permissions = permissions;
        this.question = question;
        this.answer = answer;
    }

    public Account(int id, String name, String password, String pseudo) {
        this(id, name, password, pseudo, EnumSet.noneOf(Permission.class), "", "");
    }

    public Account(int id) {
        this(id, null, null, null, null, null, null);
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String password() {
        return password;
    }

    public String pseudo() {
        return pseudo;
    }

    public Set<Permission> permissions() {
        return permissions;
    }

    public String question() {
        return question;
    }

    public String answer() {
        return answer;
    }

    /**
     * Change the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Define the entity id
     * This method will create a new instance of the account entity
     *
     * @param id The id to set
     *
     * @return The new entity instance
     */
    public Account withId(int id) {
        return new Account(
            id,
            this.name,
            this.password,
            this.pseudo,
            this.permissions,
            this.question,
            this.answer
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Account account = (Account) o;

        return
            id == account.id
            && Objects.equals(name, account.name)
            && Objects.equals(pseudo, account.pseudo)
            && Objects.equals(password, account.password)
        ;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (pseudo != null ? pseudo.hashCode() : 0);
        return result;
    }
}
