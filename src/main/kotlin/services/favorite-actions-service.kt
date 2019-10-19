package services

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State;
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil
import java.util.*
import kotlin.collections.ArrayList


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
  class Data {
    var actionIds: MutableList<ActionId> = mutableListOf()
  }

  private val data = Data();

  override fun getState(): Data? {
    return data
  }

  override fun loadState(state: Data) {
    XmlSerializerUtil.copyBean(state, data);
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
    // append to the list
    data.actionIds.add(0, actionId)
    // remove duplicate without changing order
    val set = TreeSet<ActionId>()
    data.actionIds = data.actionIds
        .filterNot {
          val exist = set.contains(it)
          set.add(actionId)
          exist
        }.toMutableList()

    notifyListeners()

  }

  fun getActionIds(): List<ActionId> = data.actionIds

}

