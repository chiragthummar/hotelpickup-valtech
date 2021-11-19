package com.valtech.movenpick.util;

public final class HotelPickupConstants {

    public static final class DialogConstants {
        public static final int PROGRESS_DIALOG = 0;

        private DialogConstants() {
        }
    }

    public static final class GeneralConstants {
        public static final int ANIMATION_DURATION = 500;
        public static final String BARCODE = "barcode";
        public static final String EVENT_ID = "event.id";
        public static final String JAMMED_MESSAGE = "jammed.message";
        public static final String SETTINGS_PASSWORD = "password";
        public static final String SQLITE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

        public static final String MOBILE_NUM = "mobile.num";

        private GeneralConstants() {
        }
    }

    public static final class MessageConstants {
        public static final int MESSAGE_TYPE_ERROR = -1;
        public static final int MESSAGE_TYPE_FINISHED = 2320;
        public static final int MESSAGE_TYPE_MESSAGE = 1;
        public static final int MESSAGE_TYPE_ALERT = 2;
        private MessageConstants() {
        }
    }

    public static final class UserSettingsConstants {
        public static final int BACK_CAMERA = 0;
        public static final String DEFAULT_API_URL = "";
        public static final String DEFAULT_SMS_API_URL = "";
        public static final int FRONT_CAMERA = 1;
        public static final String KEY_API_URL = "api.url";
        public static final String KEY_CAMERA_TO_USE = "camera.to.use";
        public static final String KEY_MINIMUM_BARCODE_CHARACTERS = "min.barCode.Characters";
        public static final String KEY_SMS_API_URL = "sms.api.url";
        public static final String KEY_MOBILE_OPTIONAL = "is.mobile.optional";
        public static final String KEY_NFC_OPTIONAL = "is.nfc.optional";
        public static final String KEY_MODE = "is.mode";
        private UserSettingsConstants() {
        }
    }

    private HotelPickupConstants() {
    }
}
