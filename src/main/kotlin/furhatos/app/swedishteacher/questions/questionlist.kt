package furhatos.app.swedishteacher.questions

/**
 * The questions are structured like
 *  -The question
 *  -The correct answer, followed by alternative pronounciations
 *  -A list of other answers followed by their alternatives
 */
val questionsEnglish = mutableListOf(
    Question("What is the word for blue in Swedish",
        answer = listOf("blå")),

    Question("What is the word for gray in Swedish",
        answer = listOf("grå")),

    Question("What is the word for pink in Swedish",
    answer = listOf("rosa"))
)
