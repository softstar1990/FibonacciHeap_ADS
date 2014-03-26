import java.util.*; // For ArrayList

public final class FHeap{

	/**
	 * Here is the class for the entry node
	 */
    public static class Entry{
        private int     mDegree = 0;       // Number of children
        private boolean mChildCut = false; // Whether this node is marked

        private Entry mNext;   // Next and previous elements in the list
        private Entry mPrev;

        private Entry mParent; // Parent in the tree, if any.
        private Entry mChild;  // Child node, if any.

        private int mKey;     // key for the entry node, in our project, it is the index of node in the graph
        private int mPriority; // Its priority, in our project it is the current shortest path to the node

        public int getValue() {
            return mKey;
        }

        public void setValue(int value) {
            mKey = value;
        }

        public int getPriority() {
            return mPriority;
        }

        public Entry(int elem, int priority) {
            mNext = mPrev = this; //when create new node, its next and prev is itself
            mKey = elem;
            mPriority = priority;
        }
    }

    /**
     * Here is fields for Fibonacci Heap
     * mMin point to the min element
     * mSize record number of node in the FHeap
     */
    private Entry mMin = null;
    private int mSize = 0;

    public boolean isEmpty() {
    	return mMin == null;
    }

    public Entry min() {
    	if (isEmpty())
    		throw new NoSuchElementException("Heap is empty.");
    	return mMin;
    }
       
    public int size() {
    	return mSize;
    }

    /**
     * Insert an element into the FHeap with specific key and priority
     * Note it create an Entry and return it to make sure we can access
     * the node from outside the FHeap
     */
    public Entry insert(int key, int priority) {   	
        Entry result = new Entry(key, priority);
        mMin = mergeLists(mMin, result); //merge the new entry node into the top level list
        ++mSize;
        return result;  //return reference to the new node
    }

    /**
     * When FHeap is not empty, return and delete the min entry in the FHeap
     */
    public Entry deleteMin() {
        if (isEmpty()){
            throw new NoSuchElementException("Heap is empty.");
        }
        
        --mSize;

        Entry minElem = mMin; //temp reference to min entry we will return later

        //remove mMin from the FHeap
        if (mMin.mNext == mMin) { 
        	// Case one, only one entry in top level list
            mMin = null;
        }
        else { 
        	// Case two, there are other entries in the list of roots, then remove the Min entry 
        	// from the list and arbitrary set the Min.
            mMin.mPrev.mNext = mMin.mNext;
            mMin.mNext.mPrev = mMin.mPrev;
            mMin = mMin.mNext; // Arbitrary element of the root list.
        }

        // Next, clear the parent fields of all of the min element's children,
        // since they're about to become roots.
        if (minElem.mChild != null) {
            Entry curr = minElem.mChild;
            do {
                curr.mParent = null;
                curr = curr.mNext;
            } while (curr != minElem.mChild);
        }

        // Next, splice the children of the root node into the top level list, 
        // then set mMin to point somewhere in that list.
        mMin = mergeLists(mMin, minElem.mChild);

        
        /**
         * Here is the pairwise combination
         */
        // If there are no entries left, then the min entry is the only entry in 
        // the FHeap, we do not need the pairwise combination
        if (mMin == null) return minElem;

        List<Entry> treeTable = new ArrayList<Entry>();  //used to record roots with different degree
        List<Entry> toVisit = new ArrayList<Entry>(); //used to record all the entry in the root list

        //Add all node in the top level list to the toVisit
        for (Entry curr = mMin; toVisit.isEmpty() || toVisit.get(0) != curr; curr = curr.mNext){
            toVisit.add(curr);
        }

        // Traverse this list and perform the appropriate union steps.
        for (Entry curr: toVisit) {
            while (true) {
                // Ensure that the list is long enough to hold an element of this degree.
                while (curr.mDegree >= treeTable.size()){
                    treeTable.add(null);
                }

                // if there is no tree with the same degree we visited before, just record this node
                if (treeTable.get(curr.mDegree) == null) {
                    treeTable.set(curr.mDegree, curr);
                    break; //note this is the only way we can break the while loop
                }

                // Otherwise, merge with what's there. 
                Entry other = treeTable.get(curr.mDegree);
                treeTable.set(curr.mDegree, null); // Clear the slot

                // Determine which of the two trees has the smaller root
                Entry min = (other.mPriority < curr.mPriority)? other : curr;
                Entry max = (other.mPriority < curr.mPriority)? curr  : other;

                // Break max out of the root list, then merge it into min's child list.
                max.mNext.mPrev = max.mPrev;
                max.mPrev.mNext = max.mNext;
                // Make max a singleton so that we can merge it.
                max.mNext = max.mPrev = max;
                min.mChild = mergeLists(min.mChild, max);
                
                // Re-parent max.
                max.mParent = min;
                // max can now lose another child. */
                max.mChildCut = false;
                // Increase min's degree
                ++min.mDegree;

                // notice the new tree may still have some other tree with the same degree
                // it is still in the while loop and check whether there will be another pairwise combination
                curr = min;
            }

            // Update the global min 
            if (curr.mPriority <= mMin.mPriority) mMin = curr;
        }
        return minElem;
    }

    /**
     * Decreases the key of the specified element to a new priority.
     * Please make sure the entry is in the FHeap, I do not check this. 
     */
    public void decreaseKey(Entry entry, int newPriority) {
    	
    	//if newPriority is > mPriority throw exception
        if (newPriority > entry.mPriority){
            throw new IllegalArgumentException("New priority exceeds old.");
        }

        entry.mPriority = newPriority;
        // if the node have parent and after decreaseKey its priority is < than its parent
        // we have to cut this node out and merge it to top level list 
        if (entry.mParent != null && entry.mPriority <= entry.mParent.mPriority){
            cutNode(entry);
        }

        if (entry.mPriority <= mMin.mPriority)
            mMin = entry;
    }
    
    /**
     * Deletes this Entry from the Fibonacci heap that contains it.
     * Please make sure the entry is in the FHeap, I do not check this. 
     */
    public void delete(Entry entry) {
    	// just decrease the specific entry's priority to -infinity and then deleteMin
        decreaseKey(entry, Integer.MIN_VALUE);
        deleteMin();
    }

    /**
     * Merge two FHeap to get a new FHeap
     */
    public static FHeap merge(FHeap one, FHeap two) {
        FHeap result = new FHeap();
        result.mMin = mergeLists(one.mMin, two.mMin);
        result.mSize = one.mSize + two.mSize;

        //clear old FHeap
        one.mSize = two.mSize = 0;
        one.mMin  = null;
        two.mMin  = null;

        return result;
    }
    
    /**
     * mergeLists merge two circularly double linked lists into one
     * need two min-pointer to those two lists, and return the min-pointer to the merged one
     */
    private static Entry mergeLists(Entry one, Entry two) {
        if (one == null && two == null) { // Both null, resulting list is null.
            return null;
        }
        else if (one != null && two == null) { // Two is null, result is one.
            return one;
        }
        else if (one == null && two != null) { // One is null, result is two.
            return two;
        }
        else { // Both non-null; actually do the splice.
            Entry oneNext = one.mNext;
            one.mNext = two.mNext;
            one.mNext.mPrev = one;
            two.mNext = oneNext;
            two.mNext.mPrev = two;

            return one.mPriority < two.mPriority? one : two;
        }
    }

    /**
     * Cuts a node from its parent.  If the parent was cut before, recursively
     * cuts that node from its parent as well.
     */
    private void cutNode(Entry entry) {
        entry.mChildCut = false;  //update ChildCut

        if (entry.mParent == null) return; //if the node have no parent, done

        //the node have parent, next if it has siblings, update them
        if (entry.mNext != entry) {
            entry.mNext.mPrev = entry.mPrev;
            entry.mPrev.mNext = entry.mNext;
        }

        // If the node is the one identified by its parent as its child,
        // we need to rewrite that pointer to point to some arbitrary other child.
        if (entry.mParent.mChild == entry) {
            // If there are any other children, pick one of them arbitrarily. 
            if (entry.mNext != entry) {
                entry.mParent.mChild = entry.mNext;
            }
            // Otherwise, there aren't any other children 
            else {
                entry.mParent.mChild = null;
            }
        }
        
        --entry.mParent.mDegree; //entry's parent lost a child T_T

        //merge entry into the top level list
        entry.mPrev = entry.mNext = entry;
        mMin = mergeLists(mMin, entry);

        //recursively check entry's parent, whether cut it or update mChildCut
        if (entry.mParent.mChildCut){
            cutNode(entry.mParent);
        }
        else{
            entry.mParent.mChildCut = true;
        }

        entry.mParent = null;  //update mParent
    }
}