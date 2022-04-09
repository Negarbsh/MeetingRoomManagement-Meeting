package schedule

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.EnableMongoAuditing

@Configuration
@EnableAutoConfiguration
@EnableMongoAuditing
@ComponentScan(basePackages = ["schedule"])
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}