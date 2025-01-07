export class Logger {
  private logLevel: 'debug' | 'info' | 'warn' | 'error' = 'debug';

  constructor() {}

  // Überprüft, ob die aktuelle Log-Ebene das gewünschte Log-Level zulässt
  private shouldLog(level: 'debug' | 'info' | 'warn' | 'error'): boolean {
    const levels: { [key in 'debug' | 'info' | 'warn' | 'error']: number } = {
      debug: 0,
      info: 1,
      warn: 2,
      error: 3,
    };
    return levels[level] >= levels[this.logLevel];
  }

  // Debug-Log
  public debug(message: string, ...args: any[]): void {
    if (this.shouldLog('debug')) {
      console.debug('[DEBUG]', message, ...args);
    }
  }

  // Info-Log
  public info(message: string, ...args: any[]): void {
    if (this.shouldLog('info')) {
      console.info('[INFO]', message, ...args);
    }
  }

  // Warn-Log
  public warn(message: string, ...args: any[]): void {
    if (this.shouldLog('warn')) {
      console.warn('[WARN]', message, ...args);
    }
  }

  // Error-Log
  public error(message: string, ...args: any[]): void {
    if (this.shouldLog('error')) {
      console.error('[ERROR]', message, ...args);
    }
  }
}
