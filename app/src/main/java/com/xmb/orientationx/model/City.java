package com.xmb.orientationx.model;

import java.io.Serializable;

/**
 * City.
 *
 * @author 徐梦笔
 */
public class City implements Serializable {
    /**
     * city data structures.
     */
    private String label, name, pinyin, zip;

    /**
     * Gets label.
     *
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets label.
     *
     * @param label the label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets pinyin.
     *
     * @return the pinyin
     */
    public String getPinyin() {
        return pinyin;
    }

    /**
     * Sets pinyin.
     *
     * @param pinyin the pinyin
     */
    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    /**
     * Gets zip.
     *
     * @return the zip
     */
    public String getZip() {
        return zip;
    }

    /**
     * Sets zip.
     *
     * @param zip the zip
     */
    public void setZip(String zip) {
        this.zip = zip;
    }

}
