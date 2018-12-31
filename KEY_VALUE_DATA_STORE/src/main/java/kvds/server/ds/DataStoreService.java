package kvds.server.ds;

import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class DataStoreService { 
	
	private static final int MAX_QUEUE_SIZE = 3;
	
	private static final ReentrantReadWriteLock rrwl = new ReentrantReadWriteLock();

	private DataStoreDao dao;
	private Map<String, Deque<String>> keyRefer; // key = file name
	private Deque<Deque<String>> queuedValues;
	
	
	public DataStoreService() {
		dao = new DataStoreDao();
		keyRefer = new ConcurrentHashMap<>(); 
		queuedValues = new ConcurrentLinkedDeque<>();
	}

	public void set(String key, Deque<String> values) throws Exception {
		rrwl.writeLock().lock();
		try {
			// if values in queue - remove to later add them back to the end of the queue
			Optional.ofNullable(keyRefer.get(key)).ifPresent(queuedValues::remove);
			evictFifoIfQueueReachedLimit();
			if (!values.peekFirst().equals(key))
				values.addFirst(key); // for more efficient removal later
			queuedValues.addLast(values);
			keyRefer.put(key, queuedValues.peekLast());
		} finally {
			rrwl.writeLock().unlock();
		}
	}

	public Deque<String> get(String key) throws Exception {
		rrwl.readLock().lock();
		Deque<String> values;
		try {
			values = keyRefer.get(key);
			if (null == values) {
				values = readFromDisk(key);
			}
		} finally {
			rrwl.readLock().unlock();
		}
		if (!values.isEmpty())
			set(key, values);
		return values;
	}
	
	public Set<String> getAllKeys(String pattern) throws Exception {
		rrwl.readLock().lock();
		try {
			Set<String> keys = keyRefer.keySet(); // collect from cache
			Set<String> copiedKeys = new HashSet<>(); // needs a copy because the set view does not support the add operation
			if (null != keys) 
				copiedKeys.addAll(keys);
			dao.readFilesFromDisk().forEach(path -> copiedKeys.add(path.getFileName().toString())); // add keys from disk
			return copiedKeys.stream().filter(key -> key.contains(pattern)).collect(Collectors.toSet()); // filter by pattern
		} finally {
			rrwl.readLock().unlock();
		}
	}

	public void rightAdd(String key, String value) throws Exception {
		add(key, value, true);
	}

	public void leftAdd(String key, String value) throws Exception {
		add(key, value, false);
	}

	private void add(String key, String value, boolean isRight) throws Exception {
		Deque<String> values;
		rrwl.writeLock().lock();;
		try {
			values = search(key);
			if (isRight)
				values.addLast(value);
			else {
				if (!values.isEmpty())
					values.removeFirst(); // remove file name
				values.addFirst(value);
			}
		} finally {
			rrwl.writeLock().unlock();
		}
		set(key, values);
	}
	
	private Deque<String> search(String key) throws Exception {
		// search in cache, if not found search in disk
		Deque<String> values = keyRefer.get(key);
		if(null == values)
			values = readFromDisk(key);
		return values;
	}
	
	private void evictFifoIfQueueReachedLimit() throws Exception {
		if (queuedValues.size() == MAX_QUEUE_SIZE)  {
			Deque<String> first = queuedValues.removeFirst(); // remove from queuedValues
			keyRefer.remove(first.peekFirst()); // remove from keyReferer, key (=fileName) is the first value so its O(1)
			writeToDisc(first.peekFirst(), first);
		}
	}
	
	private Deque<String> readFromDisk(String key) throws Exception {
		return this.dao.readFromDisk(key);
	}

	private void writeToDisc(String key, Deque<String> first) throws Exception {
		dao.writeToDisc(key, first);
	}

}
