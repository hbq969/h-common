package com.github.hbq969.code.common.cache;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author : hbq969@gmail.com
 * @description : 包装值和过期信息的对象
 * @createTime : 12:51:06, 2023.04.02, 周日
 */
@AllArgsConstructor
@Data
public class ExpireValue implements Serializable {

  private static final long serialVersionUID = -1;

  private ExpireKey expire;

  private Object value;

  public boolean expire() {
    return expire.expire();
  }

  public void writeObject(ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
    out.writeObject(expire);
    out.writeObject(value);
  }

  public void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    this.expire = (ExpireKey) in.readObject();
    this.value = in.readObject();
  }
}
