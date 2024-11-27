import mitt from 'mitt';

/**
 * Service that can be subribed to, emits an event to listeners
 * Used by socketService to inform subscribers that a server message has arrived
 */

const eventBus = mitt<{ serverMessage: string }>();

export default eventBus;