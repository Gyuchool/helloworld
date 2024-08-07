package com.example.lock.RFencedLock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.redisson.api.RFencedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RFencedLockTest {

	@Autowired
	private RedissonClient redissonClient;

	@Test
	public void testFencedLock() throws InterruptedException {
		String lockName = "testLock";

		RFencedLock lock = redissonClient.getFencedLock(lockName);
		// traditional lock method
		// Long token = lock.lockAndGetToken();

		// or acquire lock and automatically unlock it after 10 seconds
		Long token = lock.lockAndGetToken(10, TimeUnit.SECONDS);

		// or wait for lock aquisition up to 100 seconds
		// and automatically unlock it after 10 seconds
		// Long token = lock.tryLockAndGetToken(100, 10, TimeUnit.SECONDS);
		if (token != null) {
			try {
				// check if token >= old token

			} finally {
				lock.unlock();
			}
		}

		// AtomicBoolean to check if the lock is held by another thread
		AtomicBoolean lockHeldByAnotherThread = new AtomicBoolean(false);

		ExecutorService executor = Executors.newFixedThreadPool(2);

		// Thread 1: Acquire the lock and hold it for a while
		executor.submit(() -> {
			try {
				if (lock.tryLock(500, 5000, TimeUnit.MILLISECONDS)) {
					System.out.println("Thread 1 acquired the lock");
					Thread.sleep(3000); // Simulate some work with the lock
					lock.unlock();
					System.out.println("Thread 1 released the lock");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		// Give some time for Thread 1 to acquire the lock
		Thread.sleep(1000);

		// Thread 2: Try to acquire the lock
		executor.submit(() -> {
			try {
				if (!lock.tryLock(500, 5000, TimeUnit.MILLISECONDS)) {
					lockHeldByAnotherThread.set(true);
					System.out.println("Thread 2 could not acquire the lock because it is held by another thread");
				} else {
					System.out.println("Thread 2 acquired the lock");
					lock.unlock();
					System.out.println("Thread 2 released the lock");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		// Give some time for Thread 2 to try to acquire the lock
		Thread.sleep(5000);

		executor.shutdown();
		executor.awaitTermination(10, TimeUnit.SECONDS);

		// Verify if Thread 2 was unable to acquire the lock initially
		Assertions.assertTrue(lockHeldByAnotherThread.get(), "Thread 2 should not acquire the lock initially");

		// Clean up
		if (lock.isLocked()) {
			lock.unlock();
		}
	}
}
