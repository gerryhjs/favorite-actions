package action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.ui.showOkCancelDialog
import services.SavedActionsService


class DumbAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val savedActionsComponent =
                e.project?.let {
                    ServiceManager.getService(it, SavedActionsService::class.java)
                }
        val currentVal = savedActionsComponent?.counter ?: 18
        showOkCancelDialog("Message", "Current value is " + currentVal, "Ok")
        savedActionsComponent?.counter = currentVal + 1
    }
}



