package notifService

import schedule.model.meeting.MeetingRequest

class Notifier {

    fun sendMail(meetingRequest: MeetingRequest){
       println("Sent email to meeting participants!")
    }
}