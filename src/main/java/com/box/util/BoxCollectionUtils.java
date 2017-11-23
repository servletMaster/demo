package com.box.util;

import com.box.exception.Exceptions;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import  org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * @author yulewei on 2016/8/30
 */
public class BoxCollectionUtils {
    private static final Logger logger = LoggerFactory.getLogger(BoxCollectionUtils.class);

    /**
     * 从bean对象List中筛选出某个属性的List
     * 注1：读取的是属性值而非字段值
     * 注2：类似的功能，也可以直接使用Apache Commons库，示例：
     * CollectionUtils.collect(helloList, TransformerUtils.<Hello, Integer>invokerTransformer("getId"))
     */
    public static <I, O> List<O> collect(Collection<I> inputCollection, String propertyName) {
        List<O> list = new ArrayList<O>();
        for (I item : inputCollection) {
            try {
                if (item == null) {
                    continue;
                }
                list.add((O) PropertyUtils.getProperty(item, propertyName));
            } catch (Exception e) {
                throw Exceptions.unchecked(e);
            }
        }
        return list;
    }

    public static <K, I> Map<K, I> toMap(Collection<I> inputCollection, Transformer<? super I, ? extends K> keyTransformer) {
        Map<K, I> map = new HashMap<K, I>();
        for (I item : inputCollection) {
            if (item == null) {
                continue;
            }
            map.put(keyTransformer.transform(item), item);
        }
        return map;
    }

    /**
     * 将List转为Map，键和值对应关系为，1对1
     *
     * @param inputCollection 来源集合
     * @param keyPropertyName 要提取为Map中的Key值的属性名
     */
    public static <K, O> Map<K, O> toMap(Collection<O> inputCollection, String keyPropertyName) {
        Map<K, O> map = new HashMap<K, O>();
        for (O item : inputCollection) {
            try {
                if (item == null) {
                    continue;
                }
                K keyValue = (K) PropertyUtils.getProperty(item, keyPropertyName);
                map.put(keyValue, item);
            } catch (Exception e) {
                throw Exceptions.unchecked(e);
            }
        }
        return map;
    }

    /**
     * 将List转为Map，键和值对应关系为，1对1
     *
     * @param inputCollection   来源集合
     * @param keyPropertyName   要提取为Map中的Key值的属性名
     * @param valuePropertyName 要提取为Map中的Value值的属性名
     */
    public static <K, O, V> Map<K, V> toMap(Collection<O> inputCollection, String keyPropertyName, String valuePropertyName) {
        Map<K, V> map = new HashMap<K, V>();
        for (O item : inputCollection) {
            try {
                if (item == null) {
                    continue;
                }
                K keyValue = (K) PropertyUtils.getProperty(item, keyPropertyName);
                V valueProperty = (V) PropertyUtils.getProperty(item, valuePropertyName);
                map.put(keyValue, valueProperty);
            } catch (Exception e) {
                throw Exceptions.unchecked(e);
            }
        }
        return map;
    }

    /**
     * 将List转为Map，键和值对应关系为，1对N
     */
    public static <K, O> Map<K, List<O>> groupToMap(Collection<O> inputCollection, String keyPropertyName) {
        Map<K, List<O>> map = new HashMap<K, List<O>>();
        for (O item : inputCollection) {
            try {
                if (item == null) {
                    continue;
                }
                K keyValue = (K) PropertyUtils.getProperty(item, keyPropertyName);
                List<O> list = map.get(keyValue);
                if (list == null) {
                    list = new ArrayList<O>();
                }
                list.add(item);
                map.put(keyValue, list);
            } catch (Exception e) {
                throw Exceptions.unchecked(e);
            }
        }
        return map;
    }

    /**
     * 内连接。不保留未匹配的元素
     *
     * @param leftList       左列表
     * @param rightList      右列表
     * @param leftProperty   左列表中的属性，用于和右列表关联
     * @param rightProperty  右列表中的属性，用于和左列表关联
     * @param targetProperty 在左列表中保存右列表中的属性
     * @param <O1>
     * @param <O2>
     * @return
     */
    public static <O1, O2> List<O1> innerJoin(List<O1> leftList, List<O2> rightList, String leftProperty, String rightProperty, String targetProperty) {
        List<O1> notMatchedList = new ArrayList<>();
        Map<Object, O2> rightMap = BoxCollectionUtils.toMap(rightList, rightProperty);
        for (O1 leftItem : leftList) {
            try {
                Object value = PropertyUtils.getProperty(leftItem, leftProperty);
                O2 rightItem = rightMap.get(value);
                if (rightItem != null) {
                    PropertyUtils.setProperty(leftItem, targetProperty, rightItem);
                } else {
                    notMatchedList.add(leftItem);
                }
            } catch (Exception e) {
                throw Exceptions.unchecked(e);
            }
        }
        leftList.removeAll(notMatchedList);
        return leftList;
    }

    /**
     * 左外连接。左边列表保留未匹配的元素
     *
     * @param leftList       左列表
     * @param rightList      右列表
     * @param leftProperty   左列表中的属性，用于和右列表关联
     * @param rightProperty  右列表中的属性，用于和左列表关联
     * @param targetProperty 在左列表中保存右列表中的属性
     * @param <O1>
     * @param <O2>
     * @return
     */
    public static <O1, O2> List<O1> leftJoin(List<O1> leftList, List<O2> rightList, String leftProperty, String rightProperty, String targetProperty) {
        Map<Object, O2> rightMap = BoxCollectionUtils.toMap(rightList, rightProperty);
        for (O1 leftItem : leftList) {
            try {
                Object value = PropertyUtils.getProperty(leftItem, leftProperty);
                O2 rightItem = rightMap.get(value);
                PropertyUtils.setProperty(leftItem, targetProperty, rightItem);
            } catch (Exception e) {
                throw Exceptions.unchecked(e);
            }
        }
        return leftList;
    }

    /**
     * 按键的子集，提取子列表
     */
    public static <K, O> List<O> subList(Map<K, O> inputMap, Collection<K> keys) {
        List<O> list = new ArrayList<O>();
        for (K key : keys) {
            if (key == null) {
                continue;
            }
            list.add(inputMap.get(key));
        }
        return list;
    }


    /**
     * 在不改变顺序的情况下，去重
     *
     * @param inputCollection 入参集合
     * @param <O>             类型
     */
    public static <O> List<O> removeDuplicate(Collection<O> inputCollection) {
        if (CollectionUtils.isEmpty(inputCollection)) {
            return Collections.emptyList();
        }
        return IterableUtils.toList(new LinkedHashSet<>(inputCollection));
    }

    /**
     * 随机提取count个元素的集合
     * <p>
     * 复杂对象时，这种随机提取更好，Integer的随机排序比复杂对象的排序更加方便
     *
     * @param list  原集合
     * @param count count
     */
    public static <O> List<O> subListRandom(List<O> list, Integer count) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        int size = list.size();
        ArrayList<Integer> indexList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            indexList.add(i);
        }
        List<O> randomList = new ArrayList<>(size);
        if (count == null || count > size) {
            count = size;
        }
        Random random = new Random();
        int max_i = size - 1;
        for (int i = 0; i < count; i++) {
            int index = random.nextInt(max_i + 1);
            randomList.add(list.get(indexList.get(index)));
            indexList.set(index, indexList.get(max_i));
            max_i--;
        }
        return randomList;
    }

    /**
     * 转换Collection所有元素(通过toString())为String, 中间以 separator分隔。
     */
    public static String convertToString(final Collection collection, final String separator) {
        return StringUtils.join(collection, separator);
    }


    /**
     * 取得Collection的第一个元素，如果collection为空返回null.
     */
    public static <T> T getFirst(Collection<T> collection) {
        if (CollectionUtils.isEmpty(collection)) {
            return null;
        }
        return collection.iterator().next();
    }

    /**
     * 获取Collection的最后一个元素 ，如果collection为空返回null.
     */
    public static <T> T getLast(Collection<T> collection) {
        if (CollectionUtils.isEmpty(collection)) {
            return null;
        }

        //当类型为List时，直接取得最后一个元素 。
        if (collection instanceof List) {
            List<T> list = (List<T>) collection;
            return list.get(list.size() - 1);
        }

        //其他类型通过iterator滚动到最后一个元素.
        Iterator<T> iterator = collection.iterator();
        while (true) {
            T current = iterator.next();
            if (!iterator.hasNext()) {
                return current;
            }
        }
    }

    /**
     * 返回a+b的新List.
     */
    public static <T> List<T> union(final Collection<T> a, final Collection<T> b) {
        if (CollectionUtils.isEmpty(a)) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>(a);
        if (CollectionUtils.isEmpty(b)) {
            return result;
        }
        result.addAll(b);
        return result;
    }

    /**
     * 返回a-b的新List.
     */
    public static <T> List<T> subtract(final Collection<T> a, final Collection<T> b) {
        if (CollectionUtils.isEmpty(a)) {
            return Collections.emptyList();
        }
        List<T> list = new ArrayList<>(a);
        if (CollectionUtils.isEmpty(b)) {
            return list;
        }
        List<T> commonList = new ArrayList<>(a);
        commonList.retainAll(b);
        if (CollectionUtils.isEmpty(commonList)) {
            return list;
        }
        list.removeAll(commonList);
        return list;
    }

    /**
     * 返回a与b的交集的新List.
     */
    public static <T> List<T> intersection(Collection<T> a, Collection<T> b) {
        if (CollectionUtils.isEmpty(a) || CollectionUtils.isEmpty(b)) {
            return Collections.emptyList();
        }
        List<T> list = new ArrayList<>(a);
        list.retainAll(b);
        return list;
    }
}
