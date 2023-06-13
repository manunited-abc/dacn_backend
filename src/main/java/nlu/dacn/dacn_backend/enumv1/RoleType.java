package nlu.dacn.dacn_backend.enumv1;

public enum RoleType {
    ADMIN("ADMIN", "Quản trị viên"),
    USER("USER", "Người dùng");

    private String code;
    private String nameCode;

    RoleType(String code, String nameCode) {
        this.code = code;
        this.nameCode = nameCode;
    }

    public String getCode() {
        return this.code;
    }

    public String getNameCode() {
        return this.nameCode;
    }
}
