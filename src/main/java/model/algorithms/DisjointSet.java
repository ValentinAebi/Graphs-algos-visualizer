package model.algorithms;

import java.util.*;

final class DisjointSetRepresentation<T> {
    private final Map<T, Item> itemFromElem = new HashMap<>();
    private final Set<DisjointSet> sets = new HashSet<>();

    public void makeSet(T elem) {
        DisjointSet disjointSet = new DisjointSet(elem);
        itemFromElem.put(elem, disjointSet.head);
        sets.add(disjointSet);
    }

    public T findSet(T elem) {
        return itemFromElem.get(elem).set.head.elem;
    }

    public DisjointSet union(T elem1, T elem2) {
        DisjointSet set1 = itemFromElem.get(elem1).set, set2 = itemFromElem.get(elem2).set;
        Item head2 = set2.head;
        set1.tail.next = head2;
        Item curr = head2;
        curr.set = set1;
        while (curr.next != null) {
            curr = curr.next;
            curr.set = set1;
        }
        set1.tail = curr;
        sets.remove(set2);
        return set1;
    }

    public void print(){
        for (DisjointSet set : sets) {
            System.out.print(set+", ");
        }
        System.out.println();
    }

    private final class DisjointSet {
        private Item head, tail;

        private DisjointSet(T elem) {
            head = tail = new Item(elem, this, null);
        }

        @Override
        public String toString(){
            StringJoiner joiner = new StringJoiner(", ", "[", "]");
            Item curr = head;
            joiner.add(curr.elem.toString());
            while (curr.next != null){
                curr = curr.next;
                joiner.add(curr.elem.toString());
            }
            return joiner.toString();
        }

    }

    private final class Item {
        private final T elem;
        private DisjointSet set;
        private Item next;

        private Item(T elem, DisjointSet set, Item next) {
            this.elem = elem;
            this.set = set;
            this.next = next;
        }

    }
}