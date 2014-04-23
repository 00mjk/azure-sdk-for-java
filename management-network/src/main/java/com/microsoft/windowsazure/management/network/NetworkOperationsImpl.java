/**
 * 
 * Copyright (c) Microsoft and contributors.  All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

// Warning: This code was generated by a tool.
// 
// Changes to this file may cause incorrect behavior and will be lost if the
// code is regenerated.

package com.microsoft.windowsazure.management.network;

import com.microsoft.windowsazure.core.OperationResponse;
import com.microsoft.windowsazure.core.OperationStatus;
import com.microsoft.windowsazure.core.OperationStatusResponse;
import com.microsoft.windowsazure.core.ServiceOperations;
import com.microsoft.windowsazure.core.utils.BOMInputStream;
import com.microsoft.windowsazure.core.utils.StreamUtils;
import com.microsoft.windowsazure.core.utils.XmlUtility;
import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.management.network.models.GatewayProfile;
import com.microsoft.windowsazure.management.network.models.LocalNetworkConnectionType;
import com.microsoft.windowsazure.management.network.models.NetworkGetConfigurationResponse;
import com.microsoft.windowsazure.management.network.models.NetworkListResponse;
import com.microsoft.windowsazure.management.network.models.NetworkSetConfigurationParameters;
import com.microsoft.windowsazure.tracing.ClientRequestTrackingHandler;
import com.microsoft.windowsazure.tracing.CloudTracing;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class NetworkOperationsImpl implements ServiceOperations<NetworkManagementClientImpl>, NetworkOperations {
    /**
    * Initializes a new instance of the NetworkOperationsImpl class.
    *
    * @param client Reference to the service client.
    */
    NetworkOperationsImpl(NetworkManagementClientImpl client) {
        this.client = client;
    }
    
    private NetworkManagementClientImpl client;
    
    /**
    * Gets a reference to the
    * microsoft.windowsazure.management.network.NetworkManagementClientImpl.
    * @return The Client value.
    */
    public NetworkManagementClientImpl getClient() {
        return this.client;
    }
    
    /**
    * The Set Network Configuration operation asynchronously configures the
    * virtual network  (see
    * http://msdn.microsoft.com/en-us/library/windowsazure/jj157181.aspx for
    * more information)
    *
    * @param parameters Required. The updated network configuration.
    * @return A standard storage response including an HTTP status code and
    * request ID.
    */
    @Override
    public Future<OperationResponse> beginSettingConfigurationAsync(final NetworkSetConfigurationParameters parameters) {
        return this.getClient().getExecutorService().submit(new Callable<OperationResponse>() { 
            @Override
            public OperationResponse call() throws Exception {
                return beginSettingConfiguration(parameters);
            }
         });
    }
    
    /**
    * The Set Network Configuration operation asynchronously configures the
    * virtual network  (see
    * http://msdn.microsoft.com/en-us/library/windowsazure/jj157181.aspx for
    * more information)
    *
    * @param parameters Required. The updated network configuration.
    * @throws IOException Signals that an I/O exception of some sort has
    * occurred. This class is the general class of exceptions produced by
    * failed or interrupted I/O operations.
    * @throws ServiceException Thrown if an unexpected response is found.
    * @return A standard storage response including an HTTP status code and
    * request ID.
    */
    @Override
    public OperationResponse beginSettingConfiguration(NetworkSetConfigurationParameters parameters) throws IOException, ServiceException {
        // Validate
        if (parameters == null) {
            throw new NullPointerException("parameters");
        }
        if (parameters.getConfiguration() == null) {
            throw new NullPointerException("parameters.Configuration");
        }
        
        // Tracing
        boolean shouldTrace = CloudTracing.getIsEnabled();
        String invocationId = null;
        if (shouldTrace) {
            invocationId = Long.toString(CloudTracing.getNextInvocationId());
            HashMap<String, Object> tracingParameters = new HashMap<String, Object>();
            tracingParameters.put("parameters", parameters);
            CloudTracing.enter(invocationId, this, "beginSettingConfigurationAsync", tracingParameters);
        }
        
        // Construct URL
        String baseUrl = this.getClient().getBaseUri().toString();
        String url = "/" + this.getClient().getCredentials().getSubscriptionId().trim() + "/services/networking/media";
        // Trim '/' character from the end of baseUrl and beginning of url.
        if (baseUrl.charAt(baseUrl.length() - 1) == '/') {
            baseUrl = baseUrl.substring(0, (baseUrl.length() - 1) + 0);
        }
        if (url.charAt(0) == '/') {
            url = url.substring(1);
        }
        url = baseUrl + "/" + url;
        
        // Create HTTP transport objects
        HttpPut httpRequest = new HttpPut(url);
        
        // Set Headers
        httpRequest.setHeader("Content-Type", "application/octet-stream");
        httpRequest.setHeader("x-ms-version", "2013-11-01");
        
        // Serialize Request
        String requestContent = parameters.getConfiguration();
        StringEntity entity = new StringEntity(requestContent);
        httpRequest.setEntity(entity);
        httpRequest.setHeader("Content-Type", "application/octet-stream");
        
        // Send Request
        HttpResponse httpResponse = null;
        try {
            if (shouldTrace) {
                CloudTracing.sendRequest(invocationId, httpRequest);
            }
            httpResponse = this.getClient().getHttpClient().execute(httpRequest);
            if (shouldTrace) {
                CloudTracing.receiveResponse(invocationId, httpResponse);
            }
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_ACCEPTED) {
                ServiceException ex = ServiceException.createFromXml(httpRequest, requestContent, httpResponse, httpResponse.getEntity());
                if (shouldTrace) {
                    CloudTracing.error(invocationId, ex);
                }
                throw ex;
            }
            
            // Create Result
            OperationResponse result = null;
            result = new OperationResponse();
            result.setStatusCode(statusCode);
            if (httpResponse.getHeaders("x-ms-request-id").length > 0) {
                result.setRequestId(httpResponse.getFirstHeader("x-ms-request-id").getValue());
            }
            
            if (shouldTrace) {
                CloudTracing.exit(invocationId, result);
            }
            return result;
        } finally {
            if (httpResponse != null && httpResponse.getEntity() != null) {
                httpResponse.getEntity().getContent().close();
            }
        }
    }
    
    /**
    * The Get Network Configuration operation retrieves the network
    * configuration file for the given subscription.  (see
    * http://msdn.microsoft.com/en-us/library/windowsazure/jj157196.aspx for
    * more information)
    *
    * @return The Get Network Configuration operation response.
    */
    @Override
    public Future<NetworkGetConfigurationResponse> getConfigurationAsync() {
        return this.getClient().getExecutorService().submit(new Callable<NetworkGetConfigurationResponse>() { 
            @Override
            public NetworkGetConfigurationResponse call() throws Exception {
                return getConfiguration();
            }
         });
    }
    
    /**
    * The Get Network Configuration operation retrieves the network
    * configuration file for the given subscription.  (see
    * http://msdn.microsoft.com/en-us/library/windowsazure/jj157196.aspx for
    * more information)
    *
    * @throws IOException Signals that an I/O exception of some sort has
    * occurred. This class is the general class of exceptions produced by
    * failed or interrupted I/O operations.
    * @throws ServiceException Thrown if an unexpected response is found.
    * @return The Get Network Configuration operation response.
    */
    @Override
    public NetworkGetConfigurationResponse getConfiguration() throws IOException, ServiceException {
        // Validate
        
        // Tracing
        boolean shouldTrace = CloudTracing.getIsEnabled();
        String invocationId = null;
        if (shouldTrace) {
            invocationId = Long.toString(CloudTracing.getNextInvocationId());
            HashMap<String, Object> tracingParameters = new HashMap<String, Object>();
            CloudTracing.enter(invocationId, this, "getConfigurationAsync", tracingParameters);
        }
        
        // Construct URL
        String baseUrl = this.getClient().getBaseUri().toString();
        String url = "/" + this.getClient().getCredentials().getSubscriptionId().trim() + "/services/networking/media";
        // Trim '/' character from the end of baseUrl and beginning of url.
        if (baseUrl.charAt(baseUrl.length() - 1) == '/') {
            baseUrl = baseUrl.substring(0, (baseUrl.length() - 1) + 0);
        }
        if (url.charAt(0) == '/') {
            url = url.substring(1);
        }
        url = baseUrl + "/" + url;
        
        // Create HTTP transport objects
        HttpGet httpRequest = new HttpGet(url);
        
        // Set Headers
        httpRequest.setHeader("x-ms-version", "2013-11-01");
        
        // Send Request
        HttpResponse httpResponse = null;
        try {
            if (shouldTrace) {
                CloudTracing.sendRequest(invocationId, httpRequest);
            }
            httpResponse = this.getClient().getHttpClient().execute(httpRequest);
            if (shouldTrace) {
                CloudTracing.receiveResponse(invocationId, httpResponse);
            }
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                ServiceException ex = ServiceException.createFromXml(httpRequest, null, httpResponse, httpResponse.getEntity());
                if (shouldTrace) {
                    CloudTracing.error(invocationId, ex);
                }
                throw ex;
            }
            
            // Create Result
            NetworkGetConfigurationResponse result = null;
            // Deserialize Response
            InputStream responseContent = httpResponse.getEntity().getContent();
            result = new NetworkGetConfigurationResponse();
            result.setConfiguration(StreamUtils.toString(responseContent));
            
            result.setStatusCode(statusCode);
            if (httpResponse.getHeaders("x-ms-request-id").length > 0) {
                result.setRequestId(httpResponse.getFirstHeader("x-ms-request-id").getValue());
            }
            
            if (shouldTrace) {
                CloudTracing.exit(invocationId, result);
            }
            return result;
        } finally {
            if (httpResponse != null && httpResponse.getEntity() != null) {
                httpResponse.getEntity().getContent().close();
            }
        }
    }
    
    /**
    * The List Virtual network sites operation retrieves the virtual networks
    * configured for the subscription.  (see
    * http://msdn.microsoft.com/en-us/library/windowsazure/jj157185.aspx for
    * more information)
    *
    * @return The response structure for the Server List operation.
    */
    @Override
    public Future<NetworkListResponse> listAsync() {
        return this.getClient().getExecutorService().submit(new Callable<NetworkListResponse>() { 
            @Override
            public NetworkListResponse call() throws Exception {
                return list();
            }
         });
    }
    
    /**
    * The List Virtual network sites operation retrieves the virtual networks
    * configured for the subscription.  (see
    * http://msdn.microsoft.com/en-us/library/windowsazure/jj157185.aspx for
    * more information)
    *
    * @throws IOException Signals that an I/O exception of some sort has
    * occurred. This class is the general class of exceptions produced by
    * failed or interrupted I/O operations.
    * @throws ServiceException Thrown if an unexpected response is found.
    * @throws ParserConfigurationException Thrown if there was a serious
    * configuration error with the document parser.
    * @throws SAXException Thrown if there was an error parsing the XML
    * response.
    * @return The response structure for the Server List operation.
    */
    @Override
    public NetworkListResponse list() throws IOException, ServiceException, ParserConfigurationException, SAXException {
        // Validate
        
        // Tracing
        boolean shouldTrace = CloudTracing.getIsEnabled();
        String invocationId = null;
        if (shouldTrace) {
            invocationId = Long.toString(CloudTracing.getNextInvocationId());
            HashMap<String, Object> tracingParameters = new HashMap<String, Object>();
            CloudTracing.enter(invocationId, this, "listAsync", tracingParameters);
        }
        
        // Construct URL
        String baseUrl = this.getClient().getBaseUri().toString();
        String url = "/" + this.getClient().getCredentials().getSubscriptionId().trim() + "/services/networking/virtualnetwork";
        // Trim '/' character from the end of baseUrl and beginning of url.
        if (baseUrl.charAt(baseUrl.length() - 1) == '/') {
            baseUrl = baseUrl.substring(0, (baseUrl.length() - 1) + 0);
        }
        if (url.charAt(0) == '/') {
            url = url.substring(1);
        }
        url = baseUrl + "/" + url;
        
        // Create HTTP transport objects
        HttpGet httpRequest = new HttpGet(url);
        
        // Set Headers
        httpRequest.setHeader("x-ms-version", "2013-11-01");
        
        // Send Request
        HttpResponse httpResponse = null;
        try {
            if (shouldTrace) {
                CloudTracing.sendRequest(invocationId, httpRequest);
            }
            httpResponse = this.getClient().getHttpClient().execute(httpRequest);
            if (shouldTrace) {
                CloudTracing.receiveResponse(invocationId, httpResponse);
            }
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                ServiceException ex = ServiceException.createFromXml(httpRequest, null, httpResponse, httpResponse.getEntity());
                if (shouldTrace) {
                    CloudTracing.error(invocationId, ex);
                }
                throw ex;
            }
            
            // Create Result
            NetworkListResponse result = null;
            // Deserialize Response
            InputStream responseContent = httpResponse.getEntity().getContent();
            result = new NetworkListResponse();
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document responseDoc = documentBuilder.parse(new BOMInputStream(responseContent));
            
            Element virtualNetworkSitesSequenceElement = XmlUtility.getElementByTagNameNS(responseDoc, "http://schemas.microsoft.com/windowsazure", "VirtualNetworkSites");
            if (virtualNetworkSitesSequenceElement != null) {
                for (int i1 = 0; i1 < com.microsoft.windowsazure.core.utils.XmlUtility.getElementsByTagNameNS(virtualNetworkSitesSequenceElement, "http://schemas.microsoft.com/windowsazure", "VirtualNetworkSite").size(); i1 = i1 + 1) {
                    org.w3c.dom.Element virtualNetworkSitesElement = ((org.w3c.dom.Element) com.microsoft.windowsazure.core.utils.XmlUtility.getElementsByTagNameNS(virtualNetworkSitesSequenceElement, "http://schemas.microsoft.com/windowsazure", "VirtualNetworkSite").get(i1));
                    NetworkListResponse.VirtualNetworkSite virtualNetworkSiteInstance = new NetworkListResponse.VirtualNetworkSite();
                    result.getVirtualNetworkSites().add(virtualNetworkSiteInstance);
                    
                    Element nameElement = XmlUtility.getElementByTagNameNS(virtualNetworkSitesElement, "http://schemas.microsoft.com/windowsazure", "Name");
                    if (nameElement != null) {
                        String nameInstance;
                        nameInstance = nameElement.getTextContent();
                        virtualNetworkSiteInstance.setName(nameInstance);
                    }
                    
                    Element labelElement = XmlUtility.getElementByTagNameNS(virtualNetworkSitesElement, "http://schemas.microsoft.com/windowsazure", "Label");
                    if (labelElement != null) {
                        String labelInstance;
                        labelInstance = labelElement.getTextContent();
                        virtualNetworkSiteInstance.setLabel(labelInstance);
                    }
                    
                    Element idElement = XmlUtility.getElementByTagNameNS(virtualNetworkSitesElement, "http://schemas.microsoft.com/windowsazure", "Id");
                    if (idElement != null) {
                        String idInstance;
                        idInstance = idElement.getTextContent();
                        virtualNetworkSiteInstance.setId(idInstance);
                    }
                    
                    Element affinityGroupElement = XmlUtility.getElementByTagNameNS(virtualNetworkSitesElement, "http://schemas.microsoft.com/windowsazure", "AffinityGroup");
                    if (affinityGroupElement != null) {
                        String affinityGroupInstance;
                        affinityGroupInstance = affinityGroupElement.getTextContent();
                        virtualNetworkSiteInstance.setAffinityGroup(affinityGroupInstance);
                    }
                    
                    Element stateElement = XmlUtility.getElementByTagNameNS(virtualNetworkSitesElement, "http://schemas.microsoft.com/windowsazure", "State");
                    if (stateElement != null) {
                        String stateInstance;
                        stateInstance = stateElement.getTextContent();
                        virtualNetworkSiteInstance.setState(stateInstance);
                    }
                    
                    Element addressSpaceElement = XmlUtility.getElementByTagNameNS(virtualNetworkSitesElement, "http://schemas.microsoft.com/windowsazure", "AddressSpace");
                    if (addressSpaceElement != null) {
                        NetworkListResponse.AddressSpace addressSpaceInstance = new NetworkListResponse.AddressSpace();
                        virtualNetworkSiteInstance.setAddressSpace(addressSpaceInstance);
                        
                        Element addressPrefixesSequenceElement = XmlUtility.getElementByTagNameNS(addressSpaceElement, "http://schemas.microsoft.com/windowsazure", "AddressPrefixes");
                        if (addressPrefixesSequenceElement != null) {
                            for (int i2 = 0; i2 < com.microsoft.windowsazure.core.utils.XmlUtility.getElementsByTagNameNS(addressPrefixesSequenceElement, "http://schemas.microsoft.com/windowsazure", "AddressPrefix").size(); i2 = i2 + 1) {
                                org.w3c.dom.Element addressPrefixesElement = ((org.w3c.dom.Element) com.microsoft.windowsazure.core.utils.XmlUtility.getElementsByTagNameNS(addressPrefixesSequenceElement, "http://schemas.microsoft.com/windowsazure", "AddressPrefix").get(i2));
                                addressSpaceInstance.getAddressPrefixes().add(addressPrefixesElement.getTextContent());
                            }
                        }
                    }
                    
                    Element subnetsSequenceElement = XmlUtility.getElementByTagNameNS(virtualNetworkSitesElement, "http://schemas.microsoft.com/windowsazure", "Subnets");
                    if (subnetsSequenceElement != null) {
                        for (int i3 = 0; i3 < com.microsoft.windowsazure.core.utils.XmlUtility.getElementsByTagNameNS(subnetsSequenceElement, "http://schemas.microsoft.com/windowsazure", "Subnet").size(); i3 = i3 + 1) {
                            org.w3c.dom.Element subnetsElement = ((org.w3c.dom.Element) com.microsoft.windowsazure.core.utils.XmlUtility.getElementsByTagNameNS(subnetsSequenceElement, "http://schemas.microsoft.com/windowsazure", "Subnet").get(i3));
                            NetworkListResponse.Subnet subnetInstance = new NetworkListResponse.Subnet();
                            virtualNetworkSiteInstance.getSubnets().add(subnetInstance);
                            
                            Element nameElement2 = XmlUtility.getElementByTagNameNS(subnetsElement, "http://schemas.microsoft.com/windowsazure", "Name");
                            if (nameElement2 != null) {
                                String nameInstance2;
                                nameInstance2 = nameElement2.getTextContent();
                                subnetInstance.setName(nameInstance2);
                            }
                            
                            Element addressPrefixElement = XmlUtility.getElementByTagNameNS(subnetsElement, "http://schemas.microsoft.com/windowsazure", "AddressPrefix");
                            if (addressPrefixElement != null) {
                                String addressPrefixInstance;
                                addressPrefixInstance = addressPrefixElement.getTextContent();
                                subnetInstance.setAddressPrefix(addressPrefixInstance);
                            }
                        }
                    }
                    
                    Element dnsElement = XmlUtility.getElementByTagNameNS(virtualNetworkSitesElement, "http://schemas.microsoft.com/windowsazure", "Dns");
                    if (dnsElement != null) {
                        Element dnsServersSequenceElement = XmlUtility.getElementByTagNameNS(dnsElement, "http://schemas.microsoft.com/windowsazure", "DnsServers");
                        if (dnsServersSequenceElement != null) {
                            for (int i4 = 0; i4 < com.microsoft.windowsazure.core.utils.XmlUtility.getElementsByTagNameNS(dnsServersSequenceElement, "http://schemas.microsoft.com/windowsazure", "DnsServer").size(); i4 = i4 + 1) {
                                org.w3c.dom.Element dnsServersElement = ((org.w3c.dom.Element) com.microsoft.windowsazure.core.utils.XmlUtility.getElementsByTagNameNS(dnsServersSequenceElement, "http://schemas.microsoft.com/windowsazure", "DnsServer").get(i4));
                                NetworkListResponse.DnsServer dnsServerInstance = new NetworkListResponse.DnsServer();
                                virtualNetworkSiteInstance.getDnsServers().add(dnsServerInstance);
                                
                                Element nameElement3 = XmlUtility.getElementByTagNameNS(dnsServersElement, "http://schemas.microsoft.com/windowsazure", "Name");
                                if (nameElement3 != null) {
                                    String nameInstance3;
                                    nameInstance3 = nameElement3.getTextContent();
                                    dnsServerInstance.setName(nameInstance3);
                                }
                                
                                Element addressElement = XmlUtility.getElementByTagNameNS(dnsServersElement, "http://schemas.microsoft.com/windowsazure", "Address");
                                if (addressElement != null) {
                                    InetAddress addressInstance;
                                    addressInstance = InetAddress.getByName(addressElement.getTextContent());
                                    dnsServerInstance.setAddress(addressInstance);
                                }
                            }
                        }
                    }
                    
                    Element gatewayElement = XmlUtility.getElementByTagNameNS(virtualNetworkSitesElement, "http://schemas.microsoft.com/windowsazure", "Gateway");
                    if (gatewayElement != null) {
                        NetworkListResponse.Gateway gatewayInstance = new NetworkListResponse.Gateway();
                        virtualNetworkSiteInstance.setGateway(gatewayInstance);
                        
                        Element profileElement = XmlUtility.getElementByTagNameNS(gatewayElement, "http://schemas.microsoft.com/windowsazure", "Profile");
                        if (profileElement != null) {
                            GatewayProfile profileInstance;
                            profileInstance = GatewayProfile.valueOf(profileElement.getTextContent());
                            gatewayInstance.setProfile(profileInstance);
                        }
                        
                        Element sitesSequenceElement = XmlUtility.getElementByTagNameNS(gatewayElement, "http://schemas.microsoft.com/windowsazure", "Sites");
                        if (sitesSequenceElement != null) {
                            for (int i5 = 0; i5 < com.microsoft.windowsazure.core.utils.XmlUtility.getElementsByTagNameNS(sitesSequenceElement, "http://schemas.microsoft.com/windowsazure", "LocalNetworkSite").size(); i5 = i5 + 1) {
                                org.w3c.dom.Element sitesElement = ((org.w3c.dom.Element) com.microsoft.windowsazure.core.utils.XmlUtility.getElementsByTagNameNS(sitesSequenceElement, "http://schemas.microsoft.com/windowsazure", "LocalNetworkSite").get(i5));
                                NetworkListResponse.LocalNetworkSite localNetworkSiteInstance = new NetworkListResponse.LocalNetworkSite();
                                gatewayInstance.getSites().add(localNetworkSiteInstance);
                                
                                Element nameElement4 = XmlUtility.getElementByTagNameNS(sitesElement, "http://schemas.microsoft.com/windowsazure", "Name");
                                if (nameElement4 != null) {
                                    String nameInstance4;
                                    nameInstance4 = nameElement4.getTextContent();
                                    localNetworkSiteInstance.setName(nameInstance4);
                                }
                                
                                Element vpnGatewayAddressElement = XmlUtility.getElementByTagNameNS(sitesElement, "http://schemas.microsoft.com/windowsazure", "VpnGatewayAddress");
                                if (vpnGatewayAddressElement != null) {
                                    InetAddress vpnGatewayAddressInstance;
                                    vpnGatewayAddressInstance = InetAddress.getByName(vpnGatewayAddressElement.getTextContent());
                                    localNetworkSiteInstance.setVpnGatewayAddress(vpnGatewayAddressInstance);
                                }
                                
                                Element addressSpaceElement2 = XmlUtility.getElementByTagNameNS(sitesElement, "http://schemas.microsoft.com/windowsazure", "AddressSpace");
                                if (addressSpaceElement2 != null) {
                                    NetworkListResponse.AddressSpace addressSpaceInstance2 = new NetworkListResponse.AddressSpace();
                                    localNetworkSiteInstance.setAddressSpace(addressSpaceInstance2);
                                    
                                    Element addressPrefixesSequenceElement2 = XmlUtility.getElementByTagNameNS(addressSpaceElement2, "http://schemas.microsoft.com/windowsazure", "AddressPrefixes");
                                    if (addressPrefixesSequenceElement2 != null) {
                                        for (int i6 = 0; i6 < com.microsoft.windowsazure.core.utils.XmlUtility.getElementsByTagNameNS(addressPrefixesSequenceElement2, "http://schemas.microsoft.com/windowsazure", "AddressPrefix").size(); i6 = i6 + 1) {
                                            org.w3c.dom.Element addressPrefixesElement2 = ((org.w3c.dom.Element) com.microsoft.windowsazure.core.utils.XmlUtility.getElementsByTagNameNS(addressPrefixesSequenceElement2, "http://schemas.microsoft.com/windowsazure", "AddressPrefix").get(i6));
                                            addressSpaceInstance2.getAddressPrefixes().add(addressPrefixesElement2.getTextContent());
                                        }
                                    }
                                }
                                
                                Element connectionsSequenceElement = XmlUtility.getElementByTagNameNS(sitesElement, "http://schemas.microsoft.com/windowsazure", "Connections");
                                if (connectionsSequenceElement != null) {
                                    for (int i7 = 0; i7 < com.microsoft.windowsazure.core.utils.XmlUtility.getElementsByTagNameNS(connectionsSequenceElement, "http://schemas.microsoft.com/windowsazure", "Connection").size(); i7 = i7 + 1) {
                                        org.w3c.dom.Element connectionsElement = ((org.w3c.dom.Element) com.microsoft.windowsazure.core.utils.XmlUtility.getElementsByTagNameNS(connectionsSequenceElement, "http://schemas.microsoft.com/windowsazure", "Connection").get(i7));
                                        NetworkListResponse.Connection connectionInstance = new NetworkListResponse.Connection();
                                        localNetworkSiteInstance.getConnections().add(connectionInstance);
                                        
                                        Element typeElement = XmlUtility.getElementByTagNameNS(connectionsElement, "http://schemas.microsoft.com/windowsazure", "Type");
                                        if (typeElement != null) {
                                            LocalNetworkConnectionType typeInstance;
                                            typeInstance = NetworkManagementClientImpl.parseLocalNetworkConnectionType(typeElement.getTextContent());
                                            connectionInstance.setType(typeInstance);
                                        }
                                    }
                                }
                            }
                        }
                        
                        Element vPNClientAddressPoolElement = XmlUtility.getElementByTagNameNS(gatewayElement, "http://schemas.microsoft.com/windowsazure", "VPNClientAddressPool");
                        if (vPNClientAddressPoolElement != null) {
                            NetworkListResponse.VPNClientAddressPool vPNClientAddressPoolInstance = new NetworkListResponse.VPNClientAddressPool();
                            gatewayInstance.setVPNClientAddressPool(vPNClientAddressPoolInstance);
                            
                            Element addressPrefixesSequenceElement3 = XmlUtility.getElementByTagNameNS(vPNClientAddressPoolElement, "http://schemas.microsoft.com/windowsazure", "AddressPrefixes");
                            if (addressPrefixesSequenceElement3 != null) {
                                for (int i8 = 0; i8 < com.microsoft.windowsazure.core.utils.XmlUtility.getElementsByTagNameNS(addressPrefixesSequenceElement3, "http://schemas.microsoft.com/windowsazure", "AddressPrefix").size(); i8 = i8 + 1) {
                                    org.w3c.dom.Element addressPrefixesElement3 = ((org.w3c.dom.Element) com.microsoft.windowsazure.core.utils.XmlUtility.getElementsByTagNameNS(addressPrefixesSequenceElement3, "http://schemas.microsoft.com/windowsazure", "AddressPrefix").get(i8));
                                    vPNClientAddressPoolInstance.getAddressPrefixes().add(addressPrefixesElement3.getTextContent());
                                }
                            }
                        }
                    }
                }
            }
            
            result.setStatusCode(statusCode);
            if (httpResponse.getHeaders("x-ms-request-id").length > 0) {
                result.setRequestId(httpResponse.getFirstHeader("x-ms-request-id").getValue());
            }
            
            if (shouldTrace) {
                CloudTracing.exit(invocationId, result);
            }
            return result;
        } finally {
            if (httpResponse != null && httpResponse.getEntity() != null) {
                httpResponse.getEntity().getContent().close();
            }
        }
    }
    
    /**
    * The Set Network Configuration operation asynchronously configures the
    * virtual network  (see
    * http://msdn.microsoft.com/en-us/library/windowsazure/jj157181.aspx for
    * more information)
    *
    * @param parameters Required. The updated network configuration.
    * @return The response body contains the status of the specified
    * asynchronous operation, indicating whether it has succeeded, is
    * inprogress, or has failed. Note that this status is distinct from the
    * HTTP status code returned for the Get Operation Status operation itself.
    * If the asynchronous operation succeeded, the response body includes the
    * HTTP status code for the successful request.  If the asynchronous
    * operation failed, the response body includes the HTTP status code for
    * the failed request, and also includes error information regarding the
    * failure.
    */
    @Override
    public Future<OperationStatusResponse> setConfigurationAsync(final NetworkSetConfigurationParameters parameters) {
        return this.getClient().getExecutorService().submit(new Callable<OperationStatusResponse>() { 
            @Override
            public OperationStatusResponse call() throws Exception {
                return setConfiguration(parameters);
            }
         });
    }
    
    /**
    * The Set Network Configuration operation asynchronously configures the
    * virtual network  (see
    * http://msdn.microsoft.com/en-us/library/windowsazure/jj157181.aspx for
    * more information)
    *
    * @param parameters Required. The updated network configuration.
    * @throws InterruptedException Thrown when a thread is waiting, sleeping,
    * or otherwise occupied, and the thread is interrupted, either before or
    * during the activity. Occasionally a method may wish to test whether the
    * current thread has been interrupted, and if so, to immediately throw
    * this exception. The following code can be used to achieve this effect:
    * @throws ExecutionException Thrown when attempting to retrieve the result
    * of a task that aborted by throwing an exception. This exception can be
    * inspected using the Throwable.getCause() method.
    * @throws ServiceException Thrown if the server returned an error for the
    * request.
    * @throws IOException Thrown if there was an error setting up tracing for
    * the request.
    * @return The response body contains the status of the specified
    * asynchronous operation, indicating whether it has succeeded, is
    * inprogress, or has failed. Note that this status is distinct from the
    * HTTP status code returned for the Get Operation Status operation itself.
    * If the asynchronous operation succeeded, the response body includes the
    * HTTP status code for the successful request.  If the asynchronous
    * operation failed, the response body includes the HTTP status code for
    * the failed request, and also includes error information regarding the
    * failure.
    */
    @Override
    public OperationStatusResponse setConfiguration(NetworkSetConfigurationParameters parameters) throws InterruptedException, ExecutionException, ServiceException, IOException {
        NetworkManagementClient client2 = this.getClient();
        boolean shouldTrace = CloudTracing.getIsEnabled();
        String invocationId = null;
        if (shouldTrace) {
            invocationId = Long.toString(CloudTracing.getNextInvocationId());
            HashMap<String, Object> tracingParameters = new HashMap<String, Object>();
            tracingParameters.put("parameters", parameters);
            CloudTracing.enter(invocationId, this, "setConfigurationAsync", tracingParameters);
        }
        try {
            if (shouldTrace) {
                client2 = this.getClient().withRequestFilterLast(new ClientRequestTrackingHandler(invocationId)).withResponseFilterLast(new ClientRequestTrackingHandler(invocationId));
            }
            
            OperationResponse response = client2.getNetworksOperations().beginSettingConfigurationAsync(parameters).get();
            OperationStatusResponse result = client2.getOperationStatusAsync(response.getRequestId()).get();
            int delayInSeconds = 30;
            while ((result.getStatus() != OperationStatus.InProgress) == false) {
                Thread.sleep(delayInSeconds * 1000);
                result = client2.getOperationStatusAsync(response.getRequestId()).get();
                delayInSeconds = 30;
            }
            
            if (shouldTrace) {
                CloudTracing.exit(invocationId, result);
            }
            
            if (result.getStatus() != OperationStatus.Succeeded) {
                if (result.getError() != null) {
                    ServiceException ex = new ServiceException(result.getError().getCode() + " : " + result.getError().getMessage());
                    ex.setErrorCode(result.getError().getCode());
                    ex.setErrorMessage(result.getError().getMessage());
                    if (shouldTrace) {
                        CloudTracing.error(invocationId, ex);
                    }
                    throw ex;
                } else {
                    ServiceException ex = new ServiceException("");
                    if (shouldTrace) {
                        CloudTracing.error(invocationId, ex);
                    }
                    throw ex;
                }
            }
            
            return result;
        } finally {
            if (client2 != null && shouldTrace) {
                client2.close();
            }
        }
    }
}
