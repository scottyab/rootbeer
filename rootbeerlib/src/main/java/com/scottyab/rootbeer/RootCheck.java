package com.scottyab.rootbeer;

/**
 * Enum used by the {@link RootBeer#isRooted(RootCheck[])} method
 * <p>
 * Created on 02/07/2019 at 20:50
 *
 * @author Max Pilotto (github.com/maxpilotto, maxpilotto.com)
 */
public enum RootCheck {
    TEST_KEYS,
    ROOT_MANAGEMENT_APPS,
    DANGEROUS_APPS,
    POTENTIALLY_DANGEROUS_APPS,
    ROOT_CLOAKING_APPS,
    SU_BINARY,
    MAGISK_BINARY,
    BUSYBOX_BINARY,
    DANGEROUS_PROPS,
    RW_PATHS,
    NATIVE_ROOT,
    SU_PRESENCE;
}
