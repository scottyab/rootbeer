package com.scottyab.rootbeer.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import android.util.Log;

public final class QLog {
    public static final int NONE = 0;
    public static final int ERRORS_ONLY = 1;
    public static final int ERRORS_WARNINGS = 2;
    public static final int ERRORS_WARNINGS_INFO = 3;
    public static final int ERRORS_WARNINGS_INFO_DEBUG = 4;
    public static final int ALL = 5;

    public static int LOGGING_LEVEL = ALL;

    /*
     * For filtering app specific output
     */
    private static final String TAG = "RootBeer";
    /*
     * So any important logs can be outputted in non filtered output also
     */
    private static final String TAG_GENERAL_OUTPUT = "QLog";

    static {
        //i("Log class reloaded");
    }

    /**
     * @param obj the object to log
     * @param cause
     *            The exception which caused this error, may not be null
     */
    public static void e(final Object obj, final Throwable cause) {
        if (isELoggable()) {
            Log.e(TAG, getTrace() + String.valueOf(obj));
            Log.e(TAG, getThrowableTrace(cause));
            Log.e(TAG_GENERAL_OUTPUT, getTrace() + String.valueOf(obj));
            Log.e(TAG_GENERAL_OUTPUT, getThrowableTrace(cause));
        }
    }

    public static void e(final Object obj) {
        if (isELoggable()) {
            Log.e(TAG, getTrace() + String.valueOf(obj));
            Log.e(TAG_GENERAL_OUTPUT, getTrace() + String.valueOf(obj));
        }
    }

    public static void w(final Object obj, final Throwable cause) {
        if (isWLoggable()) {
            Log.w(TAG, getTrace() + String.valueOf(obj));
            Log.w(TAG, getThrowableTrace(cause));
            Log.w(TAG_GENERAL_OUTPUT, getTrace() + String.valueOf(obj));
            Log.w(TAG_GENERAL_OUTPUT, getThrowableTrace(cause));
        }
    }

    public static void w(final Object obj) {
        if (isWLoggable()) {
            Log.w(TAG, getTrace() + String.valueOf(obj));
            Log.w(TAG_GENERAL_OUTPUT, getTrace() + String.valueOf(obj));
        }
    }

    public static void i(final Object obj) {
        if (isILoggable()) {
            Log.i(TAG, getTrace() + String.valueOf(obj));
        }
    }

    public static void d(final Object obj) {
        if (isDLoggable()) {
            Log.d(TAG, getTrace() + String.valueOf(obj));
        }
    }

    public static void v(final Object obj) {
        if (isVLoggable()) {
            Log.v(TAG, getTrace() + String.valueOf(obj));
        }
    }

    public static boolean isVLoggable() {
        return LOGGING_LEVEL > ERRORS_WARNINGS_INFO_DEBUG;
    }

    public static boolean isDLoggable() {
        return LOGGING_LEVEL > ERRORS_WARNINGS_INFO;
    }

    public static boolean isILoggable() {
        return LOGGING_LEVEL > ERRORS_WARNINGS;
    }

    public static boolean isWLoggable() {
        return LOGGING_LEVEL > ERRORS_ONLY;
    }

    public static boolean isELoggable() {
        return LOGGING_LEVEL > NONE;
    }

    private static String getThrowableTrace(final Throwable thr) {
        StringWriter b = new StringWriter();
        thr.printStackTrace(new PrintWriter(b));
        return b.toString();
    }

    private static String getTrace() {
        int depth = 2;
        Throwable t = new Throwable();
        StackTraceElement[] elements = t.getStackTrace();
        String callerMethodName = elements[depth].getMethodName();
        String callerClassPath = elements[depth].getClassName();
        int lineNo = elements[depth].getLineNumber();
        int i = callerClassPath.lastIndexOf('.');
        String callerClassName = callerClassPath.substring(i + 1);
        return callerClassName + ": " + callerMethodName + "() ["
                + lineNo + "] - ";
    }

    /**
     * Prints the stack trace to mubaloo log and standard log
     *
     * @param e the exception to log
     */
    public static void handleException(final Exception e) {
        QLog.e(e.toString());
        e.printStackTrace();
    }

    private QLog() {
    }
}
