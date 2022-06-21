import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BinaryConverterTest {

    @Test
    void test() {
        int[] tc = new int[] {
            -1, 7, 16
        };
        String[] res = new String[] {
            "1111", "0111", "0111"
        };
        for (int i = 0; i < tc.length; i++) {
            String r = BinaryConverter.convert(tc[i], 4);
            assertEquals(res[i], r);
        }
    }
}