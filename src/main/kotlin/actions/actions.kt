package actions

import com.intellij.icons.AllIcons.General
import com.intellij.ide.script.IDE
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.ProjectManager
import services.FavoriteActionsService
import utils.IDELOG
import java.util.logging.Logger

class AddActionToFavoritesAction : AnAction("Add Action to Favorites",
    "Select an IDE action and add it to favorite actions",
    General.Add) {
  private val actionManager = ActionManager.getInstance()

  override fun actionPerformed(e: AnActionEvent) {
    val project = e.project
    selectIDEAction(e) { action ->
      val actionId = actionManager.getId(action)
      project?.let { FavoriteActionsService.getService(it) }
          ?.addNewAction(actionId)
    }
  }

}
