package io.github.SkywolfDragon.enchWorkers;

public class EnchResult {
    final boolean success;
    final String message;

    public EnchResult(boolean s, String m) {
        this.success = s;
        this.message = m;
    }

    public boolean wasSuccessful() {
        return this.success;
    }
    public String getMessage() {
        return this.message;
    }
}
