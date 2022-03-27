package domain

import dao.MeetingCRUD
import dao.MeetingRepository
import dao.RoomReader
import dao.RoomRepository
import model.meeting.TimedMeetingRequest
import org.bson.types.ObjectId

class RoomAllocator : RoomSearch { // TODO: think for a better name!
    val roomDAO: RoomReader = RoomRepository()
    val meetingDAO: MeetingCRUD = MeetingRepository()

    override fun searchForRoom(meetingRequest: TimedMeetingRequest): ObjectId? {
        TODO("Not yet implemented")
    }
}