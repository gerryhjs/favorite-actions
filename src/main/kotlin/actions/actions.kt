package actions

import com.intellij.icons.AllIcons.General
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import services.FavoriteActionsService

class AddActionToFavoritesAction : AnAction("Add Action to Favorites",
    "Select an IDE action and add it to favorite actions",
    General.Add) {

  override fun actionPerformed(e: AnActionEvent) {
    val actionManager = ActionManager.getInstance()
    val project = e.project ?: return
    selectIDEAction(e) { action ->
      val actionId = actionManager.getId(action)
      FavoriteActionsService.getService(project).addNewAction(actionId)
    }
  }

}
