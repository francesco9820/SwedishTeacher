package furhatos.app.swedishteacher.nlu

import furhatos.app.swedishteacher.questions.QuestionSet
import furhatos.nlu.EnumEntity
import furhatos.nlu.EnumItem
import furhatos.nlu.Intent
import furhatos.util.Language



//Vocabulary type entity
class VocabularyType : EnumEntity(stemming = true, speechRecPhrases = true){
    override fun getEnum(lang: Language): List<String> {
        return listOf("colors", "clothing items", "numbers", "basics one", "basics two")
    }
}

class AnswerType : EnumEntity(stemming = true, speechRecPhrases = true){
    override fun getEnum(lang: Language): List<String> {
        return listOf("byxor", "fem", "grön")
        //return listOf("Big Sur", "fem", "grand")
    }
}

//intent for choosing a certain vocabulary type to practice
class ChooseVocabularyType(var vocabularyType : VocabularyType? = null): Intent(){
    override fun getExamples(lang: Language): List<String> {
        return listOf("@vocabularyType", "I want @vocabularyType", "I would like to practice @vocabularyType",
            "I want to learn @vocabularyType", "I want @vocabularyType", "Teach me @vocabularyType")
    }
}

//intent for requesting all available vocabulary types
class RequestVocabularyTypes: Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("What options do you have?",
            "Which vocabulary can I practice?",
            "What are the alternatives?",
            "What could I practice?",
            "Which words can you teach me?",
            "Which words do you know?",
            "What can you teach me?"
        )
    }
}

//change vocabulary type
class ChangeVocabularyTypes: Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("I would like to change vocabulary",
            "Can I choose another vocabulary?",
            "Is it possible to change vocabulary?",
            "Can we change vocabulary?",
            "Change vocabulary",
            "another vocabulary"
        )
    }
}
class AnswerOption : EnumEntity {

    var correct : Boolean = false

    // Every entity and intent needs an empty constructor.
    constructor() {
    }

    // Since we are overwriting the value, we need to use this custom constructor
    constructor(correct : Boolean, value : String) {
        this.correct = correct
        this.value = value
    }

    override fun getEnumItems(lang: Language): List<EnumItem> {
        return QuestionSet.current.options;
    }

}
//correct answers
class PossiblyCorrectAnswers(var correctAnswer : AnswerType? = null): Intent() {
    override fun getExamples(lang: Language): List<String> {
        /*return listOf("@correctAnswer",
            "The answer is @correctAnswer",
            "It is @correctAnswer"
        )*/
        return listOf("@correctAnswer")
    }
}

class RequestRepeatQuestion : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "what was the question",
            "can you repeat the question",
            "what was the question again"
        )
    }
}

class RequestRepeatOptions : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "what are the options",
            "can you repeat the options",
            "what was the options"
        )
    }
}


