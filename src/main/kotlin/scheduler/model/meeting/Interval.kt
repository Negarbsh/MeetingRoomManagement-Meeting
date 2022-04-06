package scheduler.model.meeting

import java.sql.Timestamp

class Interval(val start: Timestamp, val end: Timestamp) {

    fun containsTimestamp(timestamp: Timestamp): Boolean {
        return this.start.before(timestamp) && this.end.after(timestamp)
    }

    fun isInterfering(interval: Interval): Boolean {
        return this.containsTimestamp(interval.start) || this.containsTimestamp(interval.end)
    }
}