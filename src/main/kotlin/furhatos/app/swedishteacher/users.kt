package furhatos.app.swedishteacher.flow

import furhatos.app.swedishteacher.nlu.*
import furhatos.records.User

class ChosenVocabularyType (
    var vocabType : VocabularyType = VocabularyType()
)

val User.currentVocabularyType : ChosenVocabularyType
    get() = data.getOrPut(ChosenVocabularyType::class.qualifiedName, ChosenVocabularyType())