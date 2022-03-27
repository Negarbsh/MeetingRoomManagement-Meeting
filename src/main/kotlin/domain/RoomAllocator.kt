package domain

import dao.MeetingCRUD
import dao.MeetingRepository
import dao.RoomReader
import dao.RoomRepository
import model.Room
import model.enums.MeetingPurpose
import model.meeting.TimedMeetingRequest
import org.bson.types.ObjectId

class RoomAllocator : RoomSearch { // TODO: think for a better name!
    private val roomDAO: RoomReader = RoomRepository()
    private val meetingDAO: MeetingCRUD = MeetingRepository()

    override fun getBestRoomChoice(meetingRequest: TimedMeetingRequest): ObjectId? {
        val possibleRooms = getAllPossibleRooms(meetingRequest)
        var bestRoomId: ObjectId? = null
        var minExtraCapacity = -1
        for ((id, room) in possibleRooms) {
            val extraCapacity = room.capacity - meetingRequest.population
            if (minExtraCapacity == -1 || minExtraCapacity > extraCapacity) {
                minExtraCapacity = extraCapacity
                bestRoomId = id
            }
        }
        return bestRoomId
    }

    /*
    * Finds all possible rooms for one meeting, regardless of optimization
    * If the meeting is for an interview or a PD-chat, only small rooms are possible */
    override fun getAllPossibleRooms(meetingRequest: TimedMeetingRequest): HashMap<ObjectId, Room> {
        val purpose = meetingRequest.purpose
        val maxCapacity = getMaxCapacity(purpose)
        val roomsPossibleByAttributes =
            roomDAO.searchRooms(meetingRequest.features, minCapacity = meetingRequest.population, maxCapacity)
        val interferingMeetings = meetingDAO.getMeetingsInPeriod(meetingRequest.startTime, meetingRequest.endTime)
        TODO("Not yet implemented")
    }

    private fun getMaxCapacity(purpose: MeetingPurpose): Int {
        var maxCapacity = -1
        if (purpose == MeetingPurpose.PD_CHAT || purpose == MeetingPurpose.INTERVIEW)
            maxCapacity = 3
        return maxCapacity
    }


}