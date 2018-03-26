package uyun.xianglong.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationPidFileWriter;

import java.net.URL;
import java.net.URLClassLoader;

@SpringBootApplication
public class WebApp {

    public static void main(String[] args) {
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        URL[] urls = ((URLClassLoader) cl).getURLs();
        System.out.println("------------------------ CLASS PATHS ------------------------");
        for (URL url : urls) {
            System.out.println(url.getFile());
        }
        System.out.println("---------------------- CLASS PATHS END ----------------------");

        SpringApplication springApplication = new SpringApplication(WebApp.class);
        springApplication.addListeners(new ApplicationPidFileWriter("AppslivingServerPID"));
        springApplication.run(args);
    }

}
