package lib;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class QueueSync<E> {
	final int MAX_SIZE = 10000000;
	int mWaitTime = 50000;
	LinkedList<E> mList;
	int mSize;
	
	public QueueSync() {
		initLimit(MAX_SIZE);
		initParams();
	}
	
	public QueueSync(int size) {
		initLimit(size);
		initParams();
	}

	private void initLimit(int size) {
		if (size <= 0) {
			mSize = MAX_SIZE;
		} else {
			mSize = size;
		}
	}

	private void initParams() {
		mList = new LinkedList<E>();
	}

	/* getter and setter */

	public LinkedList<E> getOriginList() {
		return mList;
	}

	/* actions */

	public synchronized void push(E msg) {
		while (size() == mSize) {
			try {
				wait();
			} catch (InterruptedException e) { e.printStackTrace(); }
		}
		mList.addLast(msg);
		if (size() > 0) { notify(); }
	}

	public synchronized E pop() {
		while (size() == 0) {
			try {
				wait(mWaitTime);
			} catch (InterruptedException e) { e.printStackTrace(); }
		}
		if (size() == mSize) { notify(); }
		E res = mList.removeFirst();
		return res;
	}

	public synchronized E peek() {
		return mList.peek();
	}
	
	public synchronized E get(int num) {
		return size() == 0 ? null : mList.get(num);
	}

	public synchronized void addAll(List<E> list) {
		mList.addAll(list);
	}

	public synchronized E remove(int num) {
		return mList.remove(num);
	}

	public synchronized boolean isEmpty() {
		return mList.size() == 0;
	}

	public int size() {
		return mList.size();
	}

	public synchronized Iterator<E> iterator() {
		return mList.iterator();
	}
}
