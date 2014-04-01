package com.rex.poolserver;

/**
 * Created with IntelliJ IDEA.
 * User: hur2
 * Date: 3/30/14
 * Time: 10:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class ThreadPoolTest {

    class Handler implements Runnable{

        @Override
        public void run() {
            for (int i=0;i<10000;i++){

            }
            System.out.println("finished");

        }
    }

    @org.junit.Test
    public void testDispatch() throws Exception {
        ThreadPool threadPool = new ThreadPool();
        threadPool.dispatch(new Handler());
        threadPool.dispatch(new Handler());
        threadPool.dispatch(new Handler());
        threadPool.dispatch(new Handler());
        threadPool.dispatch(new Handler());
        threadPool.dispatch(new Handler());
        threadPool.dispatch(new Handler());

        while (true){
            Thread.sleep(1000);
        }


    }
}
