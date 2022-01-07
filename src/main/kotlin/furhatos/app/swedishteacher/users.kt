package furhatos.app.swedishteacher.flow

import furhatos.app.swedishteacher.nlu.*
import furhatos.records.Record
import furhatos.records.User

class SkillData(
    var score : Int = 0,
    var lastScore : Int = 0,
    var interested : Boolean = true,
    var playing: Boolean = false,
    var played : Boolean = false,
    var questionsAsked : MutableList<String> = mutableListOf()
) : Record()

val User.quiz : SkillData
    get() = data.getOrPut(SkillData::class.qualifiedName, SkillData())


class ChosenVocabularyType (
    var vocabType : String = ""
)

class QuestionType (
    var qstType : String = ""
)

class UserData(
    var name : String = String()
)


val User.currentVocabularyType : ChosenVocabularyType
    get() = data.getOrPut(ChosenVocabularyType::class.qualifiedName, ChosenVocabularyType())

val User.information : UserData
    get() = data.getOrPut(UserData::class.qualifiedName, UserData())

val User.currentQuestionType : QuestionType
    get() = data.getOrPut(QuestionType::class.qualifiedName, QuestionType())