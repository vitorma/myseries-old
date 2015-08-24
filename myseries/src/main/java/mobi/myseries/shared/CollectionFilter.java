package mobi.myseries.shared;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


public class CollectionFilter<T> {

    private Specification<T> specification;

    public CollectionFilter(Specification<T> specification) {
        Validate.isNonNull(specification, "specification cannot be null.");
        this.specification = specification;
    }

    public Collection<T> in(Collection<T> collection) {
        List<T> filteredList = new LinkedList<T>();

        for (T candidate : collection) {
            if(this.specification.isSatisfiedBy(candidate)) {
                filteredList.add(candidate);
            }
        }

        return filteredList;
    }
}
