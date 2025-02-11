package info.kgeorgiy.ja.gerasimov.iterative;

import java.util.AbstractList;
import java.util.List;

/**
 * A {@link List} with a step between elements
 * @param <T> The type of item that is stored in the list
 */
public class MyList<T> extends AbstractList<T> {

    private final List<T> list;
    private final int step;

    /**
     * The constructor of this class
     *
     * @param list Source {@link List}
     * @param step The step between the elements
     */

    public MyList(List<T> list, int step) {
        this.list = list;
        this.step = step;
    }

    /**
     * The element {@link T} with the current step
     *
     * @param index index of the element to return
     * @return element {@link T}
     */
    @Override
    public T get(int index) {
        return list.get(index * step);
    }

    /**
     * Allows you to get the size of the {@link List}, which will depend on the step
     *
     * @return size of {@link MyList}
     */
    @Override
    public int size() {
        final int size = list.size() / step;
        if (list.size() % step == 0) {
            return size;
        }
        return size + 1;
    }
}
