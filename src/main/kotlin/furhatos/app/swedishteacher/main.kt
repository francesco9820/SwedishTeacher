package furhatos.app.swedishteacher

import furhatos.app.swedishteacher.flow.*
import furhatos.skills.Skill
import furhatos.flow.kotlin.*

class SwedishteacherSkill : Skill() {
    override fun start() {
        Flow().run(Idle)
    }
}

fun main(args: Array<String>) {
    Skill.main(args)
}
