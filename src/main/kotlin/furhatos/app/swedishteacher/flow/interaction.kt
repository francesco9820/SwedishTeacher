package furhatos.app.swedishteacher.flow

import furhatos.nlu.common.*
import furhatos.app.swedishteacher.nlu.*
import furhatos.flow.kotlin.*
import furhatos.util.Language

/*
We can use Options state as a parent
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
            random(
                {furhat.say("${vocabType.text}, that is a decent choice.")},
                {furhat.say("Nice, I am pretty good at ${vocabType.text}!")},
                {furhat.say("Oh wow, ${vocabType.text} you say. Let's go for it")}
            )
            goto(registerVocabularyType(vocabType))
        }
        else {
            propagate()
        }
    }
    onResponse<DontKnow> {
        goto(vocabularyTypeRecommendation())
    }
    /*
    TO DO:
    Allow the user to change to vocabulary type during learning

    onResponse<RequestVocabularyRecommendation> {
        goto(vocabularyTypeRecommendation())
    }

     */

    //If the user wants to know what vocabulary words are available
    onResponse<RequestVocabularyTypes> {
        furhat.say("I know so many words")
        furhat.say("We could practice ${VocabularyType().getEnum(Language.ENGLISH_US).joinToString(", ")}")
        furhat.ask("Which one would you want to practice?")
    }
}

fun vocabularyTypeRecommendation() : State = state(Options){
    onEntry {
        furhat.say("We could practice ${VocabularyType().getEnum(Language.ENGLISH_US).random()}")
        furhat.ask("Would you like that?")

    //TO DO: should also make sure to not give a suggestion that the user
    // is currently learning or has learned

    }
    onResponse<Yes> {

    //TO DO:
    // should take the suggestion and send it to the function below
    //goto(registerVocabularyType(vocabType))

    }
    onResponse<No> {

    //TO DO:
    // should randomize another suggestion
    // but make sure to not give the same suggestions as already given...

    }
}


fun teachingVocabulary() : State = state(Options){
    onEntry {
        //Ask something like What is GREEN i Swedish
        furhat.ask("What is the word for... in Swedish")

    }

    /*
    TO DO:
    the user proposes answer which is recognized,
    the bot should say that it is correct
    otherwise let the user try again if they want
     */

}



fun registerVocabularyType(vocabularyType: VocabularyType) : State = state(Options) {
    onEntry {
        //storing the chosen vocabulary type on the user profile
        users.current.currentVocabularyType.vocabType = vocabularyType
        furhat.say("I am now ready to start teaching you ${users.current.currentVocabularyType.vocabType}")
        goto(teachingVocabulary())
    }
}

val IntroVocabulary : State = state(Options){
    onEntry {
        furhat.ask("Alright, are you ready to become a kick-ass Swedish speaker?")
    }
    onReentry {
        furhat.ask("Wanna practice some Swedish vocabulary?")
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
