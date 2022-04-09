package schedule.domain

import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import schedule.dao.MeetingCRUD
import schedule.dao.roomGrpc.RoomReader
import schedule.domain.assignment.Assigner
import schedule.domain.assignment.reassignment.Reassigner
import schedule.model.MeetingSearchRequest
import schedule.model.Room
import schedule.model.meeting.*
import java.sql.Timestamp

@Component
class MeetingServiceImpl(
    @Autowired private val meetingAssigner: Assigner,
    @Autowired private val meetingCRUD: MeetingCRUD,
    @Autowired val reassigner: Reassigner,
    @Autowired val roomDAO: RoomReader
) : MeetingService {

    override fun schedule(timedMeetingRequest: TimedMeetingRequest): ObjectId? {
        val roomId = meetingAssigner.scheduleFixedTimeMeeting(timedMeetingRequest)
        if (roomId != null) return roomId
        val (changedMeetings, newRoomId) = createByReorganization(timedMeetingRequest)
        if (newRoomId == null) return null
        val meetingId = meetingAssigner.finalizeMeetingCreation(timedMeetingRequest, newRoomId)
        resetDB(changedMeetings)
        return meetingId
    }


    private fun resetDB(overwritingMeetings: List<Meeting>?) {
        if (overwritingMeetings == null) return
        meetingCRUD.saveAll(overwritingMeetings)
    }


    private fun createByReorganization(timedMeetingRequest: TimedMeetingRequest): Pair<List<Meeting>?, ObjectId?> {
        val meetings = meetingCRUD.findAll() //todo no! (it should be passed from the outer layer)
        val rooms: List<Room> = roomDAO.getAllRooms()
        val (changedMeetings, newRoomId) = reassigner.reorganizeByMeeting(
            meetings,
            rooms,
            timedMeetingRequest,
        )
        return Pair(changedMeetings, newRoomId)
    }

    override fun getEarliestMeetingChance(
        timeLowerBound: Timestamp?,
        meetingRequest: MeetingRequest
    ): Pair<ObjectId, Timestamp>? {
        return meetingAssigner.getEarliestMeetingChance(timeLowerBound, meetingRequest, 15, 1000)
    }

    override fun edit(editRequest: MeetingEditRequest, editorMail: String): ObjectId? {
        val isCanceled = cancel(editRequest.meetingId)
        if (!isCanceled) return null
        return schedule(editRequest.newMeetingRequest)
    }

    override fun cancel(meetingId: ObjectId): Boolean {
        return try {
            meetingCRUD.deleteById(meetingId)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun searchMeeting(meetingSearchRequest: MeetingSearchRequest): Set<Meeting> {
        var searchResult = meetingCRUD.findInterferingWithInterval(meetingSearchRequest.timeInterval)
        if (meetingSearchRequest.roomId != null) {
            val searchByRoomResult = meetingCRUD.findAllByRoomId(meetingSearchRequest.roomId)
            searchResult = searchResult.intersect(searchByRoomResult.toSet())
        }
        return searchResult
    }

    override fun getMeetingsInInterval(interval: TimeInterval): Set<Meeting> {
        return meetingCRUD.findInterferingWithInterval(interval)
    }
}