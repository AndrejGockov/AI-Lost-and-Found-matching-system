package mk.finki.ukim.lost_and_found.model.enums;

public enum AppRole {
    PROFESSOR, ADMIN, GUEST;


    public String roleName() {
        return "ROLE_" + this.name();
    }
}

