package domain

import dao.MeetingCRUD
import dao.MeetingRepository
import dao.RoomReader
import dao.RoomRepository
import model.Room
import model.meeting.Meeting
import model.meeting.TimedMeetingRequest
import org.bson.types.ObjectId

class RoomAllocator : RoomSearch { // TODO: think for a better name!
    private val roomDAO: RoomReader = RoomRepository()
    private val meetingDAO: MeetingCRUD = MeetingRepository()

    /*
    * Along all possible rooms, optimizes the room with the least capacity*/
    override fun getBestRoomChoice(meetingRequest: TimedMeetingRequest): ObjectId? {
        val possibleRooms = getAllPossibleRooms(meetingRequest)
        var bestRoomId: ObjectId? = null
        var minCapacity = -1
        for ((id, room) in possibleRooms) {
            val capacity = room.capacity
            if (minCapacity == -1 || minCapacity > capacity) {
                minCapacity = capacity
                bestRoomId = id
            }
        }
        return bestRoomId
    }

    /*
    * Finds all possible rooms for one meeting, regardless of optimization
    * If the meeting is for an interview or a PD-chat, only small rooms are possible */
    override fun getAllPossibleRooms(meetingRequest: TimedMeetingRequest): Map<ObjectId, Room> {
        val purpose = meetingRequest.purpose
        val maxCapacity = LargeRoomAllocation().getMaxCapacity(purpose)
        val roomsPossibleByAttributes =
            roomDAO.searchRooms(meetingRequest.features, minCapacity = meetingRequest.population, maxCapacity)
        val interferingMeetings = meetingDAO.getMeetingsInPeriod(meetingRequest.startTime, meetingRequest.endTime)
        val busyRooms = getRoomIds(interferingMeetings.values)
        return roomsPossibleByAttributes.minus(busyRooms.toSet())
    }

    private fun getRoomIds(meetings: Collection<Meeting>): List<ObjectId> {
        val roomIds = mutableListOf<ObjectId>()
        for (meeting in meetings) {
            roomIds.add(meeting.roomId)
        }
        return roomIds
    }
}