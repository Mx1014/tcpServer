package cn.usr.service;


/**
 * 管理连接：添加、删除等
 *
 * @author liu
 */
public interface IConnectionManager<V> {
    /**
     * 添加一个连接
     *
     * @param key
     * @param v
     * @return 添加成功返回true
     */
    boolean addConnection(String key, V v);

    /**
     * 获得一个连接
     *
     * @param key
     * @return
     */
    V getConnection(String key);


    /**
     * 是否包含连接
     *
     * @param key
     * @return
     */
    boolean containConnection(String key);

    /**
     * 删除一个连接
     *
     * @param key
     * @param v   期望删除的连接
     * @return 删除成功返回true
     */
    boolean removeConnection(String key, V v);

    /**
     * 获取连接数量
     *
     * @return
     */
    long getConnectionCount();
}
