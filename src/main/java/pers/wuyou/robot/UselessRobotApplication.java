package pers.wuyou.robot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pers.wuyou.robot.core.annotation.UselessScan;

/**
 * @author wuyou
 */
@SpringBootApplication
@EnableTransactionManagement
@UselessScan(
        listenerPackages = "pers.wuyou.robot.listener"
//        filterPackages = "pers.wuyou.robot.filter"
)
public class UselessRobotApplication {

    public static void main(String[] args) {
        SpringApplication.run(UselessRobotApplication.class, args);
    }

}
