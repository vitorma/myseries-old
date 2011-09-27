package br.edu.ufcg.aweseries.thetvdb.util;

import java.util.Arrays;

public abstract class Strings {

    public static String normalizePipedString(String pipedString) {
        if (pipedString == null) {
            throw new IllegalArgumentException(
                    "String to normalize shouldn't be null!");
        }

        return Arrays.toString(pipedString.split("\\|"))
                     .replace("[, ", "")
                     .replace("]", "");
    }
}
