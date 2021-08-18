package com.taiter.ce.Enchantments;

import com.taiter.ce.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/*
* This file is part of Custom Enchantments
* Copyright (C) Taiterio 2015
*
* This program is free software: you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as published by the
* Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
* for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

public class EnchantManager {

    private static Set<CEnchantment> enchantments = new LinkedHashSet<CEnchantment>();
    private static int maxEnchants = -1;

    private static String lorePrefix;
    private static String enchantBookName;


    public static ItemStack addEnchant(ItemStack item, CEnchantment ce) {
        return addEnchant(item, ce, 1);
    }

    public static ItemStack addEnchant(ItemStack item, CEnchantment ce, int level) {
        ItemMeta im = item.getItemMeta();
        List<String> lore = new ArrayList<String>();
        if (im.hasLore()) {
            lore = im.getLore();
            if (maxEnchants < enchantments.size()) {
                int counter = maxEnchants;
                for (String s : lore)
                    if (containsEnchantment(s)) {
                        counter--;
                        if (counter <= 0) {
                            return item;
                        }
                    }
            }
        }
        if (level > ce.getEnchantmentMaxLevel())
            level = ce.getEnchantmentMaxLevel();
        lore.add(ce.getDisplayName() + " " + intToLevel(level));
        im.setLore(lore);
        item.setItemMeta(im);
        return item;
    }

    public static ItemStack addEnchantments(ItemStack item, HashMap<CEnchantment, Integer> list) {
        ItemMeta im = item.getItemMeta();
        List<String> lore = new ArrayList<String>();
        if (im.hasLore()) {
            lore = im.getLore();
            if (maxEnchants < enchantments.size()) {
                int counter = maxEnchants - list.size();
                for (String s : lore)
                    if (containsEnchantment(s)) {
                        counter--;
                        if (counter <= 0) {
                            return item;
                        }
                    }
            }
        }
        for (CEnchantment ce : list.keySet()) {
            int level = list.get(ce);
            if (level > ce.getEnchantmentMaxLevel())
                level = ce.getEnchantmentMaxLevel();
            lore.add(ce.getDisplayName() + " " + intToLevel(level));
        }
        im.setLore(lore);
        item.setItemMeta(im);
        return item;
    }

    public static boolean hasEnchant(ItemStack item, CEnchantment ce) {
        ItemMeta im = item.getItemMeta();
        List<String> lore = im.getLore();
        for (String s : lore)
            if (s.startsWith(ce.getDisplayName()) || s.startsWith(lorePrefix + ce.getOriginalName()))
                return true;
        return false;
    }

    public static void removeEnchant(ItemStack item, CEnchantment ce) {
        if (!item.hasItemMeta() || !item.getItemMeta().hasLore())
            return;
        ItemMeta im = item.getItemMeta();
        List<String> lore = im.getLore();
        for (String s : lore)
            if (s.startsWith(ce.getDisplayName()) || s.startsWith(ce.getOriginalName())) {
                lore.remove(s);
                im.setLore(lore);
                item.setItemMeta(im);
                return;
            }
    }

    /**
     * Retrieves an enchantment by its original name. Assumes that the given String is not colored and equals the name
     * of an enchantment.
     * 
     * @param originalName
     *            The original name of the enchantment to retrieve
     * @return The enchantment specified by originalName
     */
    public static CEnchantment getInternalEnchantment(String originalName) {
        for (CEnchantment ce : enchantments) {
            if (ce.getOriginalName().equals(originalName))
                return ce;
        }
        return null;
    }

    public static CEnchantment getEnchantment(String name) {
        if (name.length() > 3)
            for (CEnchantment ce : enchantments) {
                String enchantment = ChatColor.stripColor(ce.getDisplayName()).toLowerCase();
                name = ChatColor.stripColor(name).toLowerCase();
                if (name.startsWith(enchantment) || name.startsWith(ce.getOriginalName().toLowerCase())) {
                    String[] split = name.split(" ");
                    if (split.length == enchantment.split(" ").length + 1) {
                        name = name.substring(0, name.length() - 1 - split[split.length - 1].length());
                        if (name.equals(enchantment) || name.equals(ce.getOriginalName()))
                            return ce;
                    } else {
                        if (name.equals(enchantment) || name.equals(ce.getOriginalName()))
                            return ce;
                    }
                }
            }
        return null;
    }

    public static Set<CEnchantment> getEnchantments() {
        return enchantments;
    }

    public static Set<CEnchantment> getEnchantments(List<String> lore) {
        Set<CEnchantment> list = new LinkedHashSet<CEnchantment>();
        if (lore != null)
            for (String name : lore)
                if (name.length() > 3)
                    for (CEnchantment ce : enchantments) {
                        String enchantment = ChatColor.stripColor(ce.getDisplayName()).toLowerCase();
                        name = ChatColor.stripColor(name).toLowerCase();
                        if (name.startsWith(enchantment) || name.startsWith(ce.getOriginalName().toLowerCase())) {
                            String[] split = name.split(" ");
                            name = name.substring(0, name.length() - 1 - split[split.length - 1].length());
                            if (name.equals(enchantment) || name.equals(ce.getOriginalName()))
                                list.add(ce);
                        }
                    }
        return list;
    }

    public static HashMap<CEnchantment, Integer> getEnchantmentLevels(List<String> lore) {
        HashMap<CEnchantment, Integer> list = new HashMap<CEnchantment, Integer>();
        if (lore != null)
            for (String name : lore)
                if (name.length() > 3)
                    for (CEnchantment ce : enchantments) {
                        String enchantment = ChatColor.stripColor(ce.getDisplayName()).toLowerCase();
                        name = ChatColor.stripColor(name).toLowerCase();
                        if (name.startsWith(enchantment) || name.startsWith(ce.getOriginalName().toLowerCase())) {
                            String[] split = name.split(" ");
                            name = name.substring(0, name.length() - 1 - split[split.length - 1].length());
                            if (name.equals(enchantment) || name.equals(ce.getOriginalName()))
                                list.put(ce, levelToInt(split[split.length - 1]));
                        }
                    }
        return list;
    }

    public static Boolean hasEnchantments(ItemStack toTest) {
        if (toTest != null)
            if (toTest.hasItemMeta() && toTest.getItemMeta().hasLore())
                for (String s : toTest.getItemMeta().getLore())
                    if (containsEnchantment(s))
                        return true;
        return false;
    }

    public static boolean isEnchantmentBook(ItemStack i) {
        if (i != null && i.getType().equals(Material.ENCHANTED_BOOK))
            if (i.hasItemMeta() && i.getItemMeta().hasDisplayName() && i.getItemMeta().getDisplayName().equals(enchantBookName))
                return true;
        return false;
    }

    public static boolean isEnchantable(String mat) {
        if (mat.contains("HELMET") || mat.contains("CHESTPLATE") || mat.contains("LEGGINGS") || mat.contains("BOOTS") || mat.contains("SWORD") || mat.contains("PICKAXE") || mat.contains("AXE")
                || mat.contains("SPADE") || mat.contains("HOE") || mat.equals("BOW"))
            return true;
        if ((Main.config.getBoolean("Global.Runecrafting.Disenchanting") && mat.equals("BOOK"))
                || ((Main.config.getBoolean("Global.Runecrafting.CanStackEnchantments") && mat.equals("ENCHANTED_BOOK"))))
            return true;
        return false;
    }

    public static Boolean containsEnchantment(List<String> toTest) {
        for (String s : toTest)
            if (containsEnchantment(s))
                return true;
        return false;
    }

    public static Boolean containsEnchantment(String toTest) {
        for (CEnchantment ce : enchantments) {
            if (containsEnchantment(toTest, ce))
                return true;
        }
        return false;
    }

    public static Boolean containsEnchantment(List<String> toTest, CEnchantment ce) {
        for (String s : toTest)
            if (containsEnchantment(s, ce))
                return true;
        return false;
    }

    public static Boolean containsEnchantment(String toTest, CEnchantment ce) {
        if (toTest.startsWith(ChatColor.YELLOW + "" + ChatColor.ITALIC + "\""))
            toTest = lorePrefix + ChatColor.stripColor(toTest.replace("\"", ""));
        String next = "";
        if (toTest.startsWith(lorePrefix + ce.getOriginalName()))
            next = lorePrefix + ce.getOriginalName();
        if (toTest.startsWith(ce.getDisplayName()))
            next = ce.getDisplayName();
        if (next.isEmpty())
            return false;
        String nextTest = toTest.replace(next, "");

        if (nextTest.startsWith(" ") || nextTest.isEmpty())
            return true;
        return false;
    }

    public static String getLorePrefix() {
        return lorePrefix;
    }

    public static int getMaxEnchants() {
        return maxEnchants;
    }

    /*
     * This returns the enchantment level of the CE identified by checkEnchant
     */
    public static int getLevel(String checkEnchant) {
        int level = 1;
        if (checkEnchant.contains(" ")) {
            String[] splitName = checkEnchant.split(" ");
            String possibleLevel = splitName[splitName.length - 1];
            level = levelToInt(possibleLevel);
        }
        return level;
    }

    public static LinkedHashMap<String, Integer> roman_numerals = new LinkedHashMap<String, Integer>() {{
            put("M", 1000);
            put("CM", 900);
            put("D", 500);
            put("CD", 400);
            put("C", 100);
            put("XC", 90);
            put("L", 50);
            put("XL", 40);
            put("X", 10);
            put("IX", 9);
            put("V", 5);
            put("IV", 4);
            put("I", 1);
        }};

    public static String intToLevel(int i) {
        String res = "";
        for(Map.Entry<String, Integer> entry : roman_numerals.entrySet()){
            int matches = i/entry.getValue();
            res += repeat(entry.getKey(), matches);
            i = i % entry.getValue();
        }
        return res;
    }

    public static String repeat(String s, int n) {
        if(s == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        for(int i = 0; i < n; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

//    public static String intToLevel(int i) {
//        String level;
//
//        if (i == 1)
//            level = "I";
//        else if (i == 2)
//            level = "II";
//        else if (i == 3)
//            level = "III";
//        else if (i == 4)
//            level = "IV";
//        else if (i == 5)
//            level = "V";
//        else if (i == 6)
//            level = "VI";
//        else if (i == 7)
//            level = "VII";
//        else if (i == 8)
//            level = "VIII";
//        else if (i == 9)
//            level = "IX";
//        else if (i == 10)
//            level = "X";
//        else
//            level = "" + i;
//
//        return level;
//    }

    public static int levelToInt(String number) {
        if (number.isEmpty()) return 0;
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException ignore) { }
        if (number.startsWith("M")) return 1000 + levelToInt(number.substring(1));
        if (number.startsWith("CM")) return 900 + levelToInt(number.substring(2));
        if (number.startsWith("D")) return 500 + levelToInt(number.substring(1));
        if (number.startsWith("CD")) return 400 + levelToInt(number.substring(2));
        if (number.startsWith("C")) return 100 + levelToInt(number.substring(1));
        if (number.startsWith("XC")) return 90 + levelToInt(number.substring(2));
        if (number.startsWith("L")) return 50 + levelToInt(number.substring(1));
        if (number.startsWith("XL")) return 40 + levelToInt(number.substring(2));
        if (number.startsWith("X")) return 10 + levelToInt(number.substring(1));
        if (number.startsWith("IX")) return 9 + levelToInt(number.substring(2));
        if (number.startsWith("V")) return 5 + levelToInt(number.substring(1));
        if (number.startsWith("IV")) return 4 + levelToInt(number.substring(2));
        if (number.startsWith("I")) return 1 + levelToInt(number.substring(1));
        throw new IllegalArgumentException(String.format("Could not process roman numeral of '%s'", number));
    }

//    public static int levelToInt(String level) {
//        level = level.toUpperCase();
//        int intLevel = 1;
//
//        if (level.equals("I"))
//            intLevel = 1;
//        else if (level.equals("II"))
//            intLevel = 2;
//        else if (level.equals("III"))
//            intLevel = 3;
//        else if (level.equals("IV"))
//            intLevel = 4;
//        else if (level.equals("V"))
//            intLevel = 5;
//        else if (level.equals("VI"))
//            intLevel = 6;
//        else if (level.equals("VII"))
//            intLevel = 7;
//        else if (level.equals("VIII"))
//            intLevel = 8;
//        else if (level.equals("IX"))
//            intLevel = 9;
//        else if (level.equals("X"))
//            intLevel = 10;
//        else
//            try {
//                intLevel = Integer.parseInt(level);
//            } catch (Exception ignore) { }
//
//        return intLevel;
//    }

    public static String getEnchantBookName() {
        return enchantBookName;
    }

    public static ItemStack getEnchantBook(CEnchantment ce) {
        return getEnchantBook(ce, 1);
    }

    public static ItemStack getEnchantBook(CEnchantment ce, int level) {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta im = item.getItemMeta();
        im.setLore(Arrays.asList(new String[] { lorePrefix + ce.getDisplayName() + " " + intToLevel(level) }));
        im.setDisplayName(enchantBookName);
        item.setItemMeta(im);
        return item;
    }

    public static ItemStack getEnchantBook(HashMap<CEnchantment, Integer> list) {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta im = item.getItemMeta();
        List<String> lore = new ArrayList<String>();
        for (CEnchantment ce : list.keySet()) {
            lore.add(lorePrefix + ce.getDisplayName() + " " + intToLevel(list.get(ce)));
        }
        im.setLore(lore);
        im.setDisplayName(enchantBookName);
        item.setItemMeta(im);
        return item;
    }

    public static void setLorePrefix(String newPrefix) {
        lorePrefix = newPrefix;
    }

    public static void setMaxEnchants(int newMax) {
        maxEnchants = newMax;
    }

    public static void setEnchantBookName(String newName) {
        enchantBookName = newName;
    }
}
