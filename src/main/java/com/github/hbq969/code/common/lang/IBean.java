package com.github.hbq969.code.common.lang;

import com.github.hbq969.code.common.spring.context.SpringContext;

public interface IBean {
    void initial(SpringContext context);

    void modify(SpringContext context);

    void format(SpringContext context);

    default void deleting(SpringContext context) {
    }
}
