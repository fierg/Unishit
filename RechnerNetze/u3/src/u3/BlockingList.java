package u3;

import java.util.LinkedList;
import java.util.List;

public class BlockingList<T> {

    private List<T> list = new LinkedList<T>();
    private int capacity;

    public BlockingList(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void put(T element) throws InterruptedException {
        while(list.size() == capacity) {
            wait();
        }

        list.add(element);
        notifyAll();
    }

    public synchronized T get(int index) throws InterruptedException {
        while(list.isEmpty()) {
            wait();
        }

        T item = list.get(index);
        notifyAll();
        return item;
        
        
    }
    public synchronized boolean contains(T element){
    	return list.contains(element);
    }
    public synchronized void removeItem(Object o){
    	list.remove(o);
    }
}