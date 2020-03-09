package com.eureka.service.Util;

import java.util.ArrayList;
import java.util.List;

public class BitUtil {
    public static List<Integer> bitsAllSet(Long value) {
        int i = 0;
        List<Integer> list = new ArrayList<>();
        while (value != 0 && i < 64) {
            if ((value & 1) == 1)
                list.add(i);
            i += 1;
            value = value >> 1;
        }
        return list;
    }

    public static List<Long> parseToList(int position) {
        List<Long> list = new ArrayList<>();
        while (position >= 0) {
            if (position >= 64) {
                list.add(1l << 63);
                position -= 64;
            } else {
                list.add(1l << position);
                break;
            }
        }
        return list;
    }

    public static Boolean andList(List<Long> list1, List<Long> list2) {
        if (list1 == null || list2 == null)
            return false;
        if (list1.size() != list2.size())
            return false;
        for (int i = 0; i < list1.size(); i++) {
            if ((list1.get(i) & list2.get(i)) == 0)
                return false;
        }
        return true;
    }

    public static void orList(List<Long> list1, List<Long> list2) {
        if (list1 == null)
            list1 = new ArrayList<>();
        if (list2 == null)
            return;
        while (list1.size() < list2.size())
            list1.add(0l);
        for (int i = 0; i < list2.size(); i++) {
            list1.set(i, list1.get(i) | list2.get(i));
        }
    }

}