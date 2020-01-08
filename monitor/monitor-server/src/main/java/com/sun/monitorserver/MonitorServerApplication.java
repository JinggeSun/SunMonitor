package com.sun.monitorserver;

import com.sun.monitorserver.server.MonitorServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zcm
 */
@SpringBootApplication
public class MonitorServerApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(MonitorServerApplication.class, args);
    }


    /**
     * 启动时，执行任务。
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        new MonitorServer().run();
    }
}
