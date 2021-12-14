package furhatos.app.swedishteacher.nlu


import furhatos.nlu.EnumEntity
import furhatos.nlu.Intent
import furhatos.util.Language


//Vocabulary type entity
class VocabularyType : EnumEntity(stemming = true, speechRecPhrases = true){
    override fun getEnum(lang: Language): List<String> {
        return listOf("colors", "clothing items", "numbers")
    }
}

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