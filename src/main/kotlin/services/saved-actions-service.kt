package services

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage
import com.intellij.util.xml.Attribute
import com.intellij.util.xmlb.XmlSerializerUtil
import java.util.logging.Logger

class SavedActionsData {
  var actionIds: List<String> = listOf()
}

@State(name = "SavedActionsData", storages = [Storage("SavedActionsData.xml")])
class FavoriteActionsService : PersistentStateComponent<SavedActionsData> {
  var savedActionsData = SavedActionsData();
  override fun getState(): SavedActionsData? {
    return savedActionsData
  }

  override fun loadState(state: SavedActionsData) {
    XmlSerializerUtil.copyBean(state, savedActionsData);
  }
}

