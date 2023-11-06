package hu.krtn.brigad.test.hashing;

import hu.krtn.brigad.engine.extra.KF8BHash;

import java.util.HashMap;

public class HashTest {

    public static void main(String[] args) {
        // Check for hash collisions
        HashMap<String, Integer> map = new HashMap<>();

        int collisions = 0;

        // 2^24 = 16'777'216'
        for (int i = 0; i < Math.pow(2, 24); i++) {
            String id = KF8BHash.encode(String.valueOf(i));
            if (map.put(id, i) != null) {
                System.out.println("Hash collision: " + id);
                collisions++;
            }
        }

        System.out.println("Collisions: " + collisions);

        // Check timing of idHash
        for (int i = 0; i < 10; i++) {
            long start = System.currentTimeMillis();

            KF8BHash.encode("Example" + i);

            long end = System.currentTimeMillis();

            System.out.println("idHash (" + i + ") took " + (end - start) + " milliseconds.");
        }
    }

}
