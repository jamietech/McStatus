package tk.nekotech.mcstatus;

public enum Status {
    UP, DOWN, UNKNOWN;
    public static Status fromStatusCode(final int status) {
        switch (status) {
            case 200:
                return Status.UP;
            case 503:
                return Status.DOWN;
            default:
                return Status.UNKNOWN;
        }
    }
}
