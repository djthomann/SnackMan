import mitt from 'mitt';

/**
 * Service that 
 */

const eventBus = mitt<{ serverMessage: string }>();

export default eventBus;