// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.messaging.eventhubs;

import com.azure.core.amqp.implementation.AmqpReceiveLink;
import com.azure.core.amqp.implementation.MessageSerializer;
import com.azure.core.amqp.implementation.StringUtil;
import com.azure.core.annotation.ReturnType;
import com.azure.core.annotation.ServiceClient;
import com.azure.core.annotation.ServiceMethod;
import com.azure.core.util.logging.ClientLogger;
import com.azure.messaging.eventhubs.implementation.EventHubManagementNode;
import com.azure.messaging.eventhubs.models.EventPosition;
import com.azure.messaging.eventhubs.models.PartitionContext;
import com.azure.messaging.eventhubs.models.PartitionEvent;
import com.azure.messaging.eventhubs.models.ReceiveOptions;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Closeable;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.azure.core.util.FluxUtil.fluxError;
import static com.azure.core.util.FluxUtil.monoError;

/**
 * An <b>asynchronous</b> consumer responsible for reading {@link EventData} from either a specific Event Hub partition
 * or all partitions in the context of a specific consumer group.
 *
 * <p><strong>Creating an {@link EventHubConsumerAsyncClient}</strong></p>
 * <p>Required parameters are {@code consumerGroup}, and credentials are required when
 * creating a consumer.</p> {@codesnippet com.azure.messaging.eventhubs.eventhubconsumerasyncclient.instantiation}
 *
 * <p><strong>Consuming events a single partition from Event Hub</strong></p>
 * {@codesnippet com.azure.messaging.eventhubs.eventhubconsumerasyncclient.receive#string-eventposition}
 *
 * <p><strong>Rate limiting consumption of events from Event Hub</strong></p>
 * <p>For event consumers that need to limit the number of events they receive at a given time, they can use {@link
 * BaseSubscriber#request(long)}.</p> {@codesnippet com.azure.messaging.eventhubs.eventhubconsumerasyncclient.receive#string-eventposition-basesubscriber}
 *
 * <p><strong>Viewing latest partition information</strong></p>
 * <p>Latest partition information as events are received can by setting
 * {@link ReceiveOptions#setTrackLastEnqueuedEventProperties(boolean) setTrackLastEnqueuedEventProperties} to {@code
 * true}. As events come in, explore the {@link PartitionContext} object. {@codesnippet
 * com.azure.messaging.eventhubs.eventhubconsumerasyncclient.receive#boolean-receiveoptions}
 *
 * <p><strong>Receiving from all partitions</strong></p>
 * {@codesnippet com.azure.messaging.eventhubs.eventhubconsumerasyncclient.receive#boolean}
 */
@ServiceClient(builder = EventHubClientBuilder.class, isAsync = true)
public class EventHubConsumerAsyncClient implements Closeable {
    private static final String RECEIVER_ENTITY_PATH_FORMAT = "%s/ConsumerGroups/%s/Partitions/%s";

    private final AtomicBoolean isDisposed = new AtomicBoolean();
    private final ReceiveOptions defaultReceiveOptions = new ReceiveOptions();
    private final ClientLogger logger = new ClientLogger(EventHubConsumerAsyncClient.class);
    private final String fullyQualifiedNamespace;
    private final String eventHubName;
    private final EventHubConnection connection;
    private final MessageSerializer messageSerializer;
    private final String consumerGroup;
    private final int prefetchCount;
    private final boolean isSharedConnection;
    /**
     * Keeps track of the open partition consumers keyed by linkName. The link name is generated as: {@code
     * "partitionId_GUID"}. For receiving from all partitions, links are prefixed with {@code "all-GUID-partitionId"}.
     */
    private final ConcurrentHashMap<String, EventHubPartitionAsyncConsumer> openPartitionConsumers =
        new ConcurrentHashMap<>();

    EventHubConsumerAsyncClient(String fullyQualifiedNamespace, String eventHubName, EventHubConnection connection,
        MessageSerializer messageSerializer, String consumerGroup, int prefetchCount, boolean isSharedConnection) {
        this.fullyQualifiedNamespace = fullyQualifiedNamespace;
        this.eventHubName = eventHubName;
        this.connection = connection;
        this.messageSerializer = messageSerializer;
        this.consumerGroup = consumerGroup;
        this.prefetchCount = prefetchCount;
        this.isSharedConnection = isSharedConnection;
    }

    /**
     * Gets the fully qualified Event Hubs namespace that the connection is associated with. This is likely similar to
     * {@code {yournamespace}.servicebus.windows.net}.
     *
     * @return The fully qualified Event Hubs namespace that the connection is associated with
     */
    public String getFullyQualifiedNamespace() {
        return fullyQualifiedNamespace;
    }

    /**
     * Gets the Event Hub name this client interacts with.
     *
     * @return The Event Hub name this client interacts with.
     */
    public String getEventHubName() {
        return eventHubName;
    }

    /**
     * Gets the consumer group this consumer is reading events as a part of.
     *
     * @return The consumer group this consumer is reading events as a part of.
     */
    public String getConsumerGroup() {
        return consumerGroup;
    }

    /**
     * Retrieves information about an Event Hub, including the number of partitions present and their identifiers.
     *
     * @return The set of information for the Event Hub that this client is associated with.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Mono<EventHubProperties> getEventHubProperties() {
        return connection.getManagementNode().flatMap(EventHubManagementNode::getEventHubProperties);
    }

    /**
     * Retrieves the identifiers for the partitions of an Event Hub.
     *
     * @return A Flux of identifiers for the partitions of an Event Hub.
     */
    public Flux<String> getPartitionIds() {
        return getEventHubProperties().flatMapMany(properties -> Flux.fromIterable(properties.getPartitionIds()));
    }

    /**
     * Retrieves information about a specific partition for an Event Hub, including elements that describe the available
     * events in the partition event stream.
     *
     * @param partitionId The unique identifier of a partition associated with the Event Hub.
     *
     * @return The set of information for the requested partition under the Event Hub this client is associated with.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Mono<PartitionProperties> getPartitionProperties(String partitionId) {
        if (Objects.isNull(partitionId)) {
            return monoError(logger, new NullPointerException("'partitionId' cannot be null."));
        } else if (partitionId.isEmpty()) {
            return monoError(logger, new IllegalArgumentException("'partitionId' cannot be an empty string."));
        }

        return connection.getManagementNode().flatMap(node -> node.getPartitionProperties(partitionId));
    }

    /**
     * Consumes events from a single partition starting at {@code startingPosition}.
     *
     * @param partitionId Identifier of the partition to read events from.
     * @param startingPosition Position within the Event Hub partition to begin consuming events.
     *
     * @return A stream of events for this partition starting from {@code startingPosition}.
     *
     * @throws NullPointerException if {@code partitionId}, or {@code startingPosition} is null.
     * @throws IllegalArgumentException if {@code partitionId} is an empty string.
     */
    public Flux<PartitionEvent> receiveFromPartition(String partitionId, EventPosition startingPosition) {
        return receiveFromPartition(partitionId, startingPosition, defaultReceiveOptions);
    }

    /**
     * Consumes events from a single partition starting at {@code startingPosition} with a set of {@link ReceiveOptions
     * receive options}.
     *
     * <ul>
     * <li>If receive is invoked where {@link ReceiveOptions#getOwnerLevel()} has a value, then Event Hubs service will
     * guarantee only one active consumer exists per partitionId and consumer group combination. This consumer is
     * sometimes referred to as an "Epoch Consumer."</li>
     * <li>Multiple consumers per partitionId and consumer group combination can be created by not setting
     * {@link ReceiveOptions#getOwnerLevel()} when creating consumers. This non-exclusive consumer is sometimes
     * referred to as a "Non-Epoch Consumer."</li>
     * </ul>
     *
     * @param partitionId Identifier of the partition to read events from.
     * @param startingPosition Position within the Event Hub partition to begin consuming events.
     * @param receiveOptions Options when receiving events from the partition.
     *
     * @return A stream of events for this partition. If a stream for the events was opened before, the same position
     *     within that partition is returned. Otherwise, events are read starting from {@code startingPosition}.
     *
     * @throws NullPointerException if {@code partitionId}, {@code startingPosition}, {@code receiveOptions} is
     *     null.
     * @throws IllegalArgumentException if {@code partitionId} is an empty string.
     */
    public Flux<PartitionEvent> receiveFromPartition(String partitionId, EventPosition startingPosition,
        ReceiveOptions receiveOptions) {
        if (Objects.isNull(partitionId)) {
            return fluxError(logger, new NullPointerException("'partitionId' cannot be null."));
        } else if (partitionId.isEmpty()) {
            return fluxError(logger, new IllegalArgumentException("'partitionId' cannot be an empty string."));
        }

        if (Objects.isNull(startingPosition)) {
            return fluxError(logger, new NullPointerException("'startingPosition' cannot be null."));
        }

        final String linkName = StringUtil.getRandomString(partitionId);
        return createConsumer(linkName, partitionId, startingPosition, receiveOptions);
    }

    /**
     * Consumes events from all partitions starting from the beginning of each partition.
     *
     * <p>
     * This method is not recommended for production use; the {@link EventProcessorClient} should be used for reading
     * events from all partitions in a production scenario, as it offers a much more robust experience with higher
     * throughput.
     *
     * It is important to note that this method does not guarantee fairness amongst the partitions. Depending on service
     * communication, there may be a clustering of events per partition and/or there may be a noticeable bias for a
     * given partition or subset of partitions.
     * </p>
     *
     * @return A stream of events for every partition in the Event Hub starting from {@code startingPosition}.
     */
    public Flux<PartitionEvent> receive() {
        return receive(true, defaultReceiveOptions);
    }

    /**
     * Consumes events from all partitions.
     *
     * <p>
     * This method is not recommended for production use; the {@link EventProcessorClient} should be used for reading
     * events from all partitions in a production scenario, as it offers a much more robust experience with higher
     * throughput.
     *
     * It is important to note that this method does not guarantee fairness amongst the partitions. Depending on service
     * communication, there may be a clustering of events per partition and/or there may be a noticeable bias for a
     * given partition or subset of partitions.
     * </p>
     *
     * @param startReadingAtEarliestEvent {@code true} to begin reading at the first events available in each
     *     partition; otherwise, reading will begin at the end of each partition seeing only new events as they are
     *     published.
     *
     * @return A stream of events for every partition in the Event Hub.
     */
    public Flux<PartitionEvent> receive(boolean startReadingAtEarliestEvent) {
        return receive(startReadingAtEarliestEvent, defaultReceiveOptions);
    }

    /**
     * Consumes events from all partitions.
     *
     * <p>
     * This method is not recommended for production use; the {@link EventProcessorClient} should be used for reading
     * events from all partitions in a production scenario, as it offers a much more robust experience with higher
     * throughput.
     *
     * It is important to note that this method does not guarantee fairness amongst the partitions. Depending on service
     * communication, there may be a clustering of events per partition and/or there may be a noticeable bias for a
     * given partition or subset of partitions.
     * </p>
     *
     * @param startReadingAtEarliestEvent {@code true} to begin reading at the first events available in each
     *     partition; otherwise, reading will begin at the end of each partition seeing only new events as they are
     *     published.
     * @param receiveOptions Options when receiving events from each Event Hub partition.
     *
     * @return A stream of events for every partition in the Event Hub.
     *
     * @throws NullPointerException if {@code receiveOptions} is null.
     */
    public Flux<PartitionEvent> receive(boolean startReadingAtEarliestEvent, ReceiveOptions receiveOptions) {
        if (Objects.isNull(receiveOptions)) {
            return fluxError(logger, new NullPointerException("'receiveOptions' cannot be null."));
        }

        final EventPosition startingPosition = startReadingAtEarliestEvent
            ? EventPosition.earliest()
            : EventPosition.latest();
        final String prefix = StringUtil.getRandomString("all");
        final Flux<PartitionEvent> allPartitionEvents = getPartitionIds().flatMap(partitionId -> {
            final String linkName = prefix + "-" + partitionId;

            return createConsumer(linkName, partitionId, startingPosition, receiveOptions);
        });

        return Flux.merge(allPartitionEvents);
    }

    /**
     * Disposes of the consumer by closing the underlying connection to the service.
     */
    @Override
    public void close() {
        if (isDisposed.getAndSet(true)) {
            return;
        }
        openPartitionConsumers.forEach((key, value) -> value.close());
        openPartitionConsumers.clear();

        if (!isSharedConnection) {
            connection.close();
        }
    }

    private Flux<PartitionEvent> createConsumer(String linkName, String partitionId, EventPosition startingPosition,
        ReceiveOptions receiveOptions) {
        return openPartitionConsumers
            .computeIfAbsent(linkName, name -> {
                logger.info("{}: Creating receive consumer for partition '{}'", linkName, partitionId);
                return createPartitionConsumer(name, partitionId, startingPosition, receiveOptions);
            })
            .receive()
            .doFinally(signalType -> {
                logger.info("{}: Receiving completed. Partition: '{}'. Signal: '{}'", linkName, partitionId,
                    signalType);
                final EventHubPartitionAsyncConsumer consumer = openPartitionConsumers.remove(linkName);

                if (consumer != null) {
                    consumer.close();
                }
            });
    }

    private EventHubPartitionAsyncConsumer createPartitionConsumer(String linkName, String partitionId,
        EventPosition startingPosition, ReceiveOptions receiveOptions) {
        final String entityPath = String.format(Locale.US, RECEIVER_ENTITY_PATH_FORMAT,
            getEventHubName(), consumerGroup, partitionId);

        final Mono<AmqpReceiveLink> receiveLinkMono =
            connection.createReceiveLink(linkName, entityPath, startingPosition, receiveOptions)
                .doOnNext(next -> logger.verbose("Creating consumer for path: {}", next.getEntityPath()));

        return new EventHubPartitionAsyncConsumer(receiveLinkMono, messageSerializer, getFullyQualifiedNamespace(),
            getEventHubName(), consumerGroup, partitionId, prefetchCount,
            receiveOptions.getTrackLastEnqueuedEventProperties());
    }
}
