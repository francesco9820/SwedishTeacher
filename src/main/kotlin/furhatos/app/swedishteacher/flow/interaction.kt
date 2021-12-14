package furhatos.app.swedishteacher.flow

import furhatos.nlu.common.*
import furhatos.app.swedishteacher.nlu.*
import furhatos.flow.kotlin.*
import furhatos.util.Language

/*
We can use Options state as the parent for other states,
in order to catch other types of responses
*/
val Options =  state(Interaction){

    onResponse<Goodbye> {
        furhat.say("Alright, bye")
        goto(Idle)
    }

    onResponse<RequestVocabularyTypes> {
        furhat.say("I know so many words")
        furhat.say("We could practice ${VocabularyType().getEnum(Language.ENGLISH_US).joinToString(", ")}")
        //furhat.ask("Which one would you want to practice?")
    // If user asks what types of vocabulary are available
       // Should list all types of vocabulary like colors, clothing etc
    }
    onResponse {
        println("Caught response not matching any of my intents nr 2")
    }


    /*
    onResponse<ChooseVocabulary>{
        If users chooses a vocabulary type,
        the type should be sent to a function that stores it on the user
    }
     */

}


val IntroVocabulary : State = state(parent = Options){
    onEntry {
        furhat.ask("Alright, are you ready to become a kick-ass Swedish speaker?")
    }
    onResponse<Yes> {
        furhat.ask("What type of Swedish vocabulary do you want to practice?")
    }
    onResponse<No> {
        furhat.say("We could also talk about the weather but I never go outside. " +
                "So let’s stick to the stuff I am good at. ")
        furhat.ask("What type of Swedish vocabulary do you want to practice?")
    }
}


//capturing user's name
//name still needs to be stored on the user.
val RequestName: State = state(Interaction){
    onEntry {
        furhat.ask("What may I call you?")
    }
    onResponse<PersonName> {
        furhat.say("You have a lovely name " + it.intent)
        goto(IntroVocabulary)
    }
    onResponse{
        furhat.say("Sorry, I am pretty bad with names actually. I’ll just call you buddy.")
        goto(IntroVocabulary)
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
