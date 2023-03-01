package com.dtp.example.controller;

import com.dtp.core.DtpRegistry;
import com.dtp.core.support.runnable.NamedRunnable;
import com.dtp.core.thread.DtpExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Redick01
 */
@Slf4j
@RestController
@SuppressWarnings("all")
public class TestController {

    @Resource
    private ThreadPoolExecutor dtpExecutor1;
    @Resource
    private ThreadPoolExecutor commonExecutor;

    @GetMapping("/dtp-zookeeper-example/test")
    public String test() throws InterruptedException {
        log.info("dtp-zookeeper-example begin..." + Thread.currentThread().getName());
        task();
        log.info("dtp-zookeeper-example end..." + Thread.currentThread().getName());
        return "success";
    }



    public void task() throws InterruptedException {
        DtpExecutor dtpExecutor2 = DtpRegistry.getDtpExecutor("dtpExecutor2");
        for (int i = 0; i < 50; i++) {
            Thread.sleep(100);

            commonExecutor.execute(() -> {
                try {
                    Random random = new Random();
                    Thread.sleep(random.nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("i am commonExecutor task");
            });
        }
        for (int i = 0; i < 100; i++) {
            Thread.sleep(100);
            dtpExecutor1.execute(() -> {
                try {
                    Random random = new Random();
                    Thread.sleep(random.nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("i am dtpExecutor1 task");
            });
        }

        for (int i = 0; i < 200; i++) {
            Thread.sleep(100);
            dtpExecutor2.execute(NamedRunnable.of(() -> {
                try {
                    Random random = new Random();
                    Thread.sleep(random.nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("i am dtpExecutor2 task");
            }, "task-" + i));
        }
    }
}
