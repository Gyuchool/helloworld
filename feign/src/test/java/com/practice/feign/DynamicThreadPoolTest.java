package com.practice.feign;

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DynamicThreadPoolTest {

    @Test
    public void testDynamicThreadPoolAdjustment() throws InterruptedException {
        // ThreadPoolExecutor 초기 설정
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                10, // corePoolSize
                50, // maximumPoolSize
                30, // keepAliveTime (초)
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100), // 작업 큐
                new ThreadPoolExecutor.CallerRunsPolicy() // 거부 정책
        );

        // 스레드 풀의 동적 크기 조정 메서드
        Runnable adjustPoolSize = () -> adjustThreadPool(executor);

        // ScheduledExecutorService로 1초마다 동적으로 스레드 풀 조정
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(adjustPoolSize, 0, 97, TimeUnit.MILLISECONDS);

        // 트래픽 시뮬레이션: 200개의 작업을 제출
        for (int i = 0; i < 200; i++) {
            int taskId = i;
            executor.submit(() -> {
                try {
                    System.out.println("Executing task " + taskId + " by " + Thread.currentThread().getName());
                    // 랜덤한 작업 시간 시뮬레이션: 100ms ~ 500ms
                    long taskDuration = 100 + (long)(Math.random() * 400);
                    Thread.sleep(taskDuration);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // 동작 중간에 스레드 풀 크기가 조정되었는지 확인
        Thread.sleep(5000); // 5초 동안 실행 후 검증

        // 활성 스레드 수와 풀 크기를 출력 (디버깅용)
        System.out.println("Active Threads: " + executor.getActiveCount());
        System.out.println("Core Pool Size: " + executor.getCorePoolSize());
        System.out.println("Maximum Pool Size: " + executor.getMaximumPoolSize());

        // Assertions: 동적으로 크기가 조정되었는지 확인
        assertTrue(executor.getCorePoolSize() >= 10 && executor.getCorePoolSize() <= 50, "Core pool size out of bounds");
        assertTrue(executor.getMaximumPoolSize() >= 50 && executor.getMaximumPoolSize() <= 100, "Maximum pool size out of bounds");

        // Executor와 Scheduler 종료
        scheduler.shutdown();
        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS), "Executor did not shut down in time");
    }

    // 트래픽 상태를 감지하여 스레드 풀 크기 조정
    private void adjustThreadPool(ThreadPoolExecutor executor) {
        int queueSize = executor.getQueue().size(); // 대기 중인 작업 수
        int poolSize = executor.getPoolSize(); // 현재 생성된 스레드 수

        System.out.println("Adjusting Pool: Queue Size = " + queueSize + ", Pool Size = " + poolSize);

        // 대기 작업이 많을 때 스레드 풀 확장
        if (queueSize > 50 && poolSize < 100) {
            executor.setCorePoolSize(Math.min(poolSize + 10, 100));
            executor.setMaximumPoolSize(Math.min(poolSize + 20, 100));
            System.out.println("Increased thread pool size: Core = " + executor.getCorePoolSize() + ", Max = " + executor.getMaximumPoolSize());
        }

        // 대기 작업이 적을 때 스레드 풀 축소
        if (queueSize < 10 && poolSize > 10) {
            executor.setCorePoolSize(Math.max(poolSize - 10, 10));
            executor.setMaximumPoolSize(Math.max(poolSize - 20, 20));
            System.out.println("Decreased thread pool size: Core = " + executor.getCorePoolSize() + ", Max = " + executor.getMaximumPoolSize());
        }
    }
}
