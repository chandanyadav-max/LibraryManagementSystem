package controller;

import model.Librarian;
import storage.DataStore;

public class AuthController {

    private static AuthController instance;
    private final DataStore store = DataStore.getInstance();

    private AuthController() {}

    public static AuthController getInstance() {
        if (instance == null) instance = new AuthController();
        return instance;
    }

    /**
     * Returns the Librarian if credentials match, null otherwise.
     */
    public Librarian validateLogin(String username, String password) {
        return store.getLibrarians().stream()
                .filter(l -> l.getEmail().equals(username) && l.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }
}