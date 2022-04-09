package schedule.domain.assignment.reassignment

import schedule.model.meeting.Meeting
import schedule.model.Room
import schedule.model.enums.Feature
import schedule.model.meeting.TimedMeetingRequest
import org.bson.types.ObjectId
import org.springframework.stereotype.Component
import schedule.model.meeting.TimeInterval

@Component
class ReassignerImpl : Reassigner {

    override fun reassignByMeeting(
        meetingsToConsider: ArrayList<Meeting>,
        rooms: List<Room>,
        timedMeetingRequest: TimedMeetingRequest,
        algorithmLevel: Int
    ): Pair<ArrayList<Meeting>, ObjectId>? {
        // new meetings , room id
        return checkReassignment(algorithmLevel, rooms, meetingsToConsider, timedMeetingRequest)
    }

    fun checkReassignment(
        algorithmLevel: Int,
        allRooms: List<Room>,
        meetings: ArrayList<Meeting>,
        newMeetingRequest: TimedMeetingRequest
    ): Pair<ArrayList<Meeting>, ObjectId>? {
        if (algorithmLevel <= 0) return null
        val roomsPossibleByAttributes = searchRoomsByAttribute(allRooms, newMeetingRequest)
        candidateRoomLoop@ for (candidateRoom in roomsPossibleByAttributes) {
            val meetingsToChange = getInterferingMeetings(candidateRoom, meetings, newMeetingRequest.timeInterval)
            if (meetingsToChange.isEmpty()) return Pair(
                meetings,
                candidateRoom.id
            ) // if there was no interfering meeting, we are good to go
            var imaginaryMeetingList = arrayListOf<Meeting>()
            imaginaryMeetingList.addAll(meetings)
//            var imaginaryMeetingList = ArrayList(meetings)
            imaginaryMeetingList.add(
                Meeting(
                    newMeetingRequest,
                    candidateRoom.id
                )
            ) // imagine the candidate room is assigned to our new meeting

            for (oldMeeting in meetingsToChange) {
                imaginaryMeetingList.remove(oldMeeting) // imagine we didn't have the old meeting (we want to insert it again)
                val meetingToChange = Meeting(oldMeeting)
                val meetingToChangeRequest = TimedMeetingRequest(meetingToChange)
                val reassignedMeetings =
                    checkReassignment(
                        algorithmLevel - 1,
                        allRooms,
                        imaginaryMeetingList,
                        meetingToChangeRequest
                    )
                        ?: continue@candidateRoomLoop //if we couldn't assign the old meeting, the candidate room isn't fine
                imaginaryMeetingList = reassignedMeetings.first
            }
            return Pair(imaginaryMeetingList, candidateRoom.id)
        }
        return null //means the reassignment has failed
    }

    private fun getInterferingMeetings(
        roomToCheck: Room,
        consideringMeetings: List<Meeting>,
        interval: TimeInterval
    ): List<Meeting> {
        val searchResult = arrayListOf<Meeting>()
        for (meeting in consideringMeetings) {
            if (meeting.roomId == roomToCheck.id && meeting.timeInterval.isInterfering(interval)) searchResult.add(
                meeting
            )
        }
        return searchResult
    }

    //todo should this function be here?
    private fun searchRoomsByAttribute(
        rooms: List<Room>,
        meetingRequest: TimedMeetingRequest,
    ): List<Room> {
        val possibleRooms = arrayListOf<Room>()
        val maxSize = meetingRequest.purpose.getMaxCapacity()
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