package Code;

import Interface.ISortedTreeMap;

import java.util.Stack;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.stream.StreamSupport;

public class SortedTreeMap<K extends Comparable<? super K>, V> implements ISortedTreeMap<K, V>{
    private int numberOfNodes;
    private BinaryNode root;
    private Comparator comparator;

    public SortedTreeMap(Comparator comp){
        comparator = comp;
    }
    /**
     * Finds the minimum entry (by key) in the map, if no entry is found, returns
     * null instead.
     *
     * @return minimum entry
     */
    @Override
    public Entry<K, V> min() {
        Iterator<Entry<K, V>> iterator = getInorderIterator();
        if (iterator.hasNext())
            return iterator.next();
        else
            return null;
    }

    /**
     * Finds the maximum entry (by key) in the map, if no key is found returns
     * null instead.
     *
     * @return maximum value
     */
    @Override
    public Entry<K, V> max() {
        Iterator<Entry<K, V>> iterator = getInorderIterator();
        while (iterator.hasNext()){
            Entry<K, V> entry = iterator.next();
            if (!iterator.hasNext()){
                return entry;
            }
        }
        return null;
    }

    /**
     * Inserts the specified value with the specified key as a new entry into the map.
     * If a value is already present for that key, return the previous value, else null.
     *
     * @param key   The key to be inserted
     * @param value The value to be inserted
     * @return Previous value
     */
    @Override
    public V add(K key, V value) {
        return add(new Entry<>(key, value));
    }

    /**
     * Inserts the specified entry into the map. If the key is already a part of the map,
     * return the previous value, else null.
     *
     * @param entry The new entry to be inserted into the map
     * @return Previous value
     */
    @Override
    public V add(Entry<K, V> entry) {
        Entry<K, V> result = null;

        if (isEmpty())
            root = new BinaryNode(entry);
        else if (entry.key == null || entry.value == null)
            return null;
        else
            result = addEntry(root, entry);

        if (result == null){
            numberOfNodes++;
            return null;
        }
        return result.value;
    }

    private Entry<K, V> addEntry(BinaryNode rootNode, Entry<K, V > entry){
        Entry<K, V> result = null;
        int comparison = comparator.compare(entry.key, rootNode.data.key);

        if (comparison == 0){
            result = rootNode.getData();
            rootNode.setData(entry);
        }
        else if (comparison < 0)
            if (rootNode.hasLeftChild())
                result = addEntry(rootNode.getLeftChild(), entry);
            else
                rootNode.setLeftChild(new BinaryNode(entry));
        else
            if (rootNode.hasRightChild())
                result = addEntry(rootNode.getRightChild(), entry);
            else
                rootNode.setRightChild(new BinaryNode(entry));

        return result;
    }

    /**
     * Replaces the value for key in the map as long as it is already present. If they key
     * is not present, the method throws an exception.
     *
     * @param key   The key for which the value is replaced
     * @param value The new value
     * @throws NoSuchElementException When key is not in map
     */
    @Override
    public void replace(K key, V value) throws NoSuchElementException {
        if (containsKey(key)){
            add(key ,value);
        } else {
            throw new NoSuchElementException();
        }
    }

    /**
     * Applies a function to the value at key and replaces that value. Throws an exception
     * if the key is not present in the map.
     *
     * @param key The key for which we are replacing the value
     * @param f   The function to apply to the value
     * @throws NoSuchElementException When key is not in map
     */
    @Override
    public void replace(K key, BiFunction<K, V, V> f) throws NoSuchElementException {
        Entry<K, V> foundEntry = getByKey(root, key);
        if (foundEntry == null){
            throw new NoSuchElementException();
        }
        Entry<K, V> replaceEntry = new Entry<>(foundEntry.key, f.apply(foundEntry.key, foundEntry.value));
        add(replaceEntry);
    }

    /**
     * Removes the entry for key in the map. Throws an exception if the key is not present
     * in the map.
     *
     * @param key The key for the entry to remove
     * @return The removed value
     * @throws NoSuchElementException When key is not in map.
     */
    @Override
    public V remove(Object key) throws NoSuchElementException {
        ReturnObject oldEntry = new ReturnObject(null);
        BinaryNode newRoot = removeEntry(root, (K) key, oldEntry);
        if (!root.equals(newRoot)){
            root = newRoot;
        }

        if (oldEntry.get() == null){
            throw new NoSuchElementException();
        }
        numberOfNodes--;
        return oldEntry.get().value;
    }

    private BinaryNode removeEntry(BinaryNode rootNode, K key, ReturnObject oldEntry){
        if (rootNode != null){
            Entry<K, V> rootData = rootNode.getData();
            int comparison = comparator.compare(key, rootData.key);

            if (comparison == 0){
                oldEntry.set(rootData);
                rootNode = removeFromRoot(rootNode);
            }
            else if (comparison < 0 ){
                BinaryNode leftChild = rootNode.getLeftChild();
                BinaryNode subtreeRoot = removeEntry(leftChild, key, oldEntry);
                rootNode.setLeftChild(subtreeRoot);
            }
            else {
                BinaryNode rightChild = rootNode.getRightChild();
                rootNode.setRightChild(removeEntry(rightChild, key, oldEntry));
            }
        }
        return rootNode;
    }

    private BinaryNode removeFromRoot(BinaryNode rootNode){
        if (rootNode.hasLeftChild() && rootNode.hasRightChild()){
            BinaryNode leftSubtreeRoot = rootNode.getLeftChild();
            BinaryNode largestNode = findLargest(leftSubtreeRoot);

            rootNode.setData(largestNode.getData());
            rootNode.setLeftChild(removeLargest(leftSubtreeRoot));
        }
        else if(rootNode.hasRightChild())
            rootNode = rootNode.getRightChild();
        else
            rootNode = rootNode.getLeftChild();

        return rootNode;
    }

    private BinaryNode removeLargest(BinaryNode rootNode){
        if (rootNode.hasRightChild()){
            BinaryNode rightChild = rootNode.getRightChild();
            rightChild = removeLargest(rightChild);
            rootNode.setRightChild(rightChild);
        } else {
            rootNode = rootNode.getLeftChild();
        }
        return rootNode;
    }

    private BinaryNode findLargest(BinaryNode rootNode){
        if (rootNode.hasRightChild()){
            rootNode = findLargest(rootNode.getRightChild());
        }
        return rootNode;
    }

    /**
     * Retrieves the value for the key in the map.
     *
     * @param key The key for the value to retrieve
     * @return The value for the key
     * @throws NoSuchElementException When key is not in map
     */
    @Override
    public V getValue(Object key) throws NoSuchElementException {
        Entry<K, V> newEntry = getByKey(root, (K) key);

        if (newEntry == null){
            throw new NoSuchElementException();
        }
        return newEntry.value;
    }

    public Entry<K, V> getEntry(K key) throws  NoSuchElementException{
        Entry<K, V> newEntry = getByKey(root, (K) key);

        if (newEntry == null){
            throw new NoSuchElementException();
        }
        return newEntry;
    }

    /**
     * Checks if a key is in the map.
     *
     * @param key The key to check
     * @return true if the key is in the map, false otherwise
     */
    @Override
    public boolean containsKey(K key) {
        if (!isEmpty())
            return getByKey(root, key) != null;
        else
            return false;
    }

    private Entry<K, V> getByKey(BinaryNode rootNode, K key){
        if (rootNode == null){
            return null;
        }

        Entry<K, V> result = null;
        Entry<K, V> rootEntry = rootNode.data;


        int comparison = comparator.compare(key, rootEntry.key);


        if (comparison == 0)
            result = rootEntry;
        else if (comparison < 0)
            result = getByKey(rootNode.getLeftChild(), key);
        else
            result = getByKey(rootNode.getRightChild(), key);

        return result;
    }

    /**
     * Checks if a value is in the map
     *
     * @param value the value to look for
     * @return True if the value is present, false otherwise
     */
    @Override
    public boolean containsValue(V value) {
        return getByValue(root, value) != null;
    }

    private Entry<K, V> getByValue(BinaryNode rootNode, V value){

        if (rootNode == null){
            return null;
        }

        Entry<K, V> result = null;
        Entry<K, V> rootEntry = rootNode.data;

        int comparison = comparator.compare(value, rootEntry.value);

        if (comparison == 0)
            result = rootEntry;
        else if (comparison < 0)
            result = getByValue(rootNode.getLeftChild(), value);
        else
            result = getByValue(rootNode.getRightChild(), value);

        return result;
    }

    /**
     * Finds all the keys in the map and returns them in order.
     *
     * @return keys in order
     */
    @Override
    public Iterable<K> keys() {
        return () -> StreamSupport.stream(entries().spliterator(), false).map(entry -> entry.key).iterator();
    }

    /**
     * Finds the values in order of the keys.
     *
     * @return values in order of the keys
     */
    @Override
    public Iterable<V> values() {
        return () -> StreamSupport
                .stream(entries().spliterator(), false)
                .map(entry -> entry.value)
                .iterator();
    }

    /**
     * Finds all entries in the map in order of the keys.
     *
     * @return All entries in order of the keys
     */
    @Override
    public Iterable<Entry<K, V>> entries() {
        return this::getInorderIterator;
    }

    /**
     * Finds the entry for the key, if the key is not in the map returns the next
     * highest entry if such an entry exists
     *
     * @param key The key to find
     * @return The entry for the key or the next highest
     */
    @Override
    public Entry<K, V> higherOrEqualEntry(K key) {
        return higherOrEqualEntry(root, key);
    }

    private Entry<K, V> higherOrEqualEntry(BinaryNode rootNode, K key){

        if (rootNode == null){
            return null;
        }

        Entry<K, V> result = null;
        Entry<K, V> rootEntry = rootNode.data;

        int comparison = comparator.compare(key, rootEntry.key);

        if (comparison == 0)
            result = rootEntry;
        else if (comparison > 0)
            result = higherOrEqualEntry(rootNode.getRightChild(), key);
        else
            return rootNode.getLeftChild().getData();

        return result;
    }

    /**
     * Finds the entry for the key, if the key is not in the map, returns the next
     * lower entry if such an entry exists
     *
     * @param key The key to find
     * @return The entry for the key or the next lower
     */
    @Override
    public Entry<K, V> lowerOrEqualEntry(K key) {
    return null;
    }


    /**
     * Adds all entries in the other map into the current map. If a key is present
     * in both maps, the key in the other map takes precedent.
     *
     * @param other The map to add to the current map.
     */
    @Override
    public void merge(ISortedTreeMap<K, V> other) {
        for (Entry<K, V> entry : other.entries()){
            add(entry);
        }
    }

    /**
     * Removes any entry for which the predicate holds true. The predicate can
     * trigger on both the key and value of each entry.
     *
     * @param p The predicate that tests which entries should be kept.
     */
    @Override
    public void removeIf(BiPredicate<K, V> p) {
        Iterator<Entry<K, V>> iterator = getInorderIterator();

        while (iterator.hasNext()){
            Entry<K, V> found = iterator.next();
            if (p.test(found.key, found.value)){
                remove(found.key);
            }
        }
    }

    /**
     * Checks if the map is empty
     *
     * @return True if the map is empty, false otherwise.
     */
    @Override
    public boolean isEmpty() {
        return numberOfNodes == 0 && root == null;
    }

    /**
     * Returns the number of entries in the map
     *
     * @return Number of entries
     */
    @Override
    public int size() {
        return numberOfNodes;
    }

    /**
     * Clears the map of entries.
     */
    @Override
    public void clear() {
        root = null;
        numberOfNodes = 0;
    }

    public int getHeight(){
        return root.getHeight();
    }

    public BinaryNode getRootNode(){
        return root;
    }

    public Entry<K, V> getRootData(){
        return root.getData();
    }

    public Iterator<Entry<K, V>> getInorderIterator(){
        return new InorderIterator();
    }

    private class InorderIterator implements Iterator<Entry<K, V>> {
        private Stack<BinaryNode> nodeStack;
        private BinaryNode currentNode;

        public InorderIterator(){
            nodeStack = new Stack<>();
            currentNode = root;
        }

        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            return !nodeStack.isEmpty() || currentNode != null;
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public Entry<K, V> next() {
            BinaryNode nextNode = null;

            while (currentNode != null){
                nodeStack.push(currentNode);
                currentNode = currentNode.getLeftChild();
            }
            if (!nodeStack.isEmpty()){
                nextNode = nodeStack.pop();
                assert nextNode != null;
                currentNode = nextNode.getRightChild();
            }
            else
                throw new NoSuchElementException();

            return nextNode.getData();
        }

        @Override
        public void remove(){
            throw new UnsupportedOperationException();
        }

    }

    private class ReturnObject{
        Entry<K, V> data;

        public ReturnObject(Entry<K, V> dataInput){
            data = dataInput;
        }

        public Entry<K, V> get() {
            return data;
        }

        public void set(Entry<K, V> data) {
            this.data = data;
        }
    }

    private class BinaryNode{
        Entry<K, V> data;
        BinaryNode leftChild;
        BinaryNode rightChild;

        public BinaryNode(Entry<K, V> entry){
            this(entry, null, null);
        }

        public BinaryNode(Entry<K, V> entry, BinaryNode left, BinaryNode right){
            data = entry;
            leftChild = left;
            rightChild = right;
        }

        public Entry<K, V> getData() {
            return data;
        }

        public void setData(Entry<K, V> root) {
            this.data = root;
        }

        public BinaryNode getLeftChild() {
            return leftChild;
        }

        public boolean hasLeftChild(){
            return leftChild != null;
        }

        public void setLeftChild(BinaryNode leftChild) {
            this.leftChild = leftChild;
        }

        public BinaryNode getRightChild() {
            return rightChild;
        }

        public boolean hasRightChild(){
            return rightChild != null;
        }

        public void setRightChild(BinaryNode rightChild) {
            this.rightChild = rightChild;
        }

        public boolean isLeaf(){
            return (leftChild == null) && (rightChild == null);
        }

        public int getHeight(){
            return getHeight(this);
        }

        private int getHeight(BinaryNode node){
            int height = 0;

            if (node != null){
                height = 1 + Math.max(getHeight(node.getLeftChild()),
                                      getHeight(node.getRightChild()));
            }
            return height;
        }
    }
}
