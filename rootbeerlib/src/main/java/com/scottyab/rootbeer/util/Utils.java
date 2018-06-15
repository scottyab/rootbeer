package com.scottyab.rootbeer.util;

import java.lang.reflect.Method;

/**
 * Created by scottab on 30/06/15.
 */
public final class Utils {

    private Utils() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }
    /**
     * In Development - an idea of ours was to check the if selinux is enforcing - this could be disabled for some rooting apps
     * Checking for selinux mode
     *
     * @return true if selinux enabled
     */
    public static boolean isSelinuxFlagInEnabled() {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            String selinux = (String) get.invoke(c, "ro.build.selinux");
            return "1".equals(selinux);
        } catch (Exception ignored) {

        }
        return false;
    }
}
