package notifService

import schedule.model.meeting.MeetingRequest

interface Notifier {

    fun sendMail(meetingRequest: MeetingRequest)
}