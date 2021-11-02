package org.dandoh.favacts.actions

import com.intellij.icons.AllIcons.Actions
import com.intellij.icons.AllIcons.General
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.ui.popup.JBPopupFactory
import icons.PluginIcons
import org.dandoh.favacts.services.FavoriteActionsService
import org.dandoh.favacts.toolwindows.FavoriteActionsToolWindowFactory

class AddActionToFavoritesAction : AnAction("Add _Action to Favorites",
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


class ShowFavoriteActionsAction : AnAction("_Show Favorite Actions",
    "Show the list of Favorite Actions", PluginIcons.ICON_HEART) {
  override fun actionPerformed(e: AnActionEvent) {
    val project = e.project ?: return
    val service = FavoriteActionsService.getService(project)
    val actionManager = ActionManager.getInstance()
    val actions = service.getActionIds().map { actionManager.getAction(it) }

    val actionGroup = DefaultActionGroup().also {
      it.addAll(actions)
    }
    JBPopupFactory.getInstance()
        .createActionGroupPopup("Favorite Actions",
            actionGroup, e.dataContext,
            JBPopupFactory.ActionSelectionAid.SPEEDSEARCH, false
        ).showCenteredInCurrentWindow(project)
  }

}


class DeleteActionFromFavoritesAction : AnAction("Delete Action from Favorites",
    "Delete the selected IDE action", General.Remove) {
  override fun actionPerformed(e: AnActionEvent) {
    val project = e.project ?: return
    val favoriteActionsToolWindow = FavoriteActionsToolWindowFactory.favoriteActionsToolWindow ?: return
    val actionId = favoriteActionsToolWindow.getSelectedActionId() ?: return
    val service = FavoriteActionsService.getService(project)
    service.removeAction(actionId)
  }

}
