package scheduler.domain

import scheduler.model.meeting.Meeting
import scheduler.model.Room
import scheduler.model.enums.Feature
import scheduler.model.meeting.TimedMeetingRequest
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import java.sql.Timestamp

@Service
class ReorganizeHandler : Reorganizer {

    override fun reorganizeByMeeting(
        meetings: List<Meeting>,
        rooms: HashMap<ObjectId, Room>,
        timedMeetingRequest: TimedMeetingRequest
    ): Pair<List<Meeting>?, ObjectId?> {
        // new meetings , room id
        val maxCapacity = LargeRoomAllocation().getMaxCapacity(timedMeetingRequest.purpose)
        val roomsPossibleByAttributes = searchRoomsByAttribute(rooms, timedMeetingRequest, maxCapacity)
        val emptyRooms = getEmptyRooms(
            rooms, meetings, timedMeetingRequest.startTime, timedMeetingRequest.endTime
        )
        return checkReassignment(emptyRooms, roomsPossibleByAttributes, meetings, timedMeetingRequest, maxCapacity)
    }

    private fun checkReassignment(
        emptyRooms: HashMap<ObjectId, Room>,
        roomsPossibleByAttributes: HashMap<ObjectId, Room>,
        meetings: List<Meeting>,
        timedMeetingRequest: TimedMeetingRequest,
        maxCapacity: Int
    ): Pair<List<Meeting>?, ObjectId?> {
        if (emptyRooms.isEmpty()) return Pair(null, null)
        for ((roomId, candidateRoom) in roomsPossibleByAttributes) {
            val meetingsToChange = getInterferingMeetings(
                roomId, candidateRoom, meetings, timedMeetingRequest.startTime, timedMeetingRequest.endTime
            )
            /*If the room was free at the time we want, no other meeting needs to change, and we're good to go!
            (in the simple algorithm for reorganization, this case never happens, but it may happen when we have the recursive implementation) */
            if (meetingsToChange.isEmpty()) return Pair(null, roomId)
            /* for simplicity, we ignore the conditions where there are more than one meeting to change
            * */
            if (meetingsToChange.size > 1) continue
            val oldMeeting = meetingsToChange[0]
            val meetingToChange = Meeting(oldMeeting)
            val newMeetingRequest = TimedMeetingRequest(meetingToChange)
            for ((emptyRoomId, emptyRoom) in emptyRooms) {
                if (!doesRoomFitToMeeting(emptyRoom, newMeetingRequest, maxCapacity)) continue
                //now we know the interfering meeting can be held in the empty room
                meetingToChange.roomId = emptyRoomId
                return Pair(listOf(meetingToChange), candidateRoom.id)
            }
        }
        return Pair(null, null)
    }

    private fun getInterferingMeetings(
        roomId: ObjectId,
        roomToCheck: Room,
        allMeetings: List<Meeting>,
        startTime: Timestamp,
        endTime: Timestamp
    ): List<Meeting> {
        TODO("Not yet implemented")
    }

    private fun getEmptyRooms(
        rooms: HashMap<ObjectId, Room>,
        meetings: List<Meeting>,
        startTime: Timestamp,
        endTime: Timestamp
    ): HashMap<ObjectId, Room> {
        TODO("Not yet implemented")
    }

    //todo should this function be here?
    private fun searchRoomsByAttribute(
        rooms: HashMap<ObjectId, Room>,
        meetingRequest: TimedMeetingRequest,
        maxSize: Int
    ): HashMap<ObjectId, Room> {
        val possibleRooms = hashMapOf<ObjectId, Room>()
        for ((id, room) in rooms) {
            if (doesRoomFitToMeeting(room, meetingRequest, maxSize))
                possibleRooms[id] = room
        }
        return possibleRooms
    }

    private fun doesRoomFitToMeeting(room: Room, meetingRequest: TimedMeetingRequest, maxSize: Int): Boolean {
        return room.capacity >= meetingRequest.population
                && (maxSize == -1 || maxSize >= room.capacity)
                && areFeaturesEnough(room.features, meetingRequest.features)
    }

    private fun areFeaturesEnough(roomFeatures: List<Feature>, wantedFeatures: List<Feature>?): Boolean {
        if (wantedFeatures == null) return true
        for (feature in wantedFeatures) {
            if (!roomFeatures.contains(feature)) return false
        }
        return true
    }
}