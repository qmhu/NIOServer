package com.rex.server;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: hur2
 * Date: 12/26/13
 * Time: 3:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class ThreadPool{

    private List<Thread> _workers;
    private Queue<Runnable> _jobs = new ConcurrentLinkedQueue<Runnable>();
    private static int initWorkerNum = 4;
    private static int maxWorkerNum = 2 << 8;
    private AtomicInteger currWorkerIndex = new AtomicInteger(0);
    private int sleepTime = 200;

    public ThreadPool(){
        this(initWorkerNum);
    }

    public ThreadPool(int workernum){
        _workers = new CopyOnWriteArrayList<Thread>();

        for (int i=0;i < workernum; i++){
            initWorker();
        }
    }

    private void initWorker(){
        synchronized (_jobs){
            if (currWorkerIndex.intValue() >= maxWorkerNum){
                return;
            }

            Thread worker = new Worker();
            worker.setName("worker" + currWorkerIndex.intValue());
            worker.start();
            _workers.add(worker);
            currWorkerIndex.incrementAndGet();
        }
    }

    class Worker extends Thread{

        public void run(){
            while (true){
                if (!_jobs.isEmpty()){
                    Runnable runnable = _jobs.poll();
                    if (runnable != null){
                        runnable.run();
                    }
                }

                if (_jobs.isEmpty()){
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }

                int currWorkers = currWorkerIndex.intValue();
                if (_jobs.size() > currWorkers){
                    // need add worker
                    initWorker();
                }

            }
        }

    }

    public boolean dispatch(Runnable runnable){
        return _jobs.add(runnable);
    }

}