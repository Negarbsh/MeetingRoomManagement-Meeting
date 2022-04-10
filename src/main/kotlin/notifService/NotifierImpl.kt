package notifService

import schedule.model.meeting.MeetingRequest

class NotifierImpl : Notifier {
    override fun sendMail(meetingRequest: MeetingRequest) {
        println("Sent email to meeting participants!")
    }
}