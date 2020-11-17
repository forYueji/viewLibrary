package com.hyp.viewlibrary;

/**
 * @author: hyp
 * @date: 2018-01-07
 */
public class ViewInjectInfo {
    public Object value;
    public int parentId;

    public ViewInjectInfo() {
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof ViewInjectInfo)) {
            return false;
        } else {
            ViewInjectInfo that = (ViewInjectInfo) o;
            if (this.parentId != that.parentId) {
                return false;
            } else if (this.value == null) {
                return that.value == null;
            } else {
                return this.value.equals(that.value);
            }
        }
    }

    public int hashCode() {
        int result = this.value.hashCode();
        result = 31 * result + this.parentId;
        return result;
    }
}
