package actions

import com.intellij.openapi.actionSystem.AnAction
import java.util.logging.Logger

class SelectActionAction : AbstractSelectActionAction() {
    override fun actionSelected(action: AnAction) {
        Logger.getGlobal().info(action.toString())
    }

}



