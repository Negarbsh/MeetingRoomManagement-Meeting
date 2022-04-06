package scheduler.dao

import scheduler.model.meeting.Meeting
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MeetingCRUD : MongoRepository<Meeting, ObjectId>