package schedule.dao

import schedule.model.meeting.Meeting
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import schedule.model.meeting.TimeInterval

@Repository
interface MeetingCRUD : MongoRepository<Meeting, ObjectId> {

    //TODO check if we can implement this using @Query
    fun findInterferingWithInterval(timeInterval: TimeInterval): Set<Meeting> {
        val searchResult = arrayListOf<Meeting>()
        val allMeetings = findAll()
        for (meeting in allMeetings) {
            if (meeting.timeInterval.isInterfering(timeInterval))
                searchResult.add(meeting)
        }
        return searchResult.toSet()
    }

    fun findAllByRoomId(roomId: ObjectId) : List<Meeting>
}