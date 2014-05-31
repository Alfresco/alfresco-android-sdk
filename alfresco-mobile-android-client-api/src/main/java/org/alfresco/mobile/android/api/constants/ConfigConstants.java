package org.alfresco.mobile.android.api.constants;

import java.util.ArrayList;
import java.util.List;

public interface ConfigConstants
{
    // ///////////////////////////////////////////////////////////////////////////
    // VERSION
    // ///////////////////////////////////////////////////////////////////////////

    // ///////////////////////////////////////////////////////////////////////////
    // DATA DICTIONNARY
    // ///////////////////////////////////////////////////////////////////////////
    String DATA_DICTIONNARY = "Data Dictionary";

    String DATA_DICTIONNARY_MOBILE_CONFIG_PATH = "Mobile/config.json";

    @SuppressWarnings("serial")
    List<String> DATA_DICTIONNARY_LIST = new ArrayList<String>(8)
    {
        {
            add("Data Dictionary");// UK,JA
            add("Dictionnaire de données");// FR
            add("Datenverzeichnis");// DE
            add("Diccionario de datos");// ES
            add("Dizionario dei dati");// IT
            add("Dataordbok");// Nb NO
            add("Gegevenswoordenboek");// NL
            add("Dicionário de dados");// PT
        }
    };

    // ///////////////////////////////////////////////////////////////////////////
    // BETA MODEL
    // ///////////////////////////////////////////////////////////////////////////
    String DATA_DICTIONNARY_MOBILE_PATH = "Mobile/configuration.json";

    String CATEGORY_ROOTMENU = "rootMenu";

    String MENU_ACTIVITIES = "com.alfresco.activities";

    String MENU_REPOSITORY = "com.alfresco.repository";

    String MENU_SITES = "com.alfresco.sites";

    String MENU_TASKS = "com.alfresco.tasks";

    String MENU_FAVORITES = "com.alfresco.favorites";

    String MENU_SEARCH = "com.alfresco.search";

    String MENU_LOCAL_FILES = "com.alfresco.localFiles";

    String MENU_NOTIFICATIONS = "com.alfresco.notifications";

    String MENU_SHARED = "com.alfresco.repository.shared";

    String MENU_MYFILES = "com.alfresco.repository.userhome";

    String PROP_VISIBILE = "visible";

    
    // ///////////////////////////////////////////////////////////////////////////
    // DEFAULT VIEW
    // ///////////////////////////////////////////////////////////////////////////
    
    String VIEW_ROOT_NAVIGATION_MENU = "rootNavigationMenu";

    // ///////////////////////////////////////////////////////////////////////////
    // PARSING
    // ///////////////////////////////////////////////////////////////////////////
    // GENERAL
    String ID_VALUE = "id";

    String TYPE_VALUE = "type";

    String LABEL_ID_VALUE = "label-id";

    // CONFIG INFO
    String SCHEMA_VERSION_VALUE = "schema-version";

    String SERVICE_VERSION_VALUE = "service-version";

    // VIEWS
    String VIEWS_VALUE = "views";
    
    String PARAMS_VALUE = "params";

    String VISIBILITY_VALUE = "visibility";

    String VISIBLE_VALUE = "visible";
    


}
