package Security;

public enum SaID
{
    SYSTEM(0),
    HIGH(1),
    NORMAL(2),
    LOW(3),
    UNTRUSTED(4);

    private int value;
    SaID(int _value)
    {
        this.value=_value;
    }

    public static SaID fromInt(int i) {
        for (SaID b : SaID.values()) {
            if (b.value == i) {
                return b;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }
}
