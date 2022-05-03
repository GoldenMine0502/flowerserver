package kr.goldenmine.flowerserver;

public class PrintUtil {

    public static String toStringArray(Object[] array) {
        return toStringArray(array, ", ");
    }

    public static String toStringArray(Object[] array, String separator) {
        StringBuilder sb = new StringBuilder();

        if(array.length > 0 ) sb.append(array[0]);

        for(int i = 1; i < array.length; i++) {
            sb.append(separator);
            sb.append(array[i]);
        }

        return sb.toString();
    }
}
