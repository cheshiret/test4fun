package com.active.qa.automation.web.testapi.interfaces.flex;

import java.util.List;

/**
 * Flex ComboBox is a dropdown list from which the user an select a single value.
 * The ComboBox can be editable, in which case the user can type entries into it
 *
 * Created by tchen on 1/11/2016.
 */
public interface IFlexComboBox {
    void select(int itemIndex);
    void select(String itemName);
    void setText(String text);
    String getComboBoxValue();
    List<String> getDropdownOptions();
}

