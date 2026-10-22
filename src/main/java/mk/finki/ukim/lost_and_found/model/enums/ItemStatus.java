package mk.finki.ukim.lost_and_found.model.enums;

public enum ItemStatus {
    UNRESOLVED("Unresolved"),
    RESOLVED("Resolved"),
    PERMANENTLY_LOST("Permanently lost");

    private final String displayName;

    ItemStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}