package com.icm.projeto.vitalpaint.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by Bruno Silva on 21/11/2017.
 */

public interface LobbyDataListener {
    public void onLobbyListChange(List<Map<String, String>> lobbyList);

}