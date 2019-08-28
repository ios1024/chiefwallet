package me.spark.mvvm.utils.language;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/7/3
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class MultiLanguage {

    private static LanguageLocalListener languageLocalListener;

    public static void init(LanguageLocalListener listener) {
        languageLocalListener = listener;
    }

    public static Context setLocal(Context context) {
        return updateResources(context, getSetLanguageLocale(context));
    }


    /**
     * 设置语言类型
     */
    public static void setApplicationLanguage(Context context) {
        Resources resources = context.getApplicationContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        Locale locale = getSetLanguageLocale(context);
        config.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);
            context.getApplicationContext().createConfigurationContext(config);
            Locale.setDefault(locale);
        }
        resources.updateConfiguration(config, dm);
    }


    /**
     * @param context
     */
    public static void onConfigurationChanged(Context context) {
        setLocal(context);
        setApplicationLanguage(context);
    }


    /**
     * 获取选择的语言
     *
     * @param context
     * @return
     */
    private static Locale getSetLanguageLocale(Context context) {
        if (languageLocalListener != null) {
            return languageLocalListener.getSetLanguageLocale(context);
        }
        return Locale.ENGLISH;
    }

    /**
     * 更新语言设置
     *
     * @param context
     * @param locale
     * @return
     */
    private static Context updateResources(Context context, Locale locale) {
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale);
            context = context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        return context;
    }

}
