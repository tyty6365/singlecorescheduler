package singlecorescheduler;

import java.util.*;

/**
 * This class models a priority min-queue that uses an array-list-based min
 * binary heap that implements the PQueueAPI interface. The array holds objects
 * that implement the parameterized Comparable interface.r
 *
 * @author Duncan, Tyler Scott
 * @param <E> the priority queue element type.
 * @author William Duncan  <pre>
 * Date: 09/21/2022
 * Course: csc 3102
 * Programming Project # 1
 * Instructor: Dr. Duncan
 * </pre>
 */
public class PQueue<E extends Comparable<E>> implements PQueueAPI<E> {

    /**
     * A complete tree stored in an array list representing the binary heap
     */
    private ArrayList<E> tree;
    /**
     * A comparator lambda function that compares two elements of this heap when
     * rebuilding it; cmp.compare(x,y) gives 1. negative when x less than y 2.
     * positive when x greater than y 3. 0 when x equal y
     */
    private Comparator<? super E> cmp;

    public PQueue() {
        tree = new ArrayList<>();
        cmp = (object1, object2) -> object1.compareTo(object2);
    }

    /**
     * A parameterized constructor that uses an externally defined comparator
     *
     * @param fn - a trichotomous integer value comparator function
     */
    public PQueue(Comparator<? super E> fn) {
        tree = new ArrayList<>();
        cmp = fn;
    }

    @Override
    public boolean isEmpty() //
    {
        return tree.isEmpty();
    }

    @Override
    public void insert(E obj) {
        tree.add(obj);

        int place = size() - 1;
        int parent = (place - 1) / 2;

        while (parent >= 0 && cmp.compare(tree.get(place), tree.get(parent)) < 0) { //min heap
            swap(place, parent);
            place = parent;
            parent = (place - 1) / 2;
        }
    }

    @Override
    public E remove() throws PQueueException {
        if (tree.isEmpty()) {
            throw new PQueueException("queue is empty");
        }
        E obj = peek();
        tree.set(0, tree.get(size() - 1));
        tree.remove(size() - 1);
        rebuild(0, size());
        return obj;
    }

    @Override
    public E peek() throws PQueueException {
        if (tree.isEmpty()) {
            throw new PQueueException("queue is empty");
        }
        return tree.get(0);
    }

    @Override
    public int size() {
        return tree.size();
    }

    /**
     * Swaps a parent and child elements of this heap at the specified indices
     *
     * @param place an index of the child element on this heap
     * @param parent an index of the parent element on this heap
     */
    private void swap(int place, int parent) {
        E temp = tree.get(parent);
        tree.set(parent, tree.get(place));
        tree.set(place, temp);
    }

    /**
     * Rebuilds the heap to ensure that the heap property of the tree is
     * preserved.
     *
     * @param root the root index of the subtree to be rebuilt
     * @param eSize the size of this tree
     */
    private void rebuild(int root, int eSize) {
        if (root < eSize / 2) {
            int child = 2 * root + 1;

            if (child + 1 < size()) {
                if (cmp.compare(tree.get(child + 1), tree.get(child)) < 0) {
                    child++;
                }
            }

            if (cmp.compare(tree.get(root), tree.get(child)) > 0) {
                swap(root, child);
                rebuild(child, size());
            }
        }
    }
}
