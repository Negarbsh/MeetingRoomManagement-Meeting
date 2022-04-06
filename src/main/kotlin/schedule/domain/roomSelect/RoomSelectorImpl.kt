package schedule.domain.roomSelect

import schedule.dao.MeetingCRUD
import schedule.dao.roomGrpc.RoomReader
import schedule.dao.roomGrpc.RoomRepository
import schedule.model.Room
import schedule.model.meeting.Meeting
import schedule.model.meeting.TimedMeetingRequest
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import schedule.model.meeting.TimeInterval

@Service
class RoomSelectorImpl(val meetingDAO: MeetingCRUD) : RoomSelector {
    private val roomDAO: RoomReader = RoomRepository()

     override fun getBestRoomChoice(meetingRequest: TimedMeetingRequest): ObjectId? {
        val possibleRooms = getAllPossibleRooms(meetingRequest)
        var bestRoomId: ObjectId? = null
        var minCapacity = -1
        for (room in possibleRooms) {
            val capacity = room.capacity
            if (minCapacity == -1 || minCapacity > capacity) {
                minCapacity = capacity
                bestRoomId = room.id
            }
        }
        return bestRoomId
    }

    /*Finds all possible rooms for one meeting, regardless of optimization*/
    fun getAllPossibleRooms(meetingRequest: TimedMeetingRequest): List<Room> {
        val maxCapacity = meetingRequest.purpose.getMaxCapacity()
        val roomsPossibleByAttributes =
            roomDAO.searchRooms(meetingRequest.features, minCapacity = meetingRequest.population, maxCapacity)
        val interferingMeetings = getMeetingsInInterval(meetingRequest.timeInterval, meetingDAO)
        val busyRooms = getMeetingRooms(interferingMeetings)
        return roomsPossibleByAttributes.minus(getRoomsById(busyRooms).toSet())
    }

    private fun getRoomsById(roomIds: List<ObjectId>): List<Room> {
        TODO("Not yet implemented")
    }

    private fun getMeetingRooms(meetings: Collection<Meeting>): List<ObjectId> {
        val roomIds = mutableListOf<ObjectId>()
        for (meeting in meetings) {
            roomIds.add(meeting.roomId)
        }
        return roomIds
    }

    private fun getMeetingsInInterval(timeInterval: TimeInterval,  meetingDAO: MeetingCRUD): List<Meeting> {
        return meetingDAO.findAll() //todo query
    }
}
