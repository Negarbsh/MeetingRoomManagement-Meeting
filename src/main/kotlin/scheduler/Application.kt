package scheduler

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = ["scheduler"])
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}