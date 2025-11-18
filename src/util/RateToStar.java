package util;
import manager.ReviewManager;
import java.lang.StringBuilder;

public class RateToStar {
    private static String empty = "☆";
    private static String filled = "★";
    private static int star_count = 5;

    public static String stringify(int rate){
        StringBuilder builder = new StringBuilder();
        int offset = ReviewManager.RATE_RANGE[0];
        int range = ReviewManager.RATE_RANGE[1] - offset + 1;
        int count = ((Long)Math.round((rate - offset + 1) / (double)range * star_count)).intValue(); // ex) round(5/10*5) = 3
        for(int i = 0; i < star_count; i++)
            builder.append(i < count? filled: empty);
        return builder.toString();
    }
}
