package furhatos.app.swedishteacher.flow

import furhatos.flow.kotlin.*
import furhatos.util.*

val Idle: State = state {

    init {
        furhat.setVoice(Language.ENGLISH_US, Gender.MALE)
        if (users.count > 0) {
            furhat.attend(users.random)
            goto(Start)
        }
    }

    onEntry {
        furhat.attendNobody()
    }

    onUserEnter {
        furhat.attend(it)
        goto(Start)
    }
}

val maxRounds = 5
var rounds = 0
var shouldChangeUser = true
var playing = false

val Interaction: State = state {

    onUserLeave(instant = true) {
        if (users.count > 0) {
            if (it == users.current) {
                furhat.attend(users.other)
                goto(Start)
            } else {
                furhat.glance(it)
            }
        } else {
            goto(Idle)
        }
    }

    onUserEnter(instant = true) {
        furhat.glance(it)
    }

    //Finish conversation after a delay. We can adjust the delay as we want
    onTime(delay=120000) {
        furhat.say("Alright, your Swedish is not too bad. " +
                "There is still some work to be done though. " +
                "But I am a bit tired now and need my beauty sleep. " +
                "It’s been really nice to meet you. Have a nice day!")
        goto(Idle)
    }

}