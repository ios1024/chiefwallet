package com.spark.chiefwallet.util;


import com.spark.chiefwallet.ui.toast.Toasty;

import me.spark.mvvm.utils.*;

public class CheckErrorUtil {

    public static void checkError(EventBean eventBean) {
        String message = eventBean.getMessage();
        if (StringUtils.isNotEmpty(message)) {
            Toasty.showError(eventBean.getMessage());
        } else {
            Toasty.showError(eventBean.getCode() + eventBean.getMessage());
        }
    }
}
