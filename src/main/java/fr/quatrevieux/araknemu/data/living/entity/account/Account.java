package fr.quatrevieux.araknemu.data.living.entity.account;

/**
 * AuthenticationAccount data
 */
final public class Account {
    final private int id;

    private String name;
    private String password;
    private String pseudo;

    public Account(int id, String name, String password, String pseudo) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.pseudo = pseudo;
    }

    public Account(int id) {
        this(id, null, null, null);
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

    public Account withId(int id) {
        return new Account(
            id,
            this.name,
            this.password,
            this.pseudo
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        if (id != account.id) return false;
        if (name != null ? !name.equals(account.name) : account.name != null) return false;
        if (password != null ? !password.equals(account.password) : account.password != null) return false;
        return pseudo != null ? pseudo.equals(account.pseudo) : account.pseudo == null;
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
