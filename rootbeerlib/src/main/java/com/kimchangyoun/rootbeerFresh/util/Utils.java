package com.kimchangyoun.rootbeerFresh.util;

import java.lang.reflect.Method;

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
        String selinux = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            selinux = (String) get.invoke(c, "ro.build.selinux");
        } catch (Exception ignored) {

        }

        return "1".equals(selinux);
    }
}
