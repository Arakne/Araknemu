package fr.quatrevieux.araknemu.network.realm.out;

/**
 * An error occurs during login process
 */
final public class LoginError {
    final static public char ALREADY_LOGGED_GAME_SERVER = 'c';
    final static public char ALREADY_LOGGED = 'a';
    final static public char BANNED = 'b';
    final static public char U_DISCONNECT_ACCOUNT = 'd';
    final static public char KICKED = 'k';
    final static public char LOGIN_ERROR = 'f';
    
    final private char errorType;

    public LoginError(char errorType) {
        this.errorType = errorType;
    }

    public String toString(){
        return "AlE" + errorType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoginError that = (LoginError) o;

        return errorType == that.errorType;
    }

    @Override
    public int hashCode() {
        return (int) errorType;
    }
}
