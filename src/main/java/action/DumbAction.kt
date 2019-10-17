package action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.showOkCancelDialog
import components.SavedActionsComponent


class DumbAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val savedActionsComponent =
                e.project?.getComponent(SavedActionsComponent::class.java)
        val currentVal = savedActionsComponent?.savedActionsData?.counter ?: 18
        showOkCancelDialog("Message", "Current value is " + currentVal, "Ok")
        savedActionsComponent?.savedActionsData?.counter = currentVal + 1
    }
}



