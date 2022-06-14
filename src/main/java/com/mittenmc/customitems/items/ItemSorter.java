package com.mittenmc.customitems.items;

import java.util.Comparator;

public class ItemSorter implements Comparator<CustomItemStack> {
    @Override
    public int compare(CustomItemStack o1, CustomItemStack o2) {
        return o1.getUncoloredName().compareTo(o2.getUncoloredName());
    }
}