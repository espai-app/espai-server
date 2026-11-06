/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */

package app.espai.sdk.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;


/**
 * Enum for fieldType.
 */
@Schema(name="FieldType",
        description = "Represents the type of a production's custom meta data field.")
public enum FieldType {

    TEXT,
    MULTILINE,
    RICHTEXT,
    INTEGER,
    DECIMAL,
    TAG,
    BOOLEAN,
    DATE,
    DATETIME

}