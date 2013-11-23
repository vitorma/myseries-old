package mobi.myseries.shared;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Iterables {

    public static int[] toIntArray(Collection<String> integerStrings) {
        Validate.isNonNull(integerStrings, "integerStrings");

        int[] ints = new int[integerStrings.size()];

        int i = 0;
        for (String s : integerStrings) {
            ints[i] = Integer.parseInt(s); //Throws NumberFormatException
            i++;
        }

        return ints;
    }

    public static Set<String> toStringSet(int[] ints) {
        Validate.isNonNull(ints, "ints");

        Set<String> stringSet = new HashSet<String>(ints.length);

        for(int i : ints) {
            stringSet.add(String.valueOf(i));
        }

        return stringSet;
    }
}
