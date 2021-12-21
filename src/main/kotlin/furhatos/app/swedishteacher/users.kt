package furhatos.app.swedishteacher.flow

import furhatos.app.swedishteacher.nlu.*
import furhatos.records.User

class ChosenVocabularyType (
    var vocabType : String = ""
)

class UserData(
    var name : String = String()
)


val User.currentVocabularyType : ChosenVocabularyType
    get() = data.getOrPut(ChosenVocabularyType::class.qualifiedName, ChosenVocabularyType())

val User.information : UserData
    get() = data.getOrPut(UserData::class.qualifiedName, UserData())