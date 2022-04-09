package schedule.model.meeting

import java.sql.Timestamp

class TimeInterval(val start: Timestamp, val end: Timestamp) {

    fun containsTimestamp(timestamp: Timestamp): Boolean {
        return this.start.before(timestamp) && this.end.after(timestamp)
    }

    fun isInterfering(timeInterval: TimeInterval): Boolean {
        return this.containsTimestamp(timeInterval.start) || this.containsTimestamp(timeInterval.end)
    }

    fun getDuration(): Long {
        val duration = end.nanos - start.nanos
        return duration.toLong()
    }
}