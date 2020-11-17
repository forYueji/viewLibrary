package com.hyp.viewlibrary;

import android.app.Activity;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.view.View;

import com.hyp.viewlibrary.annotation.ContentView;
import com.hyp.viewlibrary.annotation.EventBase;
import com.hyp.viewlibrary.annotation.PreferenceInject;
import com.hyp.viewlibrary.annotation.ResInject;
import com.hyp.viewlibrary.annotation.ViewInject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author: hyp
 * @date: 2018-01-07
 */
public final class ViewUtils {

    private ViewUtils() {
    }

    public static void inject(View view) {
        injectObject(view, new ViewFinder(view));
    }

    public static void inject(Activity activity) {
        injectObject(activity, new ViewFinder(activity));
    }

    public static void inject(PreferenceActivity preferenceActivity) {
        injectObject(preferenceActivity, new ViewFinder(preferenceActivity));
    }

    public static void inject(Object handler, View view) {
        injectObject(handler, new ViewFinder(view));
    }

    public static void inject(Object handler, Activity activity) {
        injectObject(handler, new ViewFinder(activity));
    }

    public static void inject(Object handler, PreferenceGroup preferenceGroup) {
        injectObject(handler, new ViewFinder(preferenceGroup));
    }

    public static void inject(Object handler, PreferenceActivity preferenceActivity) {
        injectObject(handler, new ViewFinder(preferenceActivity));
    }

    private static void injectObject(Object handler, ViewFinder finder) {
        Class<?> handlerType = handler.getClass();
        ContentView contentView = handlerType.getAnnotation(ContentView.class);
        if (contentView != null) {
            try {
                Method setContentViewMethod = handlerType.getMethod("setContentView", Integer.TYPE);
                setContentViewMethod.invoke(handler, contentView.value());
            } catch (Throwable var28) {
                var28.printStackTrace();
            }
        }

        Field[] fields = handlerType.getDeclaredFields();
        int fieldsLength;
        if (fields.length > 0) {
            fieldsLength = fields.length;
            for (int cFields = 0; cFields < fieldsLength; ++cFields) {
                Field field = fields[cFields];
                ViewInject viewInject = field.getAnnotation(ViewInject.class);
                if (viewInject != null) {
                    try {
                        View view = finder.findViewById(viewInject.value(), viewInject.parentId());
                        if (view != null) {
                            field.setAccessible(true);
                            field.set(handler, view);
                        }
                    } catch (Throwable throwable) {
                        throwable.fillInStackTrace();
                    }
                } else {
                    ResInject resInject = field.getAnnotation(ResInject.class);
                    if (resInject != null) {
                        try {
                            Object res = ResLoader.loadRes(resInject.type(), finder.getContext(), resInject.id());
                            if (res != null) {
                                field.setAccessible(true);
                                field.set(handler, res);
                            }
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    } else {
                        PreferenceInject preferenceInject = field.getAnnotation(PreferenceInject.class);
                        if (preferenceInject != null) {
                            try {
                                Preference preference = finder.findPreference(preferenceInject.value());
                                if (preference != null) {
                                    field.setAccessible(true);
                                    field.set(handler, preference);
                                }
                            } catch (Throwable var25) {
                                var25.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        Method[] methods = handlerType.getDeclaredMethods();
        if (methods != null && methods.length > 0) {
            int methodsLength = methods.length;

            for (fieldsLength = 0; fieldsLength < methodsLength; ++fieldsLength) {
                Method method = methods[fieldsLength];
                Annotation[] annotations = method.getDeclaredAnnotations();
                if (annotations != null && annotations.length > 0) {
                    for (Annotation annotation : annotations) {
                        Class<?> annType = annotation.annotationType();
                        if (annType.getAnnotation(EventBase.class) != null) {
                            method.setAccessible(true);
                            try {
                                Method valueMethod = annType.getDeclaredMethod("value");
                                Method parentIdMethod = null;

                                try {
                                    parentIdMethod = annType.getDeclaredMethod("parentId");
                                } catch (Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                                Object values = valueMethod.invoke(annotation);
                                Object parentIds = parentIdMethod == null ? null : parentIdMethod.invoke(annotation);
                                int parentIdsLen = parentIds == null ? 0 : Array.getLength(parentIds);
                                int sLen = Array.getLength(values);
                                for (int index = 0; index < sLen; ++index) {
                                    ViewInjectInfo info = new ViewInjectInfo();
                                    info.value = Array.get(values, index);
                                    info.parentId = parentIdsLen > index ? (Integer) Array.get(parentIds, index) : 0;
                                    EventListenerManager.addEventMethod(finder, info, annotation, handler, method);
                                }
                            } catch (Throwable throwable) {
                                throwable.fillInStackTrace();
                            }
                        }
                    }
                }
            }
        }

    }
}
