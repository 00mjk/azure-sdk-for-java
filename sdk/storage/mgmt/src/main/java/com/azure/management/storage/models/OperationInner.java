// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.management.storage.models;

import com.azure.core.annotation.Fluent;
import com.azure.core.annotation.JsonFlatten;
import com.azure.management.storage.OperationDisplay;
import com.azure.management.storage.ServiceSpecification;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Operation model.
 */
@JsonFlatten
@Fluent
public class OperationInner {
    /*
     * Operation name: {provider}/{resource}/{operation}
     */
    @JsonProperty(value = "name")
    private String name;

    /*
     * Display metadata associated with the operation.
     */
    @JsonProperty(value = "display")
    private OperationDisplay display;

    /*
     * The origin of operations.
     */
    @JsonProperty(value = "origin")
    private String origin;

    /*
     * One property of operation, include metric specifications.
     */
    @JsonProperty(value = "properties.serviceSpecification")
    private ServiceSpecification serviceSpecification;

    /**
     * Get the name property: Operation name:
     * {provider}/{resource}/{operation}.
     * 
     * @return the name value.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the name property: Operation name:
     * {provider}/{resource}/{operation}.
     * 
     * @param name the name value to set.
     * @return the OperationInner object itself.
     */
    public OperationInner setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get the display property: Display metadata associated with the
     * operation.
     * 
     * @return the display value.
     */
    public OperationDisplay getDisplay() {
        return this.display;
    }

    /**
     * Set the display property: Display metadata associated with the
     * operation.
     * 
     * @param display the display value to set.
     * @return the OperationInner object itself.
     */
    public OperationInner setDisplay(OperationDisplay display) {
        this.display = display;
        return this;
    }

    /**
     * Get the origin property: The origin of operations.
     * 
     * @return the origin value.
     */
    public String getOrigin() {
        return this.origin;
    }

    /**
     * Set the origin property: The origin of operations.
     * 
     * @param origin the origin value to set.
     * @return the OperationInner object itself.
     */
    public OperationInner setOrigin(String origin) {
        this.origin = origin;
        return this;
    }

    /**
     * Get the serviceSpecification property: One property of operation,
     * include metric specifications.
     * 
     * @return the serviceSpecification value.
     */
    public ServiceSpecification getServiceSpecification() {
        return this.serviceSpecification;
    }

    /**
     * Set the serviceSpecification property: One property of operation,
     * include metric specifications.
     * 
     * @param serviceSpecification the serviceSpecification value to set.
     * @return the OperationInner object itself.
     */
    public OperationInner setServiceSpecification(ServiceSpecification serviceSpecification) {
        this.serviceSpecification = serviceSpecification;
        return this;
    }
}