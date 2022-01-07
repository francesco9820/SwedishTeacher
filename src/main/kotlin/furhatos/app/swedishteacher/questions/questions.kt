package furhatos.app.swedishteacher.questions

import furhatos.app.swedishteacher.nlu.AnswerOption
import furhatos.nlu.EnumItem
import furhatos.nlu.TextBuilder
import java.util.*

object QuestionSet {

    var count : Int = 0
    var current: Question = questionsEnglish[Random().nextInt(questionsEnglish.lastIndex)]

    init {
        questionsEnglish.shuffle()
    }

    fun next(theme: String) {
        do {
            count++
            if (count >= questionsEnglish.size)
                count = 0
            current = questionsEnglish[count]
            AnswerOption().forget()
        }while (current.theme!=theme)

    }

}

/**
 * The question class gets the following parameters:
 * @text : The question as a String
 * @answer : A list containing the correct answer to the question, followed by alternative pronunciations of the correct answer
 * @alternatives A list, containing lists of other (wrong) answers. Every other answer is also followed by alternative pronunciations of the correct answer.
 */
class Question(val text: String, answer: List<String>, theme:String) {

    //All options, used to prime the NLU
    var options : MutableList<EnumItem> = mutableListOf()
    //Only the first option of the answers, these are correctly spelled, and not alternative.
    var primeoptions : MutableList<EnumItem> = mutableListOf()

    var theme = theme


    //init loads the first item of the list into primeoptions
    //And loads everything into options
    init {
        primeoptions.add(EnumItem(AnswerOption(true, answer.first()), answer.first()))
        answer.forEach {
            options.add(EnumItem(AnswerOption(true, it), it))
        }


        options.shuffle()
        primeoptions.shuffle()
    }

    //Returns the well formatted answer options
    fun getOptionsString() : String {
        var text = TextBuilder()
        text.appendList(primeoptions.map { it.wordString }, "or")
        return text.toString()
    }


    //Returns the well formatted answer options
    val speechPhrases : List<String>
        get() = primeoptions.map { it.wordString ?: "" }

}

