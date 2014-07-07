package org.alfresco.mobile.android.api.constants;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.mobile.android.api.model.config.EvaluatorType;

public interface ConfigConstants
{
    // ///////////////////////////////////////////////////////////////////////////
    // VERSION
    // ///////////////////////////////////////////////////////////////////////////

    // ///////////////////////////////////////////////////////////////////////////
    // DATA DICTIONNARY
    // ///////////////////////////////////////////////////////////////////////////
    String DATA_DICTIONARY = "Data Dictionary";

    String CONFIG_APPLICATION_FOLDER_PATH = "Mobile/%s";

    String CONFIG_FILENAME = "config.json";

    String CONFIG_LOCALIZATION_FOLDER_PATH = "Messages/";

    String CONFIG_LOCALIZATION_FILENAME = "strings.properties";

    String CONFIG_LOCALIZATION_FILENAME_PATTERN = "strings_%s.properties";

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

    String VIEW_NODE_PROPERTIES = "view-properties";
    
    String VIEW_EDIT_PROPERTIES = "edit-properties";

    // ///////////////////////////////////////////////////////////////////////////
    // PARSING
    // ///////////////////////////////////////////////////////////////////////////
    // GENERAL
    String ID_VALUE = "id";

    String TYPE_VALUE = "type";

    String LABEL_ID_VALUE = "label-id";

    String DESCRIPTION_ID_VALUE = "description-id";

    String ICON_ID_VALUE = "icon-id";

    // PROFILES
    String PROFILES_VALUE = "profiles";

    String DEFAULT_VALUE = "default";

    // CONFIG INFO
    String SCHEMA_VERSION_VALUE = "schema-version";

    String CONFIG_VERSION_VALUE = "config-version";

    // VIEWS
    String ITEMS_VALUE = "items";

    String ITEM_TYPE_VALUE = "item-type";

    String VIEW_VALUE = "view";

    String VIEWS_VALUE = "views";

    String PARAMS_VALUE = "params";

    String VISIBILITY_VALUE = "visibility";

    String VISIBLE_VALUE = "visible";

    // EVALUATORS
    String EVALUATOR = "evaluator";

    String MATCH_ALL_VALUE = "matchAll";

    String MATCH_ANY_VALUE = "matchAny";

    String NEGATE_SYMBOL = "!";

    // EVALUATOR REPOSITORY VERSION
    String OPERATOR_VALUE = "operator";

    String EDITION_VALUE = "edition";

    String MAJORVERSION_VALUE = "majorVersion";

    String MINORVERSION_VALUE = "minorVersion";

    String MAINTENANCEVERSION_VALUE = "maintenanceVersion";

    // FORMS
    String PARAMS_FORMS = "forms";

    String FIELD_GROUPS_VALUE = "field-groups";

    String FIELDS_VALUE = "fields";
    
    String FIELD_VALUE = "field";

    String CONTROL_TYPE_VALUE = "control-type";

    String CONTROL_PARAMS_VALUE = "control-params";
    
    String MODEL_ID_VALUE = "model-id"; 
    
    String LAYOUT_VALUE = "layout";

    // REPOSITORY
    String SHARE_URL_VALUE = "share-url";

    String CMIS_URL_VALUE = "cmis-url";

    // CREATION
    String MIME_TYPES_VALUE = "mime-types";

    String DOCUMENT_TYPES_VALUE = "document-types";

    String FOLDER_TYPES_VALUE = "folder-types";

    String ROOTVIEW_ID_VALUE = "root-view-id";

    // /////////////////////////////////////////////////
    // PARSING ENUM
    // /////////////////////////////////////////////////
    enum ViewConfigType
    {
        VIEW_ID("view-id"), VIEW_GROUP("view-group-id"), VIEW("view");

        /** The value associated to an enum. */
        private final String value;

        /**
         * Instantiates a new property type.
         * 
         * @param v the value of the enum.
         */
        ViewConfigType(String v)
        {
            value = v;
        }

        /**
         * Value.
         * 
         * @return the string
         */
        public String value()
        {
            return value;
        }

        /**
         * From value.
         * 
         * @param v the value of the enum.
         * @return the property type
         */
        public static ViewConfigType fromValue(String v)
        {
            for (ViewConfigType c : ViewConfigType.values())
            {
                if (c.value.equalsIgnoreCase(v)) { return c; }
            }
            return null;
        }
    }

    // /////////////////////////////////////////////////
    // PARSING ENUM
    // /////////////////////////////////////////////////
    enum FieldConfigType
    {
        FIELD_ID("field-id"), FIELD_GROUP_ID("field-group-id"), FIELD_GROUP("field-group"), FIELD("field");

        /** The value associated to an enum. */
        private final String value;

        /**
         * Instantiates a new property type.
         * 
         * @param v the value of the enum.
         */
        FieldConfigType(String v)
        {
            value = v;
        }

        /**
         * Value.
         * 
         * @return the string
         */
        public String value()
        {
            return value;
        }

        /**
         * From value.
         * 
         * @param v the value of the enum.
         * @return the property type
         */
        public static FieldConfigType fromValue(String v)
        {
            for (FieldConfigType c : FieldConfigType.values())
            {
                if (c.value.equalsIgnoreCase(v)) { return c; }
            }
            return null;
        }
    }

}
