package actions

import com.intellij.openapi.actionSystem.AnAction
import java.util.logging.Logger

class SelectActionAction : SelectActionBase() {
    override fun actionSelected(action: AnAction) {
        Logger.getGlobal().info(action.toString())
    }

}
