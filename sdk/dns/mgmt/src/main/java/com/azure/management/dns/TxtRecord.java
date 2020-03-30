// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.management.dns;

import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * The TxtRecord model.
 */
@Fluent
public final class TxtRecord {
    /*
     * The text value of this TXT record.
     */
    @JsonProperty(value = "value")
    private List<String> value;

    /**
     * Get the value property: The text value of this TXT record.
     * 
     * @return the value value.
     */
    public List<String> value() {
        return this.value;
    }

    /**
     * Set the value property: The text value of this TXT record.
     * 
     * @param value the value value to set.
     * @return the TxtRecord object itself.
     */
    public TxtRecord withValue(List<String> value) {
        this.value = value;
        return this;
    }
}