package controller;

import javafx.collections.ObservableList;
import model.Fine;
import storage.DataStore;

public class FineController {

    private static FineController instance;
    private final DataStore store = DataStore.getInstance();

    private FineController() {}

    public static FineController getInstance() {
        if (instance == null) instance = new FineController();
        return instance;
    }

    public ObservableList<Fine> getAllFines() {
        return store.getFines();
    }

    public double calculatePreview(model.Transaction t) {
        return t.calculateFine();
    }
}