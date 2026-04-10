package com.game.dressup;

/**
 * 唐老鸭抽象接口类
 */
public abstract class DonaldDuck extends Person {

    public DonaldDuck(String name, String description) {
        super(name, description);
    }

    /**
     * 唐老鸭特有的方法 - 获取经典度评分
     */
    public abstract int getClassicScore();

    /**
     * 唐老鸭特有的方法 - 获取迪士尼风格评分
     */
    public abstract int getDisneyStyleScore();
}