package com.smartloan.smtrick.smart_loan.exception;

import android.util.Log;

public class ExceptionUtil {

    //*********************************************************
    //Generic dialog, takes in the method name and error message
    //*********************************************************
    public static void errorMessage(final String method, final String message, final Exception e) {
        logException(method, message, e);
    }

    public static void logException(final String tag, final String message, final Exception e) {
        Log.e(e.getClass().getName(), e.getMessage(), e.getCause());
    }

    private static void sendExceptionEmail(final String method, final String message, final Exception e) {
        //TODO: Implement send exception logic here and include gaurav.jha@microlucid.com, vimal.kumar@microlucid.com email id in that list
    }
}