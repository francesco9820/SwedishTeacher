package furhatos.app.swedishteacher.flow

import furhatos.nlu.common.*
import furhatos.flow.kotlin.*

/*
We can use Options state as the parent for other states,
in order to catch other types of responses
*/
val Options =  state(Interaction){
    onResponse {
        println("Caught response not matching any of my intents nr 2")
    }
    onResponse<Goodbye> {
        furhat.say("Alright, bye")
        goto(Idle)
    }
}

//capturing user name
val RequestName: State = state(parent = Options){
    onEntry {
        furhat.ask("What may I call you?")
    }
    onResponse<PersonName> {
        furhat.say("You have a lovely name " + it.intent)
    }
    onResponse{
        furhat.say("Sorry, I am pretty bad with names actually. I’ll just call you buddy.")
    }
}

//greetings
val Start : State = state(Interaction) {

    onEntry {
        random(
            {furhat.ask("Hi there")},
            {furhat.ask("Hello")}
        )
    }
    onResponse<Greeting> {
        goto(RequestName)
    }
    onResponse {
        println("Caught response not matching any of my intents")
        goto(RequestName)
    }
}
