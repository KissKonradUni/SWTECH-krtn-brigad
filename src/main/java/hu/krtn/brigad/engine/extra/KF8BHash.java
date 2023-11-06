package hu.krtn.brigad.engine.extra;

/**
 * Konrad's Fast 8 Byte Hash
 * Not that prone to collisions, and is reasonably fast.
 */
public class KF8BHash {

    public static String encode(String input) {
        if (input.length() < 8)
            input = String.format("%8s", input).replace(' ', '0');

        int sum = 0;

        byte[] bytes = input.getBytes();
        for (byte b : bytes) {
            sum += b;
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            int mod = (((sum + bytes[i] + i) % 16384) + (8 - i) * (4 - i)) % 62;
            if (mod < 10) {
                result.append((char) (mod + 48));
            } else if (mod < 36) {
                result.append((char) (mod + 55));
            } else {
                result.append((char) (mod + 61));
            }
        }

        return result.toString();
    }

}
