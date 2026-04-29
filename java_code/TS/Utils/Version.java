package it.tecnosystemi.TS.Utils;

public class Version {
    public int major;
    public int minor;
    public int release;

    Version(int i, int i2, int i3) {
        this.major = i;
        this.minor = i2;
        this.release = i3;
    }

    public static Version parseVersion(String str) {
        if (str == null || str.isEmpty() || str.equals("null")) {
            return null;
        }
        String[] split = str.split("\\.");
        int i = 0;
        int parseInt = split.length > 0 ? Integer.parseInt(split[0]) : 0;
        int parseInt2 = split.length > 1 ? Integer.parseInt(split[1]) : 0;
        if (split.length > 2) {
            i = Integer.parseInt(split[2]);
        }
        return new Version(parseInt, parseInt2, i);
    }

    public static int compareVersions(int i, int i2, int i3, int i4, int i5, int i6) {
        if (i != i4) {
            return Integer.compare(i, i4);
        }
        if (i2 != i5) {
            return Integer.compare(i2, i5);
        }
        return Integer.compare(i3, i6);
    }
}
