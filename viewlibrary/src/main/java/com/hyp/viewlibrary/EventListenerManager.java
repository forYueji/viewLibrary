package com.hyp.viewlibrary;

import android.view.View;

import com.hyp.viewlibrary.annotation.EventBase;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

/**
 * @author: hyp
 * @date: 2018-01-07
 */
public class EventListenerManager {
    private static final DoubleKeyValueMap<ViewInjectInfo, Class<?>, Object> mListenerCache = new DoubleKeyValueMap();

    private EventListenerManager() {
    }

    public static void addEventMethod(ViewFinder finder, ViewInjectInfo info, Annotation eventAnnotation, Object handler, Method method) {
        try {
            View view = finder.findViewByInfo(info);
            if (view != null) {
                EventBase eventBase = eventAnnotation.annotationType().getAnnotation(EventBase.class);
                Class<?> listenerType = eventBase.listenerType();
                String listenerSetter = eventBase.listenerSetter();
                String methodName = eventBase.methodName();
                boolean addNewMethod = false;
                Object sListener = mListenerCache.get(info, listenerType);
                EventListenerManager.DynamicHandler dynamicHandler = null;
                if (sListener != null) {
                    dynamicHandler = (EventListenerManager.DynamicHandler) Proxy.getInvocationHandler(sListener);
                    addNewMethod = handler.equals(dynamicHandler.getHandler());
                    if (addNewMethod) {
                        dynamicHandler.addMethod(methodName, method);
                    }
                }

                if (!addNewMethod) {
                    dynamicHandler = new EventListenerManager.DynamicHandler(handler);
                    dynamicHandler.addMethod(methodName, method);
                    sListener = Proxy.newProxyInstance(listenerType.getClassLoader(), new Class[]{listenerType}, dynamicHandler);
                    mListenerCache.put(info, listenerType, sListener);
                }

                Method setEventListenerMethod = view.getClass().getMethod(listenerSetter, listenerType);
                setEventListenerMethod.invoke(view, sListener);
            }
        } catch (Throwable var14) {
            var14.fillInStackTrace();
        }

    }

    private static class DynamicHandler implements InvocationHandler {
        private WeakReference<Object> handlerRef;
        private final HashMap<String, Method> methodMap = new HashMap(1);

        public DynamicHandler(Object handler) {
            this.handlerRef = new WeakReference(handler);
        }

        public void addMethod(String name, Method method) {
            this.methodMap.put(name, method);
        }

        public Object getHandler() {
            return this.handlerRef.get();
        }

        public void setHandler(Object handler) {
            this.handlerRef = new WeakReference(handler);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object handler = this.handlerRef.get();
            if (handler != null) {
                String methodName = method.getName();
                method = (Method) this.methodMap.get(methodName);
                if (method != null) {
                    return method.invoke(handler, args);
                }
            }
            return null;
        }
    }
}
