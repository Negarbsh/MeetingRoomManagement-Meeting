package domain

import model.meeting.Meeting
import model.Room
import model.enums.Feature
import model.meeting.TimedMeetingRequest
import org.bson.types.ObjectId
import java.sql.Timestamp

class ReorganizeHandler : Reorganizer {

    override fun reorganizeByMeeting(
        meetings: HashMap<ObjectId, Meeting>,
        rooms: HashMap<ObjectId, Room>,
        timedMeetingRequest: TimedMeetingRequest
    ): Pair<HashMap<ObjectId, Meeting>?, ObjectId?> {
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
        meetings: HashMap<ObjectId, Meeting>,
        timedMeetingRequest: TimedMeetingRequest,
        maxCapacity: Int
    ): Pair<HashMap<ObjectId, Meeting>?, ObjectId?> {
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
            val meetingToChangeId = meetingsToChange.keys.elementAt(0)
            val oldMeeting = meetingsToChange[meetingToChangeId] ?: continue
            val meetingToChange = Meeting(oldMeeting)
            val newMeetingRequest = TimedMeetingRequest(meetingToChange)
            for ((emptyRoomId, emptyRoom) in emptyRooms) {
                if (!doesRoomFitToMeeting(emptyRoom, newMeetingRequest, maxCapacity)) continue
                //now we know the interfering meeting can be held in the empty room
                meetingToChange.roomId = emptyRoomId
                return Pair(hashMapOf(Pair(meetingToChangeId, meetingToChange)), candidateRoom.id)
            }
        }
        return Pair(null, null)
    }

    private fun getInterferingMeetings(
        roomId: ObjectId,
        roomToCheck: Room,
        allMeetings: HashMap<ObjectId, Meeting>,
        startTime: Timestamp,
        endTime: Timestamp
    ): HashMap<ObjectId, Meeting> {
        TODO("Not yet implemented")
    }

    private fun getEmptyRooms(
        rooms: java.util.HashMap<ObjectId, Room>,
        meetings: java.util.HashMap<ObjectId, Meeting>,
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