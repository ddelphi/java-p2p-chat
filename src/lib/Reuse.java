package lib;

import java.util.ArrayDeque;

public class Reuse<E> {
	public int mCountGet = 0;
	public int mCountGetTimes = 50;
	public int mRecycleTimes = 100;

	public ArrayDeque<E> mObjects;
	public ArrayDeque<Integer> mTimes;
	private CreationFactory<E> mCreationFactory;



	public Reuse() {
		mObjects = new ArrayDeque<E>();
		mTimes = new ArrayDeque<Integer>();
	}

	/* getter and setter */
	
	public void setCreationFactory(CreationFactory<E> cf) {
		mCreationFactory = cf;
	}

	/* actions */

	public synchronized E get() {
		checkListMayFillOne();
		countGetToLru();

		if (mObjects.size() == 0) {
			return null;
		} else {
			mTimes.pop();
			return mObjects.pop();
		}
	}

	public synchronized void put(E obj) {
		mTimes.push(mCountGet);
		mObjects.push(obj);
	}

	public int size() {
		return mObjects.size();
	}

	/* helpers */

	private void checkListMayFillOne() {
		if (mObjects.size() == 0 && mCreationFactory != null) {
			put(mCreationFactory.create());
		}
	}

	public void countGetToLru() {
		mCountGet += 1;
		if (mCountGet % mCountGetTimes == 0) {
			lru();
		}
	}

	private void lru() {
		for (int i = mObjects.size(); i > 0; i--) {
			System.out.println("lru:" + mCountGet + "|" + mTimes.peekLast() + "|" + mRecycleTimes);
			if (mCountGet - mTimes.peekLast() > mRecycleTimes) {
				mTimes.removeLast();
				mObjects.removeLast();
			} else {
				break;
			}
		}
	}
	
	
	/* class */
	
	static public interface CreationFactory<E> {
		public E create();
	}
}

