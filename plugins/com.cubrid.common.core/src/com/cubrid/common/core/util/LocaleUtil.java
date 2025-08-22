package com.cubrid.common.core.util;

import java.util.Locale;

public class LocaleUtil {
    public static final LocaleEnum JVM_LOCALE;

    public static enum LocaleEnum {
        EN_US("en_US"),
        EN_UK("en_UK"),
        KM_KH("km_KH"),
        ZH_CN("zh_CN"),
        JP_JP("jp_JP"),
        KO_KR("ko_KR"),
        TR_TR("tr_TR");

        private String locale;

        private LocaleEnum(String locale) {
            this.locale = locale;
        }

        public static LocaleEnum fromString(String localeString) {
            if (localeString != null) {
                for (LocaleEnum localeEnum : LocaleEnum.values()) {
                    if (localeString.equalsIgnoreCase(localeEnum.locale)) {
                        return localeEnum;
                    }
                }
            }
            return null;
        }

        public String getLangName() {
            return this.locale;
        }
    };

    static {
        LocaleEnum locale = LocaleEnum.fromString(Locale.getDefault().toString());
        if (locale != null) {
            JVM_LOCALE = locale;
        } else {
            JVM_LOCALE = LocaleEnum.EN_US;
        }
    }
}
