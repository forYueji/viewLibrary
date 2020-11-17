package com.hyp.viewlibrary;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: hyp
 * @date: 2018-01-07
 */
public final class DoubleKeyValueMap<K1, K2, V> {

    private ConcurrentHashMap<K1, ConcurrentHashMap<K2, V>> concurrentHashMap = new ConcurrentHashMap();

    public void put(K1 info, K2 type, V value) {
        if (info != null && type != null && value != null) {
            ConcurrentHashMap sMap;
            if (this.concurrentHashMap.containsKey(info)) {
                sMap = (ConcurrentHashMap) this.concurrentHashMap.get(info);
                if (sMap != null) {
                    sMap.put(type, value);
                } else {
                    sMap = new ConcurrentHashMap();
                    sMap.put(type, value);
                    this.concurrentHashMap.put(info, sMap);
                }
            } else {
                sMap = new ConcurrentHashMap();
                sMap.put(type, value);
                this.concurrentHashMap.put(info, sMap);
            }

        }
    }

    public Set<K1> getFirstKeys() {
        return this.concurrentHashMap.keySet();
    }

    public ConcurrentHashMap<K2, V> get(K1 key1) {
        return (ConcurrentHashMap) this.concurrentHashMap.get(key1);
    }

    public V get(K1 info, K2 type) {
        ConcurrentHashMap<K2, V> map = (ConcurrentHashMap) this.concurrentHashMap.get(info);
        return map == null ? null : map.get(type);
    }

    public Collection<V> getAllValues(K1 info) {
        ConcurrentHashMap<K2, V> map = (ConcurrentHashMap) this.concurrentHashMap.get(info);
        return map == null ? null : map.values();
    }

    @SuppressLint({"NewApi", "ObsoleteSdkInt"})
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Collection<V> getAllValues() {
        ArrayList result = null;
        Set<K1> k1Set = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            k1Set = this.concurrentHashMap.keySet();
        }
        if (k1Set != null) {
            result = new ArrayList();

            for (K1 k1 : k1Set) {
                Collection values = ((ConcurrentHashMap) this.concurrentHashMap.get(k1)).values();
                if (values != null) {
                    result.addAll(values);
                }
            }
        }

        return result;
    }

    public boolean containsKey(K1 info, K2 type) {
        return this.concurrentHashMap.containsKey(info) ? ((ConcurrentHashMap) this.concurrentHashMap.get(info)).containsKey(type) : false;
    }

    public boolean containsKey(K1 info) {
        return this.concurrentHashMap.containsKey(info);
    }

    public int size() {
        if (this.concurrentHashMap.size() == 0) {
            return 0;
        } else {
            int result = 0;

            ConcurrentHashMap k2V_map;
            for (Iterator var2 = this.concurrentHashMap.values().iterator(); var2.hasNext(); result += k2V_map.size()) {
                k2V_map = (ConcurrentHashMap) var2.next();
            }

            return result;
        }
    }

    public void remove(K1 info) {
        this.concurrentHashMap.remove(info);
    }

    public void remove(K1 info, K2 type) {
        ConcurrentHashMap concurrentHashMap = (ConcurrentHashMap) this.concurrentHashMap.get(info);
        if (concurrentHashMap != null) {
            concurrentHashMap.remove(type);
        }
    }

    public void clear() {
        if (this.concurrentHashMap.size() > 0) {
            for (ConcurrentHashMap<K2, V> vConcurrentHashMap : this.concurrentHashMap.values()) {
                ConcurrentHashMap<K2, V> vMap = (ConcurrentHashMap) vConcurrentHashMap;
                vMap.clear();
            }
            this.concurrentHashMap.clear();
        }

    }
}
