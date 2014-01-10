package com.rex.poolserver;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: hur2
 * Date: 12/26/13
 * Time: 3:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class ThreadPool{

    List<ReadWorker> idle;

    public ThreadPool(int threadnum, PoolNIOServer server){
        idle = new LinkedList<ReadWorker>();
        for (int i=0;i<threadnum;i++){
            ReadWorker readWorker = new ReadWorker(this, server);
            readWorker.setName("Worker" + i);
            idle.add(readWorker);
            readWorker.start();
        }
    }

    public ReadWorker getWorker(){
        synchronized (idle){
            if (!idle.isEmpty()){
                return idle.remove(0);
            }
        }

        return null;
    }

    public void returnWorker(ReadWorker readWorker){
        synchronized (idle){
            idle.add(readWorker);
        }
    }
}