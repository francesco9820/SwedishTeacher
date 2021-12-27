package furhatos.app.swedishteacher.flow

import furhatos.app.swedishteacher.nlu.*
import furhatos.records.User

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