public class BinaryConverter {

    static boolean isOverFlow(int val, int size) {
        int max = (1 << (size - 1)) - 1;
        int min = -(max + 1);
        return val < min || val > max;
    }

    static String convert(int val, int size) {
        StringBuilder sb = new StringBuilder();
        int max = (1 << (size - 1)) - 1;
        int min = -(max + 1);
        if (val < min) {
            val = min;
        }
        if (val > max) {
            val = max;
        }
        for (int i = 0; i < size; i++) {
            if (((1<<i) & val) == 0) {
                sb.append(0);
            } else {
                sb.append(1);
            }
        }
        return sb.reverse().toString();
    }
}
