package com.github.hbq969.code.common.cache;

import com.github.hbq969.code.common.utils.GsonUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author : hbq969@gmail.com
 * @description : 过期KEY包装器
 * @createTime : 15:07:48, 2023.03.31, 周五
 */
@AllArgsConstructor
@Data
public class ExpireKey implements Serializable {

    private static final long serialVersionUID = -1;
    /**
     * 真实的KEY
     */
    private String key;

    /**
     * 过期时间值
     */
    private long expire;

    /**
     * 过期时间单位
     */
    private TimeUnit unit;

    /**
     * 创建时间戳
     */
    private long createTimeMills;

    public static ExpireKey of(String key) {
        ExpireKey expire = new ExpireKey(key, 0, TimeUnit.SECONDS, 0);
        return expire;
    }

    /**
     * 判断是否过期
     *
     * @return
     */
    public boolean expire() {
        long dt = System.currentTimeMillis() - createTimeMills;
        return dt >= TimeUnit.MILLISECONDS.convert(expire, unit);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExpireKey expireKey = (ExpireKey) o;
        return Objects.equals(getKey(), expireKey.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey());
    }

    public void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeUTF(key);
        out.writeLong(expire);
        out.writeObject(unit);
        out.writeLong(createTimeMills);
    }

    public void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.key = in.readUTF();
        this.expire = in.readLong();
        this.unit = (TimeUnit) in.readObject();
        this.createTimeMills = in.readLong();
    }

    @Override
    public String toString() {
        return GsonUtils.toJson(this);
    }
}
