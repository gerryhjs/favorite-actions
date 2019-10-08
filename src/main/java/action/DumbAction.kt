package action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.showOkCancelDialog


class DumbAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        showOkCancelDialog("Yes or no?", "Yes", "Ok", "Cancel")
    }
}