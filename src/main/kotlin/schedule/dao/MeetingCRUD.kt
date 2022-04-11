package schedule.dao

import schedule.model.meeting.Meeting
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import schedule.model.enums.Office
import java.sql.Timestamp
import java.util.Date

@Repository
interface MeetingCRUD : MongoRepository<Meeting, ObjectId> {

    @Query(value = "\$or [{'timeInterval.start' : {\$gte:v?0}}, {'timeInterval.end' : {\$lte: ?1}}]")
    fun findAllInterferingWithInterval(startTime: Timestamp, endTime: Timestamp): Set<Meeting>

    fun findAllByRoomId(roomId: ObjectId): List<Meeting>

    @Query(value = "{'timeInterval.start': {\$gte: ?0}, office: ?1}")
    fun findAllByOffice(startTimestamp: Date = Date(), office: Office): List<Meeting>

//    @Query(value = "\$and [" +
//            "{" +
//                "\$or [{'timeInterval.start' : {\$gte:v?1}}, {'timeInterval.end' : {\$lte: ?2}}]" +
//            "} ," +
//            "{ " +
//                "participants : { \$elemMatch : { \$in ?0}}" +
//            "}]",
//        exists = true)
//    //db.inventory.find({tags : {$elemMatch : {name: "mmd"}}})
//    fun areParticipantsFree(participants: List<Participant>, startTime: Date, endTime: Date ): Boolean
}