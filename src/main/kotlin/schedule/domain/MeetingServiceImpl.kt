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
    @Autowired val meetingCRUD: MeetingCRUD,
    @Autowired val reassigner: Reassigner,
    @Autowired val roomDAO: RoomReader
) : MeetingService {

    override fun schedule(timedMeetingRequest: TimedMeetingRequest): ObjectId? {
        val roomId = meetingAssigner.scheduleFixedTimeMeeting(timedMeetingRequest)
        if (roomId != null) return roomId
        val reassignResult = handlerReassignment(timedMeetingRequest) ?: return null
        val (changedMeetings, newRoomId) = reassignResult
        val meetingId = meetingAssigner.finalizeMeetingCreation(timedMeetingRequest, newRoomId)
        resetDB(changedMeetings)
        return meetingId
    }

    private fun resetDB(overwritingMeetings: List<Meeting>?) {
        if (overwritingMeetings == null) return
        meetingCRUD.saveAll(overwritingMeetings)
    }

    private fun handlerReassignment(timedMeetingRequest: TimedMeetingRequest): Pair<List<Meeting>, ObjectId>? {
        val rooms: List<Room> = roomDAO.findAllRooms()
        val reassignAlgorithmLevels = 5
        val meetings = ArrayList(getRequiredMeetingsForReassign(timedMeetingRequest, reassignAlgorithmLevels))
        return reassigner.reassignByMeeting(
            meetings,
            rooms,
            timedMeetingRequest,
            reassignAlgorithmLevels,
        )
    }

    private fun getRequiredMeetingsForReassign(
        timedMeetingRequest: TimedMeetingRequest,
        reassignAlgorithmLevels: Int
    ): Set<Meeting> {
        if (reassignAlgorithmLevels <= 0) return setOf()
        val meetings = meetingCRUD.findAllByOffice(office = timedMeetingRequest.office)
        val neededMeetings = mutableSetOf<Meeting>()
        for (interferingMeeting in getInterferingMeetings(meetings, timedMeetingRequest.timeInterval)) {
            neededMeetings.addAll(
                getRequiredMeetingsForReassign(
                    TimedMeetingRequest(interferingMeeting),
                    reassignAlgorithmLevels - 1
                )
            )
        }
        return neededMeetings
    }

    private fun getInterferingMeetings(
        consideringMeetings: List<Meeting>,
        interval: TimeInterval
    ): List<Meeting> {
        val searchResult = arrayListOf<Meeting>()
        for (meeting in consideringMeetings) {
            if (meeting.timeInterval.isInterfering(interval)) searchResult.add(meeting)
        }
        return searchResult
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
        var searchResult = meetingCRUD.findAllInsideTimeInterval(
            meetingSearchRequest.timeInterval.start,
            meetingSearchRequest.timeInterval.end
        )
        if (meetingSearchRequest.roomId != null) {
            val searchByRoomResult = meetingCRUD.findAllByRoomId(meetingSearchRequest.roomId)
            searchResult = searchResult.intersect(searchByRoomResult.toSet())
        }
        return searchResult
    }

    override fun getMeetingsInInterval(interval: TimeInterval): Set<Meeting> {
        return meetingCRUD.findAllInsideTimeInterval(interval.start, interval.end)
    }
}