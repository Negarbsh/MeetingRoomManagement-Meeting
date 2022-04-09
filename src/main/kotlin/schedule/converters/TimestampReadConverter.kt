package schedule.converters

import org.springframework.core.convert.converter.Converter
import java.sql.Timestamp
import java.util.*

class TimestampReadConverter: Converter<Date, Timestamp> {
    override fun convert(source: Date): Timestamp {
        return Timestamp(source.time)
    }

}