package org.dandoh.favacts.services

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State;
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil
import org.dandoh.favacts.utils.isAction
import org.jdesktop.swingx.action.ActionManager
import java.util.*


typealias ActionId = String

@State(name = "SavedActionsData")
class FavoriteActionsService : PersistentStateComponent<FavoriteActionsService.Data>, DumbAware {

  companion object {
    fun getService(project: Project): FavoriteActionsService {
      return ServiceManager
          .getService(project, FavoriteActionsService::class.java)
    }
  }

  /**
   * Persistent
   */
  data class Data(var actionIds: MutableList<ActionId>) {
    constructor() : this(mutableListOf())
  }

  private val data = Data();

  override fun getState(): Data? {
    return data.copy()
  }

  override fun loadState(state: Data) {
    XmlSerializerUtil.copyBean(state, data);
    data.actionIds = data.actionIds.filter { isAction(it) }.toMutableList()
  }


  /**
   * Listeners
   */
  private val listeners = mutableSetOf<Listener>()

  interface Listener {
    fun actionListChange(newActionIds: List<ActionId>);
  }

  fun addListener(listener: Listener) {
    listeners.add(listener)
  }

  fun removeListener(listener: Listener) {
    if (listeners.contains(listener)) {
      listeners.remove(listener)
    }
  }

  private fun notifyListeners() {
    listeners.forEach { it.actionListChange(data.actionIds) }
  }

  /**
   * Service
   */
  fun addNewAction(actionId: ActionId) {
    if (!isAction(actionId)) return;
    // append to the list
    data.actionIds.add(0, actionId)
    // remove duplicate without changing order
    val set = TreeSet<ActionId>()
    data.actionIds = data.actionIds
        .filterNot { id ->
          (id in set).also { set.add(id) }
        }.toMutableList()

    notifyListeners()

  }

  fun getActionIds(): List<ActionId> = data.actionIds

}

