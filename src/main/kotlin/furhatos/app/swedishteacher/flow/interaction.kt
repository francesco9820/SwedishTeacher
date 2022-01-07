package furhatos.app.swedishteacher.flow

import cc.mallet.util.CommandOption
import furhatos.app.swedishteacher.questions.QuestionSet
import furhatos.nlu.common.*
import furhatos.app.swedishteacher.nlu.*
import furhatos.flow.kotlin.*
import furhatos.gestures.Gestures
import furhatos.skills.emotions.UserGestures
import furhatos.util.Gender
import furhatos.util.Language
import java.io.File

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
        val vocabTypeString = vocabType.toString().toLowerCase()
        if (vocabType != null) {
            random(
                {furhat.say("${vocabType.text}, that is a decent choice.")},
                {furhat.say("Nice, I am pretty good at ${vocabType.text}!")},
                {furhat.say("Oh wow, ${vocabType.text} you say. Let's go for it")}
            )
            goto(registerVocabularyType(vocabTypeString))
        }
        else {
            propagate()
        }
    }
    onResponse<DontKnow> {
        furhat.ask("What type of Swedish vocabulary do you want to practice?")
    }

    /*
    TO DO:
    Allow the user to change to vocabulary type during learning
    */
    onResponse<ChangeVocabularyTypes> {
        goto(vocabularyTypeRecommendation(""))
    }



    //If the user wants to know what vocabulary words are available
    onResponse<RequestVocabularyTypes> {
        var fileContents = readFileAsLinesUsingReadLines("Video_Chat_Emotion_Capture/predicted_emotion.txt")
        var emotion = fileContents.last()
        if(emotion.equals("fear")){
            furhat.say("There is no need to be scared")
        }else if(emotion.equals("sad")){
            furhat.say("Don't be sad you should be happy to practise Swedish with me")
        }else if(emotion.equals("happy")){
            furhat.say("Seeing you happy makes me really happy as well!")
        }
        furhat.say("I know so many words")
        furhat.say("We could practice ${VocabularyType().getEnum(Language.ENGLISH_US).joinToString(", ")}")
        furhat.ask("Which one would you want to practice?")
    }
}

fun vocabularyTypeRecommendation(previous: String) : State = state(Options){
    var vocType : String = ""
    onEntry {
        do{
            vocType = VocabularyType().getEnum(Language.ENGLISH_US).random()
        }while (vocType.equals(previous))
        furhat.say("We could practice ${vocType}")
        furhat.ask("Would you like that?")

    //TO DO: should also make sure to not give a suggestion that the user
    // is currently learning or has learned

    }
    onUserGesture(UserGestures.Smile) {
        furhat.say("It looks like you are happy with this choice")
        goto(registerVocabularyType(vocType))
    }
    onResponse<Yes> {

    //TO DO:
    // should take the suggestion and send it to the function below
        goto(registerVocabularyType(vocType))

    }
    onResponse<No> {
        goto(vocabularyTypeRecommendation(vocType))
    }
}

val AskQuestion: State = state(Options) {
    var failedAttempts = 0

    onEntry {
        failedAttempts = 0

        // Set speech rec phrases based on the current question's answers
        furhat.setInputLanguage(Language.SWEDISH)
        furhat.setSpeechRecPhrases(QuestionSet.current.speechPhrases)

        // Ask the question followed by the options
        furhat.ask(QuestionSet.current.text)
    }

    // Here we re-state the question
    onReentry {
        failedAttempts = 0
        furhat.ask("The question was, ${QuestionSet.current.text}")
    }

    // User is answering with any of the alternatives
    onResponse<AnswerOption> {
        val answer = it.intent
        println(it.intent)
        // If the user answers correct, we up the user's score and congratulates the user
        if (answer.correct) {
            println(answer.correct)
            furhat.gesture(Gestures.Smile)
            users.current.quiz.score++
            println(users.current.quiz.score)
            random(
                { furhat.say("Great! That was the right answer, you now have a score of ${users.current.quiz.score}") },
                { furhat.say("that was correct you now have a score of ${users.current.quiz.score}") }
            )
            /*
            If the user answers incorrect, we give another user the chance of answering if one is present in the game.
            If we indeed ask another player, the furhat.ask() interrupts the rest of the handler.
             */
        } else {
            furhat.gesture(Gestures.BrowFrown)
            furhat.say("Sorry, that was not correct")

            // Keep track of what users answered what question so that we don't ask the same user
            users.current.quiz.questionsAsked.add(QuestionSet.current.text)


        }

        // Check if the game has ended and if not, goes to a new question
        if (++rounds >= maxRounds) {
            furhat.say("That was the last question")
            goto(endSession())
        } else {
            goto(NewQuestion)
        }
    }

    // The users answers that they don't know
    onResponse<DontKnow> {
        furhat.say("Too bad. Here comes the next question")
        goto(NewQuestion)
    }

    onResponse<RequestRepeat> {
        reentry()
    }

    onResponse<RequestRepeatQuestion> {
        furhat.gesture(Gestures.BrowRaise)
        furhat.ask(QuestionSet.current.text)
    }

    // The user wants to hear the options again
    onResponse<RequestRepeatOptions> {
        furhat.gesture(Gestures.Surprise)
        random(
            { furhat.ask("They are ${QuestionSet.current.getOptionsString()}") },
            { furhat.ask(QuestionSet.current.getOptionsString()) }
        )
    }

    // If we don't get any response, we assume the user was too slow
    onNoResponse {
        random(
            { furhat.say("Too slow! Here comes the next question") },
            { furhat.say("A bit too slow amigo! Get ready for the next question") }
        )
        goto(NewQuestion)
    }

    /* If we get a response that doesn't map to any alternative or any of the above handlers,
        we track how many times this has happened in a row and give them two more attempts and
        finally moving on if we still don't get it.
     */
    onResponse {
        failedAttempts++
        when (failedAttempts) {
            1 -> furhat.ask("I didn't get that, sorry. Try again!")
            2 -> {
                furhat.say("Sorry, I still didn't get that")
                furhat.ask("The right answer is ${QuestionSet.current.getOptionsString()}")
            }
            else -> {
                furhat.say("Still couldn't get that. Let's try a new question")
                goto(NewQuestion)
            }
        }
    }
}

val NewQuestion : State = state(Options) {
    onEntry {


        if (!users.current.isAttendingFurhat) {
            furhat.say {
                random {
                    block {
                        +"But then I do want you to pay attention"
                        +Gestures.BigSmile
                    }
                    +"Look at me, I'm captain now"
                    +"Could you pay some attention to me"
                }
            }
        }
        // Ask new question
        println(users.current.currentVocabularyType.vocabType)
        QuestionSet.next(users.current.currentVocabularyType.vocabType)
        goto(AskQuestion)
    }
}
/*
fun teachingVocabulary(attempt: Int) : State = state(Options){
    var a = attempt
    onEntry {
        //Ask something like What is GREEN i Swedish
        var teacVoc = users.current.currentVocabularyType.vocabType
        if(teacVoc.equals("colors")){
            users.current.currentQuestionType.qstType = teacVoc
            furhat.setInputLanguage(Language.SWEDISH)
            //println(teacVoc)
            furhat.ask("You have ${2-a} attempts left. What is the word for green in Swedish")
        }else if(teacVoc.equals("clothing items") || teacVoc.equals("clothing item")){
            users.current.currentQuestionType.qstType = teacVoc
            furhat.setInputLanguage(Language.SWEDISH)
            furhat.ask("You have ${2-a} attempts left. What is the word for pants in Swedish")
        }else if(teacVoc.equals("numbers")){
            users.current.currentQuestionType.qstType = teacVoc
            furhat.setInputLanguage(Language.SWEDISH)
            furhat.ask("You have ${2-a} attempts left. What is the word for five in Swedish")
        }
        //furhat.setInputLanguage(Language.SWEDISH)
    }

    onResponse<ChangeVocabularyTypes> {
        goto(vocabularyTypeRecommendation(""))
    }

    onResponse<PossiblyCorrectAnswers> {
        val answer = it.intent.correctAnswer?.text
        val questionType = users.current.currentQuestionType.qstType
        if(answer != null){
            if(questionType.equals("colors") && answer.equals("grön")){
                furhat.say("Yeah that's correct!!")
                goto(endSession())
            }else if(questionType.equals("clothing items") && answer.equals("byxor")){
                furhat.say("Yeah that's correct!!")
                goto(endSession())
            }else if(questionType.equals("numbers") && answer.equals("fem")){
                furhat.say("Yeah that's correct!!")
                goto(endSession())
            }else if(a==2 && questionType.equals("colors")){
                furhat.say("The total attempts available are terminated. The correct word for green in Swedish is")
                furhat.setVoice(Language.SWEDISH, Gender.MALE)
                furhat.say("grön")
                furhat.setVoice(Language.ENGLISH_US, Gender.MALE)
                goto(endSession())
            }else if(a==2 && questionType.equals("clothing items")){
                furhat.say("The total attempts available are terminated. The correct word for pants in Swedish is")
                furhat.setVoice(Language.SWEDISH, Gender.MALE)
                furhat.say("byxor")
                furhat.setVoice(Language.ENGLISH_US)
                goto(endSession())
            }else if(a==2 && questionType.equals("numbers")){
                furhat.say("The total attempts available are terminated. The correct word for five in Swedish is")
                furhat.setVoice(Language.SWEDISH, Gender.MALE)
                furhat.say("fem")
                furhat.setVoice(Language.ENGLISH_US, Gender.MALE)
                goto(endSession())
            }
            else if(a<2){
                a++
                furhat.setInputLanguage(Language.ENGLISH_US)
                furhat.ask("The answer is incorrect. Let's try another time, is that okay for you?")
            }
        }

    }

    onResponse<Yes> {
       // goto(teachingVocabulary(a))
        goto(NewQuestion)
    }

    onResponse<No> {
        goto(endSession())
    }

    /*
    TO DO:
    the user proposes answer which is recognized,
    the bot should say that it is correct
    otherwise let the user try again if they want
     */
}

 */

fun endSession() : State = state(Options) {
    onEntry {
        furhat.say("We had a lot of fun together. This is the end of our session. See you another time!")
        furhat.setInputLanguage(Language.ENGLISH_US)
        goto(Idle)
    }
}


fun registerVocabularyType(vocabularyType: String) : State = state(Options) {
    onEntry {
        //storing the chosen vocabulary type on the user profile
        users.current.currentVocabularyType.vocabType = vocabularyType
        println( users.current.currentVocabularyType.vocabType)
        furhat.say("I am now ready to start teaching you ${users.current.currentVocabularyType.vocabType}")
        goto(NewQuestion)
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
        var fileContents = readFileAsLinesUsingReadLines("Video_Chat_Emotion_Capture/predicted_emotion.txt")
        var emotion = fileContents.last()
        if(emotion.equals("fear")){
            furhat.say("There is no need to be scared")
        }else if(emotion.equals("sad")){
            furhat.say("Don't be sad you should be happy to practise Swedish with me")
        }else if(emotion.equals("happy")){
            furhat.say("Seeing you happy makes me really happy as well!")
        }
        furhat.ask("What may I call you?")
    }
    onResponse<PersonName> {
        var name = it.intent?.value
        if (name != null) {
            users.current.information.name = name
        }
        furhat.say("You have a lovely name " + name)
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

fun readFileAsLinesUsingReadLines(fileName: String): List<String>
        = File(fileName).readLines()
