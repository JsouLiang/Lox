package com.company.environment;

import com.company.tokenizer.Token;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    /**
     * 父作用域
     *
     * var a = 1
     * {
     *  var a = 2
     * }
     */
    private final Environment enclosing;

    private final Map<String, Object> values = new HashMap<>();

    public Environment() {
        enclosing = null;
    }

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    /**
     * 定义一个变量
     *
     * 在定义变量时，我们没有检查变量名是否存在，所以
     * var a = "true"
     * var a = false
     * 是可以的
     */
    public void define(String name, Object value) {
        values.put(name, value);
    }

    /**
     * 变量赋值
     * @param name
     * @param value
     */
    public void assign(Token name , Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
        }
        if (enclosing != null) {
            // 父作用域查找变量赋值
            enclosing.assign(name, value);
        }
        //TODO:(weiguoliang): Throw undefine variable error
//        throw
    }

    public Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }
        if (enclosing != null) {
            return enclosing.get(name);
        }
        // TODO:(weiguoliang): Throw Runtime Exception;
        return null;
    }
}
