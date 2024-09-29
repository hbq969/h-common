
package com.github.hbq969.code.common.rule.map;


import com.github.hbq969.code.common.rule.NodeInfo;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author hbq969@gmail.com
 */
public interface Rule<T> {

    String EQ = "=";

    String SQLEQ = " = ";

    String NEQ = "!=";

    String SQLNEQ = " != ";

    String LIKE = "%=";

    String SQL_LIKE = " like ";

    String NOT_LIKE = "!%=";

    String SQL_NOT_LIKE = " not like ";

    String PERCENT = "%";

    String GT = ">";

    String SQLGT = " > ";

    String LT = "<";

    String SQLLT = " < ";

    String GET = ">=";

    String SQLGET = " >= ";

    String LET = "<=";

    String SQLLET = " <= ";

    char DQ = '"';

    char SQ = '\'';

    String DSQ = "\'\'";

    String OR = " or ";

    String AND = " and ";

    String LB = " ( ";

    String RB = " ) ";

    String ISNULL = " is null ";

    String ISNOTNULL = " is not null ";

    /**
     * 全字段匹配
     *
     * @param msg
     * @return
     */
    boolean accept(T msg);

    void accept(T msg, Consumer<T> suc, Consumer<T> fail);

    /**
     * 任意一个key匹配即匹配
     *
     * @param msg
     * @return
     */
    boolean anyOne(T msg);

    /**
     * 转换为sql语句
     *
     * @return
     */
    String toSql();

    /**
     * 转换为sql语句
     *
     * @param inkeys 作为判断条件，包含的key
     * @return
     */
    String toSqlIncludeKeys(Set<String> inkeys);

    /**
     * 转换为sql语句
     *
     * @param exkeys 不包含的key
     * @return
     */
    String toSqlExcludeKeys(Set<String> exkeys);

    /**
     * 获取规则中已配置的枚举信息,只适合属性在一个分组内的规则
     *
     * @return
     */
    List<NodeInfo> toNodeInfos();

    /**
     * 原始规则内容中所有的key集合
     *
     * @return
     */
    Set<String> conditionKeySet();
}
