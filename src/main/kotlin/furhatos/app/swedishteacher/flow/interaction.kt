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

    onResponse<ChooseVocabularyType> {
        val vocabType = it.intent.vocabularyType
        if (vocabType != null) {
            goto(registerVocabularyType(vocabType))
        }
        else {
            propagate()
        }
    }

    //If the user wants to know what vocabulary words are available
    onResponse<RequestVocabularyTypes> {
        furhat.say("I know so many words")
        furhat.say("We could practice ${VocabularyType().getEnum(Language.ENGLISH_US).joinToString(", ")}")
        furhat.ask("Which one would you want to practice?")
    }
}

fun teachingVocabulary() : State = state(Options){
    onEntry {
        //Ask something like What is GREEN i Swedish
        furhat.ask("What is the word for... in Swedish")

    }

    /*
    //the user proposes answer which is recognized,
    the bot should say that it is correct
    otherwise let the user try again if they want
     */

}



fun registerVocabularyType(vocabularyType: VocabularyType) : State = state(Options) {
    onEntry {
        random(
            {furhat.say("${vocabularyType.text}, that is a decent choice.")},
            {furhat.say("Nice, I am pretty good at ${vocabularyType.text}!")},
            {furhat.say("Oh wow, ${vocabularyType.text} you say. Let's go for it")}
            )
        //storing the chosen vocabulary type on the user profile
        users.current.currentVocabularyType.vocabType = vocabularyType
        furhat.say("I am now ready to start teaching you ${users.current.currentVocabularyType.vocabType}")
        goto(teachingVocabulary())
    }
}

val IntroVocabulary : State = state(parent = Options){
    onEntry {
        furhat.ask("Alright, are you ready to become a kick-ass Swedish speaker?")
    }
    onReentry {
        furhat.ask("Wanna practice some Swedish?")
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
