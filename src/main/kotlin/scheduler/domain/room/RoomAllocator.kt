package scheduler.domain.room

import scheduler.dao.MeetingCRUD
import scheduler.dao.roomGrpc.RoomReader
import scheduler.dao.roomGrpc.RoomRepository
import scheduler.model.Room
import scheduler.model.meeting.Meeting
import scheduler.model.meeting.TimedMeetingRequest
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import scheduler.model.meeting.TimeInterval

@Service
class RoomAllocator(val meetingDAO: MeetingCRUD) : RoomSearch { // TODO: think for a better name!
    private val roomDAO: RoomReader = RoomRepository()

    /*
    * Along all possible rooms, optimizes the room with the least capacity*/
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

    /*
    * Finds all possible rooms for one meeting, regardless of optimization
    * If the meeting is for an interview or a PD-chat, only small rooms are possible */
    override fun getAllPossibleRooms(meetingRequest: TimedMeetingRequest): List<Room> {
        val purpose = meetingRequest.purpose
        val maxCapacity = LargeRoomAllocation().getMaxCapacity(purpose)
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
