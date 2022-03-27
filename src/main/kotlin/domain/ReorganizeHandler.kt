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
        timedMeetingRequest: TimedMeetingRequest,
        maxCapacity: Int
    ): Triple<HashMap<ObjectId, Meeting>?, ObjectId?, ObjectId?> {

        val roomsPossibleByAttributes = searchRoomsByAttribute(rooms, timedMeetingRequest, maxCapacity)
        val emptyRooms = getEmptyRooms(
            rooms, meetings,
            timedMeetingRequest.startTime,
            timedMeetingRequest.endTime
        )
        for ((roomId, possibleRoom) in roomsPossibleByAttributes) {
            val meetingsToChange = getInterferingMeetings(
                roomId,
                possibleRoom,
                meetings,
                timedMeetingRequest.startTime,
                timedMeetingRequest.endTime
            )
            TODO("Not yet implemented")
        }
        TODO("Not yet implemented")
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
            if (room.capacity >= meetingRequest.population
                && (maxSize == -1 || maxSize >= room.capacity)
                && areFeaturesEnough(room.features, meetingRequest.features)
            )
                possibleRooms[id] = room
        }
        return possibleRooms
    }

    private fun areFeaturesEnough(roomFeatures: List<Feature>, wantedFeatures: List<Feature>?): Boolean {
        if (wantedFeatures == null) return true
        for (feature in wantedFeatures) {
            if (!roomFeatures.contains(feature)) return false
        }
        return true
    }
}