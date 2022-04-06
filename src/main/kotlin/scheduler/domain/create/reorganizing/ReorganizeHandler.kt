package scheduler.domain.create.reorganizing

import scheduler.model.meeting.Meeting
import scheduler.model.Room
import scheduler.model.enums.Feature
import scheduler.model.meeting.TimedMeetingRequest
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import scheduler.domain.room.LargeRoomAllocation
import scheduler.model.meeting.TimeInterval

@Service
class ReorganizeHandler : Reorganizer {

    override fun reorganizeByMeeting(
        meetings: List<Meeting>,
        rooms: List<Room>,
        timedMeetingRequest: TimedMeetingRequest
    ): Pair<List<Meeting>?, ObjectId?> {
        // new meetings , room id
        val maxCapacity = LargeRoomAllocation().getMaxCapacity(timedMeetingRequest.purpose)
        val roomsPossibleByAttributes = searchRoomsByAttribute(rooms, timedMeetingRequest, maxCapacity)
        val emptyRooms = getEmptyRooms(
            rooms, meetings, timedMeetingRequest.timeInterval
        )
        return checkReassignment(emptyRooms, roomsPossibleByAttributes, meetings, timedMeetingRequest, maxCapacity)
    }

    private fun checkReassignment(
        emptyRooms: List<Room>,
        roomsPossibleByAttributes: List<Room>,
        meetings: List<Meeting>,
        timedMeetingRequest: TimedMeetingRequest,
        maxCapacity: Int
    ): Pair<List<Meeting>?, ObjectId?> {
        if (emptyRooms.isEmpty()) return Pair(null, null)
        for (candidateRoom in roomsPossibleByAttributes) {
            val meetingsToChange = getInterferingMeetings(
                candidateRoom.id, candidateRoom, meetings, timedMeetingRequest.timeInterval
            )
            /*If the room was free at the time we want, no other meeting needs to change, and we're good to go!
            (in the simple algorithm for reorganization, this case never happens, but it may happen when we have the recursive implementation) */
            if (meetingsToChange.isEmpty()) return Pair(null, candidateRoom.id)
            /* for simplicity, we ignore the conditions where there are more than one meeting to change
            * */
            if (meetingsToChange.size > 1) continue
            val oldMeeting = meetingsToChange[0]
            val meetingToChange = Meeting(oldMeeting)
            val newMeetingRequest = TimedMeetingRequest(meetingToChange)
            for (emptyRoom in emptyRooms) {
                if (!doesRoomFitToMeeting(emptyRoom, newMeetingRequest, maxCapacity)) continue
                //now we know the interfering meeting can be held in the empty room
                meetingToChange.roomId = emptyRoom.id
                return Pair(listOf(meetingToChange), candidateRoom.id)
            }
        }
        return Pair(null, null)
    }

    private fun getInterferingMeetings(
        roomId: ObjectId,
        roomToCheck: Room,
        allMeetings: List<Meeting>,
        interval: TimeInterval
    ): List<Meeting> {
        TODO("Not yet implemented")
    }

    private fun getEmptyRooms(
        rooms: List<Room>,
        meetings: List<Meeting>,
        interval: TimeInterval
    ): List<Room> {
        TODO("Not yet implemented")
    }

    //todo should this function be here?
    private fun searchRoomsByAttribute(
        rooms: List<Room>,
        meetingRequest: TimedMeetingRequest,
        maxSize: Int
    ): List<Room> {
        val possibleRooms = arrayListOf<Room>()
        for (room in rooms) {
            if (doesRoomFitToMeeting(room, meetingRequest, maxSize))
                possibleRooms.add(room)
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