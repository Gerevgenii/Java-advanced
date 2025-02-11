package info.kgeorgiy.ja.gerasimov.arrayset;

import java.util.*;

public class ArraySet<T> extends AbstractSet<T> implements SortedSet<T> {
    private final List<T> array;
    private final Comparator<? super T> comparator;

    public ArraySet() {
        this(Collections.emptyList(), null);
    }

    public ArraySet(Collection<? extends T> array) {
        this(new ArrayList<>(array), null);
    }

    public ArraySet(Collection<? extends T> array, Comparator<? super T> comparator) {
        this(new ArrayList<>(array), comparator);
    }

    private ArraySet(List<T> array, Comparator<? super T> comparator) {
        final Set<T> arrayNew = new TreeSet<>(comparator);
        arrayNew.addAll(array);
        this.array = new ArrayList<>(arrayNew);
        this.comparator = comparator;
    }

    private int firstOccurrenceOf(T element) {
        final int binarySearch = Collections.binarySearch(array, element, comparator);
        return binarySearch < 0 ? -binarySearch - 1 : binarySearch;
    }

    @Override
    public Iterator<T> iterator() {
        return array.iterator();
    }

    @Override
    public int size() {
        return array.size();
    }

    @Override
    public Comparator<? super T> comparator() {
        return comparator;
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        if (comparator != null && comparator.compare(fromElement, toElement) > 0)
            throw new IllegalArgumentException("You can't get reversed subet");
        final List<T> arr = new ArrayList<>();
        arr.add(fromElement);
        arr.add(toElement);
        arr.sort(comparator);
        if (arr.getFirst() != fromElement)
            throw new IllegalArgumentException("You can't get reversed subet");
        return new ArraySet<>(
                array.subList(
                        firstOccurrenceOf(fromElement),
                        firstOccurrenceOf(toElement)
                ),
                comparator
        );
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        return new ArraySet<>(array.subList(0, firstOccurrenceOf(toElement)), comparator);
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        return  new ArraySet<>(array.subList(firstOccurrenceOf(fromElement), size()), comparator);

    }

    @Override
    public T first() {
        return array.getFirst();
    }

    @Override
    public T last() {
        return array.getLast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object o) {
        final T a;
        try {
            //noinspection unchecked
            a = (T) o;
        } catch (ClassCastException e) {
            return false;
        }
        return Collections.binarySearch(array, a, comparator) > -1;
    }
}
